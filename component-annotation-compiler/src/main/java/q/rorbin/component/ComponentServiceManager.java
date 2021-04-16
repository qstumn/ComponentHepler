package q.rorbin.component;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import q.rorbin.component.interfaces.IComponentServiceHelper;
import q.rorbin.component.model.ComponentServiceType;

/**
 * @author changhai.qiu
 */
public class ComponentServiceManager {

    private final Map<Class, Map<String, Class>> allServices = new HashMap<>();

    public ComponentServiceManager() {
        initServices();
    }

    private void initServices() {
        //the code will be written here using ASM
    }

    private void initServiceByHelper(String helperName) {
        IComponentServiceHelper helper = null;
        try {
            helper = (IComponentServiceHelper) Class.forName(helperName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (helper == null) {
            return;
        }
        ComponentServiceType type = helper.getService();
        for (Class interfaceClass : type.getInterfaces()) {
            initService(interfaceClass, type.getName(), type.getVersion());
        }
    }

    private void initService(Class interfaceClass, Class serviceClass, String version) {
        Map<String, Class> services = allServices.get(interfaceClass);
        if (services == null) {
            services = new HashMap<>();
            allServices.put(interfaceClass, services);
        }
        Class existVersionService = services.get(version);
        if (existVersionService != null) {
            throw new RuntimeException(interfaceClass + "has been registered an implementaion with the same version, serviceImpl name is : " + existVersionService);
        }
        services.put(version, serviceClass);
    }

    @Nullable
    public Class getService(Class interfaceClass, String version) {
        Map<String, Class> services = allServices.get(interfaceClass);
        if (services == null) {
            return null;
        }
        return services.get(version);
    }
}
