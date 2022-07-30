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
import java.lang.reflect.Field;
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
        Set<? extends Element> annotatedHtmlFormElements = entity.
    }

    public void update(Object entity) {
        
    }

    public <T> T findById(Long id, Class<T> aClass) {
        return null;
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
        for (Element annotatedOrmEntityElementEnclosedElement : annotatedOrmEntityElementEnclosedElements) {
            if (ormColumnId == null) {
                ormColumnId = annotatedOrmEntityElementEnclosedElement.getAnnotation(OrmColumnId.class);
                if (ormColumnId != null) {
                    ormColumIdLine(schema);
                }
            }
            OrmColumn ormColumn = annotatedOrmEntityElementEnclosedElement.getAnnotation(OrmColumn.class);
            if (ormColumn != null) {
                ormColumnLine(annotatedOrmEntityElementEnclosedElement, schema);
                if (annotatedOrmEntityElementEnclosedElement != annotatedOrmEntityElementEnclosedElements.get(annotatedOrmEntityElementEnclosedElements.size() - 1)) {
                    schema.write(",\n");
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
