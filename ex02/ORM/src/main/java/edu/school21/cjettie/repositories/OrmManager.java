package edu.school21.cjettie.repositories;

import com.google.auto.service.AutoService;
import edu.school21.cjettie.annotations.OrmColumn;
import edu.school21.cjettie.annotations.OrmEntity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("edu.school21.cjettie.annotations.OrmEntity")
@AutoService(Processor.class)
public class OrmManager extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedHtmlFormElements = roundEnv.getElementsAnnotatedWith(OrmEntity.class);
        if (annotatedHtmlFormElements.size() > 0) {
            createSchemaSQL(annotatedHtmlFormElements);
        }
        return false;
    }

    private static void createSchemaSQL(Set<? extends Element> annotatedHtmlFormElements) {
        FileWriter schema;
        try {
            schema = new FileWriter("schema.sql");
            schema.write("CREATE SCHEMA if NOT EXISTS chat;\n\n");
            schema.write("DROP TABLE if EXISTS ");
            schema.flush();
            for (Element annotatedHtmlFormElement : annotatedHtmlFormElements) {
                schema.write(annotatedHtmlFormElement.getAnnotation(OrmEntity.class).table());
                if (annotatedHtmlFormElement != annotatedHtmlFormElements.toArray()[annotatedHtmlFormElements.size() - 1]) {
                    schema.write(" ,");
                }
            }
            schema.write(";\n");
            for (Element annotatedHtmlFormElement : annotatedHtmlFormElements) {
                createTable(annotatedHtmlFormElement, schema);
                if (annotatedHtmlFormElement != annotatedHtmlFormElements.toArray()[annotatedHtmlFormElements.size() - 1]) {
                    schema.write(";");
                }
            }
            schema.flush();
            schema.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTable(Element annotatedHtmlFormElement, FileWriter schema) throws IOException {
        OrmEntity ormEntity = annotatedHtmlFormElement.getAnnotation(OrmEntity.class);
        schema.write("CREATE TABLE if NOT EXISTS ");
        schema.write(annotatedHtmlFormElement.getAnnotation(OrmEntity.class).table() + " (");
        List<? extends Element> annotatedHtmlFormElementEnclosedElements = annotatedHtmlFormElement.getEnclosedElements();
        for (Element annotatedHtmlFormElementEnclosedElement : annotatedHtmlFormElementEnclosedElements) {
            OrmColumn ormColumn = annotatedHtmlFormElementEnclosedElement.getAnnotation(OrmColumn.class);
            if (ormColumn != null) {
                columnWriter(annotatedHtmlFormElementEnclosedElement, schema);
            }
            schema.write(annotatedHtmlFormElement.getAnnotation(OrmEntity.class).table() + ");\n\n");
        }
    }

    private static void columnWriter(Element annotatedHtmlFormElementEnclosedElement, FileWriter schema) throws IOException {
        OrmColumn ormColumn = annotatedHtmlFormElementEnclosedElement.getAnnotation(OrmColumn.class);
        schema.write(String.format("\t%s ", ormColumn.name()));
    }
}
