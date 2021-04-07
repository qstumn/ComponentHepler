package q.rorbin.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author changhai.qiu
 * using on the class, indicating that the class is the implementation of component service interface,
 * it will be automatically registered, so you can use it in every other component
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ComponentService {
    //service impl version, same version cannot exist at the same time
    String version() default "main";
}
