package q.rorbin.component.model;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author changhai.qiu
 */
public class AnnotationEntity {
    private Class<? extends Annotation> annotationClass;
    private String packageName;

    private Element element;

    private String hostName;
    private String hostClassName;
    private String hostClassSimpleName;
    private TypeMirror hostTypeMirror;
    private ElementKind hostType;

    private List<String> hostParamClassNames;
    private List<String> hostParamNames;
    private List<? extends TypeMirror> hostParamTypeMirrors;
    private String hostReturnClassName;
    private TypeMirror hostReturnTypeMirror;

    private String hostOwnerName;
    private String hostOwnerSimpleName;
    private TypeMirror hostOwnerTypeMirror;

    public TypeMirror getHostTypeMirror() {
        return hostTypeMirror;
    }

    public void setHostTypeMirror(TypeMirror hostTypeMirror) {
        this.hostTypeMirror = hostTypeMirror;
    }

    public List<? extends TypeMirror> getHostParamTypeMirrors() {
        return hostParamTypeMirrors;
    }

    public void setHostParamTypeMirrors(List<? extends TypeMirror> hostParamTypeMirrors) {
        this.hostParamTypeMirrors = hostParamTypeMirrors;
    }

    public TypeMirror getHostReturnTypeMirror() {
        return hostReturnTypeMirror;
    }

    public void setHostReturnTypeMirror(TypeMirror hostReturnTypeMirror) {
        this.hostReturnTypeMirror = hostReturnTypeMirror;
    }

    public TypeMirror getHostOwnerTypeMirror() {
        return hostOwnerTypeMirror;
    }

    public void setHostOwnerTypeMirror(TypeMirror hostOwnerTypeMirror) {
        this.hostOwnerTypeMirror = hostOwnerTypeMirror;
    }

    public Element getElement() {
        return element;
    }

    public AnnotationEntity setElement(Element element) {
        this.element = element;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public AnnotationEntity setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public AnnotationEntity setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public List<String> getHostParamClassNames() {
        return hostParamClassNames;
    }

    public AnnotationEntity setHostParamClassNames(List<String> hostParamClassNames) {
        this.hostParamClassNames = hostParamClassNames;
        return this;
    }

    public List<String> getHostParamNames() {
        return hostParamNames;
    }

    public AnnotationEntity setHostParamNames(List<String> hostParamNames) {
        this.hostParamNames = hostParamNames;
        return this;
    }

    public String getHostReturnClassName() {
        return hostReturnClassName;
    }

    public AnnotationEntity setHostReturnClassName(String hostReturnClassName) {
        this.hostReturnClassName = hostReturnClassName;
        return this;
    }

    public String getHostClassSimpleName() {
        return hostClassSimpleName;
    }

    public AnnotationEntity setHostClassSimpleName(String hostClassSimpleName) {
        this.hostClassSimpleName = hostClassSimpleName;
        return this;
    }

    public String getHostOwnerSimpleName() {
        return hostOwnerSimpleName;
    }

    public AnnotationEntity setHostOwnerSimpleName(String hostOwnerSimpleName) {
        this.hostOwnerSimpleName = hostOwnerSimpleName;
        return this;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public AnnotationEntity setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
        return this;
    }

    public String getHostClassName() {
        return hostClassName;
    }

    public AnnotationEntity setHostClassName(String hostClassName) {
        this.hostClassName = hostClassName;
        return this;
    }

    public ElementKind getHostType() {
        return hostType;
    }

    public AnnotationEntity setHostType(ElementKind hostType) {
        this.hostType = hostType;
        return this;
    }

    public String getHostOwnerName() {
        return hostOwnerName;
    }

    public AnnotationEntity setHostOwnerName(String hostOwnerName) {
        this.hostOwnerName = hostOwnerName;
        return this;
    }
}