package q.rorbin.component.annotation.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import q.rorbin.component.ComponentConst;
import q.rorbin.component.annotation.ComponentService;
import q.rorbin.component.annotation.compiler.base.BaseProcessor;
import q.rorbin.component.interfaces.IComponentServiceHelper;
import q.rorbin.component.model.AnnotationEntity;
import q.rorbin.component.model.ComponentServiceType;

/**
 * @author changhai.qiu
 */
public class ComponentServiceProcessor extends BaseProcessor {
    private final Class<ComponentService> annotationClass = ComponentService.class;
    private final ClassName iComponentServiceClassHelperClassName = ClassName.get(IComponentServiceHelper.class);
    private final ClassName ComponentServiceTypeClassName = ClassName.get(ComponentServiceType.class);
    private final ClassName classClassName = ClassName.get(Class.class);
    private final ParameterizedTypeName arrayListClassClassName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), classClassName);
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
                //If the annotation is not applied to the class, it is invalid
                if (!annotation.getHostType().isClass()) {
                    continue;
                }
                TypeElement typeElement = (TypeElement) annotation.getElement();
                List<? extends TypeMirror> mirrorList = typeElement.getInterfaces();
                //If the class does not implement any interface, it is invalid
                if (mirrorList == null || mirrorList.isEmpty()) {
                    continue;
                }
                String name = annotation.getHostClassName();
                String simpleName = annotation.getHostClassSimpleName();
                String version = annotation.getElement().getAnnotation(annotationClass).version();
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(ComponentConst.PREFIX + simpleName + ComponentConst.SERVICE_HEPLER_SUFFIX)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(keepClassName)
                        .addJavadoc("@author changhai qiu\n")
                        .addJavadoc("This class is automatically generated using the annotation processor." +
                                " The code in the class cannot be modified. The detailed usage is as follows\n")
                        .addJavadoc("@see " + annotationClass.getName() + "\n")
                        .addSuperinterface(iComponentServiceClassHelperClassName);
                //create getService method
                MethodSpec.Builder getServiceBuilder = MethodSpec.methodBuilder("getService")
                        .addAnnotation(Override.class).addAnnotation(Nullable.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ComponentServiceTypeClassName)
                        .addStatement("$T interfaces = new $T()", arrayListClassClassName, arrayListClassClassName);
                for (TypeMirror mirror : mirrorList) {
                    String interfaceName = mirror.toString();
                    if (interfaceName.contains("<")) {
                        interfaceName = interfaceName.replaceAll("<.*>", "");
                    }
                    getServiceBuilder.addStatement("interfaces.add(" + interfaceName + ".class)");
                }
                getServiceBuilder.addStatement("return new $T(" + name + ".class, $S, interfaces)", ComponentServiceTypeClassName, version);
                classBuilder.addMethod(getServiceBuilder.build());
                try {
                    JavaFile.builder(packageName, classBuilder.build()).build().writeTo(mFiler);
                } catch (IOException e) {
                }
            }
        }
        return true;
    }
}