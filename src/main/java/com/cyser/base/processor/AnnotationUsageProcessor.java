package com.cyser.base.processor;

import com.cyser.base.annotations.Copy_Exclude;
import com.cyser.base.annotations.Copy_Include;
import com.google.auto.service.AutoService;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.cyser.base.annotations.Copy_Include", "com.cyser.base.annotations.Copy_Exclude"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationUsageProcessor extends AbstractProcessor {

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Copy_Include.class)) {
            if (element.getAnnotation(Copy_Exclude.class) != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot use Copy_Include and Copy_Exclude together", element);
            }
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Copy_Exclude.class)) {
            if (element.getAnnotation(Copy_Include.class) != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot use Copy_Exclude and Copy_Include together", element);
            }
        }
        return true;
    }
}