package q.rorbin.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author changhai.qiu
 * using on the class, indicating that the class is the entry class for component initialization
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ComponentInitializator {
}
