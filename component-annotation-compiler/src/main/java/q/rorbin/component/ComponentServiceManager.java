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

    private final Map<String, Map<String, String>> allServices = new HashMap<>();

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
        for (String interfaceName : type.getInterfaces()) {
            initService(interfaceName, type.getName(), type.getVersion());
        }
    }

    private void initService(String interfaceName, String serviceName, String version) {
        Map<String, String> services = allServices.get(interfaceName);
        if (services == null) {
            services = new HashMap<>();
            allServices.put(interfaceName, services);
        }
        String existVersionService = services.get(version);
        if (existVersionService != null) {
            throw new RuntimeException(interfaceName + "has been registered an implementaion with the same version, serviceImpl name is : " + existVersionService);
        }
        services.put(version, serviceName);
    }

    @Nullable
    public String getService(String interfaceName, String version) {
        Map<String, String> services = allServices.get(interfaceName);
        if (services == null) {
            return null;
        }
        return services.get(version);
    }
}
