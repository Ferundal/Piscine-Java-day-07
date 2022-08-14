package edu.school21.cjettie.repositories;

import com.google.auto.service.AutoService;
import edu.school21.cjettie.annotations.OrmColumn;
import edu.school21.cjettie.annotations.OrmColumnId;
import edu.school21.cjettie.annotations.OrmEntity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("edu.school21.cjettie.annotations.OrmEntity")
@AutoService(Processor.class)
public class OrmManager extends AbstractProcessor {
    private DataSource dataSource;

    public OrmManager() {}
    public OrmManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Object entity) {
        if (entity.getClass().isAnnotationPresent(OrmEntity.class)) {
            OrmEntity ormEntity =  entity.getClass().getAnnotation(OrmEntity.class);
            String QUERY_TEMPLATE = "INSERT INTO " + ormEntity.table() + " (";
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    QUERY_TEMPLATE += ormColumn.name();
                    if (field != fields[fields.length - 1]) {
                        QUERY_TEMPLATE += ", ";
                    }
                }
            }
            QUERY_TEMPLATE += ") VALUES (";
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    field.setAccessible(true);
                    try {
                        if (field.getType().getSimpleName().toLowerCase().equals("string")) {
                            QUERY_TEMPLATE += String.format("\'%s\'", field.get(entity));
                        } else {
                            QUERY_TEMPLATE += field.get(entity);
                        }
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        throw new RuntimeException("You are mistaken");
                    }
                    if (field != fields[fields.length - 1]) {
                        QUERY_TEMPLATE += ", ";
                    }
                }
            }
            QUERY_TEMPLATE += ")";
            Connection connection;
            try {
                connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(QUERY_TEMPLATE);
                query.execute();
            }
            catch (SQLException sqlException) {
                throw new RuntimeException("Problems while adding to database");
            }
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("CALL IDENTITY()");
                resultSet.next();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(OrmColumnId.class)) {
                        field.setAccessible(true);
                        field.set(entity, resultSet.getLong(1));
                    }
                }
                connection.close();
            } catch (SQLException sqlException) {
                throw new RuntimeException("Problems with setting id in \"save\"");
            } catch (IllegalAccessException illegalAccessException) {
                throw new RuntimeException("Can't put id in object in \"save\"");
            }
        }
    }

    public void update(Object entity) {
        if (entity.getClass().isAnnotationPresent(OrmEntity.class)) {
            OrmEntity ormEntity =  entity.getClass().getAnnotation(OrmEntity.class);
            String QUERY_TEMPLATE = "INSERT INTO " + ormEntity.table() + " (";
            QUERY_TEMPLATE = "UPDATE " + ormEntity.table() + " SET ";
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    QUERY_TEMPLATE += ormColumn.name() + "=";
                    field.setAccessible(true);
                    try {
                        if (field.getType().getSimpleName().toLowerCase().equals("string")) {
                            QUERY_TEMPLATE += String.format("\'%s\'", field.get(entity));
                        } else {
                            QUERY_TEMPLATE += field.get(entity);
                        }
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        throw new RuntimeException("You are mistaken");
                    }
                    if (field != fields[fields.length - 1]) {
                        QUERY_TEMPLATE += ", ";
                    }
                }
            }
            QUERY_TEMPLATE += " WHERE id=";
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    field.setAccessible(true);
                    try {
                        QUERY_TEMPLATE += field.get(entity);
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        throw new RuntimeException("You are mistaken");
                    }
                    break;
                }
            }
            System.err.println(QUERY_TEMPLATE);
            Connection connection;
            try {
                connection = dataSource.getConnection();
                PreparedStatement query = connection.prepareStatement(QUERY_TEMPLATE);
                query.execute();
            }
            catch (SQLException sqlException) {
                throw new RuntimeException("Problems while adding to database");
            }
        }
    }

    public <T> T findById(Long id, Class<T> aClass) {
        T result = null;
        if (aClass.isAnnotationPresent(OrmEntity.class)) {
            OrmEntity ormEntity =  aClass.getAnnotation(OrmEntity.class);
            String QUERY_TEMPLATE = "SELECT * FROM " + ormEntity.table() + " WHERE id = " + id;
            Connection connection;
            ResultSet resultSet;
            try {
                connection = dataSource.getConnection();
                resultSet = connection.createStatement().executeQuery(QUERY_TEMPLATE);
                resultSet.next();
            } catch (SQLException sqlException) {
                throw new RuntimeException("Problems while adding to database");
            }
            Constructor [] constructors = aClass.getConstructors();
            Constructor constructor = null;
            for (int counter = 0; counter < constructors.length; ++counter) {
                if (constructors[counter].getParameterTypes().length == 0) {
                    constructor = constructors[counter];
                    break;
                }
            }
            if (constructor == null) {
                throw new RuntimeException("No constructors with parameters");
            }
            try {
                result = (T)constructor.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            Field[] fields = result.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    field.setAccessible(true);
                    try {
                        Object fieldValue;
                        String fieldTypeName = field.getType().getTypeName();
                        if (fieldTypeName.equals(int.class.getTypeName()) || fieldTypeName.equals(Integer.class.getTypeName())) {
                            fieldValue = resultSet.getInt(ormColumn.name());
                        } else if (fieldTypeName.equals(long.class.getTypeName()) || fieldTypeName.equals(Long.class.getTypeName())) {
                            fieldValue = resultSet.getLong(ormColumn.name());
                        } else if (fieldTypeName.equals(double.class.getTypeName()) || fieldTypeName.equals(Double.class.getTypeName())) {
                            fieldValue = resultSet.getDouble(ormColumn.name());
                        } else if (fieldTypeName.equals(boolean.class.getTypeName())) {
                            fieldValue = resultSet.getBoolean(ormColumn.name());
                        } else {
                            fieldValue = resultSet.getString(ormColumn.name());
                        }
                        field.set(result, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    field.setAccessible(true);
                    try {
                        field.set(result, resultSet.getLong("id"));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedHtmlFormElements = roundEnv.getElementsAnnotatedWith(OrmEntity.class);
        if (annotatedHtmlFormElements.size() > 0) {
            createSchemaSQL(annotatedHtmlFormElements);
        }
        return false;
    }

    private static void createSchemaSQL(Set<? extends Element> annotatedOrmEntityElements) {
        FileWriter schema;
        try {
            new File("./src/main/resources/").mkdirs();
            schema = new FileWriter("./src/main/resources/schema.sql");
            schema.write("DROP TABLE IF EXISTS ");
            for (Element annotatedOrmEntityElement : annotatedOrmEntityElements) {
                schema.write(annotatedOrmEntityElement.getAnnotation(OrmEntity.class).table());
                if (annotatedOrmEntityElement != annotatedOrmEntityElements.toArray()[annotatedOrmEntityElements.size() - 1]) {
                    schema.write(", ");
                }
            }
            schema.write(" CASCADE;\n\n");
            for (Element annotatedOrmEntityElement : annotatedOrmEntityElements) {
                createTable(annotatedOrmEntityElement, schema);
                if (annotatedOrmEntityElement != annotatedOrmEntityElements.toArray()[annotatedOrmEntityElements.size() - 1]) {
                    schema.write(";");
                }
            }
            schema.flush();
            schema.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTable(Element annotatedOrmEntityElement, FileWriter schema) throws IOException {
        OrmEntity ormEntity = annotatedOrmEntityElement.getAnnotation(OrmEntity.class);
        schema.write("CREATE TABLE IF NOT EXISTS ");
        schema.write(annotatedOrmEntityElement.getAnnotation(OrmEntity.class).table() + " (\n");
        List<? extends Element> annotatedOrmEntityElementEnclosedElements = annotatedOrmEntityElement.getEnclosedElements();
        OrmColumnId ormColumnId = null;
        boolean isFirst = true;
        for (Element annotatedOrmEntityElementEnclosedElement : annotatedOrmEntityElementEnclosedElements) {
            if (ormColumnId == null) {
                ormColumnId = annotatedOrmEntityElementEnclosedElement.getAnnotation(OrmColumnId.class);
                if (ormColumnId != null) {
                    ormColumIdLine(schema);
                }
            } else {
                OrmColumn ormColumn = annotatedOrmEntityElementEnclosedElement.getAnnotation(OrmColumn.class);
                if (ormColumn != null) {
                    if (!isFirst) {
                        schema.write(",\n");
                    }
                    ormColumnLine(annotatedOrmEntityElementEnclosedElement, schema);
                    isFirst = false;
                }
            }
        }
        schema.write("\n);");
    }

    private static void ormColumIdLine(FileWriter schema) throws IOException {
        schema.write("\tid INTEGER IDENTITY PRIMARY KEY,\n");
    }
    private static void ormColumnLine(Element annotatedOrmColumnElement, FileWriter schema) throws IOException {
        OrmColumn ormColumn = annotatedOrmColumnElement.getAnnotation(OrmColumn.class);
        schema.write(String.format("\t%s ", ormColumn.name()));
        String fieldType = annotatedOrmColumnElement.asType().toString();
        if (fieldType.equals(int.class.getTypeName()) || fieldType.equals(Integer.class.getTypeName())) {
            schema.write("INT");
        } else if (fieldType.equals(long.class.getTypeName()) || fieldType.equals(Long.class.getTypeName())) {
            schema.write("INTEGER");
        } else if (fieldType.equals(double.class.getTypeName()) || fieldType.equals(Double.class.getTypeName())) {
            schema.write("FLOAT");
        } else if (fieldType.equals(boolean.class.getTypeName())) {
            schema.write("BOOLEAN");
        } else if (fieldType.equals(String.class.getTypeName())
                && ormColumn.length() != -1) {
            schema.write("VARCHAR(10) NOT NULL");
        }
    }
}
