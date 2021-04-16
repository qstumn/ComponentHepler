package q.rorbin.component.model;

import java.util.List;
import java.util.Objects;

/**
 * @author 邱长海
 */
public class ComponentServiceType {
    private Class name;
    private String version;
    private List<Class> interfaces;

    public ComponentServiceType(Class name, String version, List<Class> interfaces) {
        this.name = name;
        this.version = version;
        this.interfaces = interfaces;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Class> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Class> interfaces) {
        this.interfaces = interfaces;
    }

    public Class getName() {
        return name;
    }

    public void setName(Class name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentServiceType that = (ComponentServiceType) o;
        return name.getName().equals(that.name.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.getName());
    }
}
