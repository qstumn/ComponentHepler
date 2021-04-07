package q.rorbin.component.annotation.compiler.base;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import q.rorbin.component.model.AnnotationEntity;

/**
 * @author changhai.qiu
 */
public abstract class BaseProcessor extends AbstractProcessor {
    protected Messager mMessager;
    protected Elements mElementUtils;
    protected Types mTypeUtils;
    protected Filer mFiler;

    private final Map<String, Map<Class<? extends Annotation>, List<AnnotationEntity>>> mAnnotations = new HashMap<>();
    private final List<AnnotationEntity> mAllAnnotations = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mAnnotations.clear();
        mAllAnnotations.clear();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Class[] typeStrings = getSupportedAnnotations();
        for (Class type : typeStrings) {
            types.add(type.getCanonicalName());
        }
        return types;
    }

    protected abstract Class<? extends Annotation>[] getSupportedAnnotations();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * collect all annotation into one collection whitout nothing classification
     */
    protected List<AnnotationEntity> gatherSupportedAnnotation(RoundEnvironment roundEnv) {
        if (getSupportedAnnotations() == null || getSupportedAnnotations().length <= 0) {
            return Collections.emptyList();
        }
        for (Class<? extends Annotation> clazz : getSupportedAnnotations()) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(clazz);
            for (Element element : elements) {
                AnnotationEntity anno = getAnnotationByKind(element);
                if (anno != null) {
                    gatherAnnotationPublicInfo(clazz, element, anno);
                    mAllAnnotations.add(anno);
                }
            }
        }
        return mAllAnnotations;
    }

    /**
     * collect by host class and annotation type
     */
    protected Map<String, Map<Class<? extends Annotation>, List<AnnotationEntity>>> gatherSupportAnnotations(RoundEnvironment roundEnv) {
        if (getSupportedAnnotations() == null || getSupportedAnnotations().length <= 0) {
            return Collections.emptyMap();
        }
        for (Class<? extends Annotation> clazz : getSupportedAnnotations()) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(clazz);
            for (Element element : elements) {
                AnnotationEntity anno = getAnnotationByKind(element);
                if (anno != null) {
                    gatherAnnotationPublicInfo(clazz, element, anno);
                    Map<Class<? extends Annotation>, List<AnnotationEntity>> classListMap = mAnnotations.get(anno.getHostOwnerName());
                    if (classListMap == null) {
                        classListMap = new HashMap<>();
                        mAnnotations.put(anno.getHostOwnerName(), classListMap);
                    }
                    List<AnnotationEntity> annotationBeans = classListMap.get(clazz);
                    if (annotationBeans == null) {
                        annotationBeans = new ArrayList<>();
                    }
                    classListMap.put(clazz, annotationBeans);
                    annotationBeans.add(anno);
                }
            }
        }
        return mAnnotations;
    }

    private AnnotationEntity getAnnotationByKind(Element element) {
        AnnotationEntity anno = null;
        switch (element.getKind()) {
            case FIELD:
                anno = createFieldAnnotation((VariableElement) element);
                break;
            case METHOD:
                anno = createMethodAnnotation((ExecutableElement) element);
                break;
            case CLASS:
                anno = createClassAnnotation((TypeElement) element);
                break;
            default:
        }
        return anno;
    }

    private void gatherAnnotationPublicInfo(Class<? extends Annotation> clazz, Element element, AnnotationEntity anno) {
        anno.setElement(element);
        anno.setPackageName(mElementUtils.getPackageOf(element).getQualifiedName().toString());
        anno.setHostName(element.getSimpleName().toString());
        anno.setHostType(element.getKind());
        anno.setAnnotationClass(clazz);
        anno.setHostOwnerName(element.getEnclosingElement().asType().toString());
        anno.setHostOwnerSimpleName(element.getEnclosingElement().getSimpleName().toString());
        anno.setHostOwnerTypeMirror(element.getEnclosingElement().asType());
    }

    private AnnotationEntity createFieldAnnotation(VariableElement element) {
        AnnotationEntity anno = new AnnotationEntity();
        anno.setHostClassName(element.asType().toString());
        Element typeEle = mTypeUtils.asElement(element.asType());
        anno.setHostClassSimpleName(typeEle != null ? typeEle.getSimpleName().toString() : element.asType().toString());
        anno.setHostTypeMirror(element.asType());
        return anno;
    }

    private AnnotationEntity createMethodAnnotation(ExecutableElement element) {
        AnnotationEntity anno = new AnnotationEntity();
        List<? extends VariableElement> parameters = element.getParameters();
        List<String> paramNames = new ArrayList<>();
        if (parameters != null) {
            for (VariableElement parameter : parameters) {
                paramNames.add(parameter.getSimpleName().toString());
            }
        }
        anno.setHostParamNames(paramNames);
        anno.setHostReturnClassName(element.getReturnType().toString());
        anno.setHostReturnTypeMirror(element.getReturnType());
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<String> paramClassNames = new ArrayList<>();
        if (parameterTypes != null) {
            for (TypeMirror type : parameterTypes) {
                paramClassNames.add(type.toString());
            }
        }
        anno.setHostParamClassNames(paramClassNames);
        anno.setHostParamTypeMirrors(parameterTypes);
        return anno;
    }

    private AnnotationEntity createClassAnnotation(TypeElement element) {
        AnnotationEntity anno = new AnnotationEntity();
        anno.setHostClassName(element.asType().toString());
        anno.setHostClassSimpleName(mTypeUtils.asElement(element.asType()).getSimpleName().toString());
        anno.setHostTypeMirror(element.asType());
        return anno;
    }


    protected void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
