package edu.school21.cjettie.annotations;

import com.google.auto.service.AutoService;

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

@SupportedAnnotationTypes("edu.school21.cjettie.annotations.HtmlForm")
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedHtmlFormElements = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element annotatedHtmlFormElement : annotatedHtmlFormElements) {
            printForm(annotatedHtmlFormElement);
        }
        return false;
    }

    private static void printForm(Element annotatedHtmlFormElement) {
        HtmlForm htmlForm = annotatedHtmlFormElement.getAnnotation(HtmlForm.class);
        FileWriter formFile;
        try {
            formFile = new FileWriter(htmlForm.fileName());
            formFile.write(String.format("<form action = \"%s\" method = \"%s\">\n",
                    htmlForm.action(), htmlForm.method()));
            List<? extends Element> annotatedHtmlFormElementEnclosedElements = annotatedHtmlFormElement.getEnclosedElements();
            for (Element annotatedHtmlFormElementEnclosedElement : annotatedHtmlFormElementEnclosedElements) {
                HtmlInput htmlInput = annotatedHtmlFormElementEnclosedElement.getAnnotation(HtmlInput.class);
                if (htmlInput != null) {
                    formFile.write(String.format("\t<input type = \"%s\" name = \"%s\" placeholder = \"%s\">\n",
                            htmlInput.type(), htmlInput.name(), htmlInput.placeholder()));
                }
            }
            formFile.write("\t<input type = \"submit\" value = \"Send\">\n");
            formFile.write("</form>\n");
            formFile.flush();
            formFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
