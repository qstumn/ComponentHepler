package q.rorbin.component.annotation.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import q.rorbin.component.ComponentConst;
import q.rorbin.component.annotation.ComponentInitializator;
import q.rorbin.component.annotation.compiler.base.BaseProcessor;
import q.rorbin.component.interfaces.IComponent;
import q.rorbin.component.model.AnnotationEntity;

/**
 * @author changhai.qiu
 */
public class ComponentInitProcessor extends BaseProcessor {
    private final Class<ComponentInitializator> annotationClass = ComponentInitializator.class;
    private final ClassName exceptionClassName = ClassName.get(Exception.class);
    private final ClassName iComponentClassName = ClassName.get(IComponent.class);
    private final ClassName keepClassName = ClassName.bestGuess("androidx.annotation.Keep");

    @Override
    protected Class<? extends Annotation>[] getSupportedAnnotations() {
        return new Class[]{annotationClass};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {

        String packageName = annotationClass.getPackage().getName();

        Map<String, Map<Class<? extends Annotation>, List<AnnotationEntity>>> annotations = gatherSupportAnnotations(roundEnv);
        for (Map.Entry<String, Map<Class<? extends Annotation>, List<AnnotationEntity>>> entry : annotations.entrySet()) {
            Map<Class<? extends Annotation>, List<AnnotationEntity>> value = entry.getValue();
            if (value == null || value.isEmpty()) {
                continue;
            }
            List<AnnotationEntity> componentInitializatorAnnotation = value.get(annotationClass);
            if (componentInitializatorAnnotation == null || componentInitializatorAnnotation.isEmpty()) {
                continue;
            }
            for (AnnotationEntity annotation : componentInitializatorAnnotation) {
                String className = annotation.getHostClassName();
                String simpleName = annotation.getHostClassSimpleName();
                //create ComponentInitHelper.java
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(ComponentConst.PREFIX + simpleName + ComponentConst.INITIALIZATOR_HEPLER_SUFFIX)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(keepClassName)
                        .addJavadoc("@author changhai qiu\n")
                        .addJavadoc("This class is automatically generated using the annotation processor." +
                                " The code in the class cannot be modified. The detailed usage is as follows\n")
                        .addJavadoc("@see " + annotationClass.getName() + "\n");
                //create constructor and invoke initializtor
                TypeName initializatorTypeName = ClassName.get(annotation.getHostTypeMirror());
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                        .beginControlFlow("try")
                        .addStatement("$T obj = new $T()", initializatorTypeName, initializatorTypeName)
                        .beginControlFlow("if(obj instanceof $T)", iComponentClassName)
                        .addStatement("(($T) obj).onInit()")
                        .endControlFlow()
                        .nextControlFlow("catch ($T e)", exceptionClassName)
                        .addStatement("e.printStackTrace()")
                        .endControlFlow();
                classBuilder.addMethod(constructorBuilder.build());
                try {
                    JavaFile.builder(packageName, classBuilder.build()).build().writeTo(mFiler);
                } catch (IOException e) {
                }
            }
        }

        return true;
    }
}
