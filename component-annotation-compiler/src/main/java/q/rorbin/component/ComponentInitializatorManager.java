package q.rorbin.component;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.component.interfaces.IComponent;

/**
 * @author changhai.qiu
 */
public class ComponentInitializatorManager {
    private final List<String> allInitializators = new ArrayList<>();

    public ComponentInitializatorManager() {
        addAllInitializator();
    }

    private void addAllInitializator() {
        //the code will be written here using ASM
    }

    private void addInitializator(String name) {
        allInitializators.add(name);
    }

    public void invokeAllComponentInitializator() {
        for (String initalizator : allInitializators) {
            try {
                Class.forName(initalizator).newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
