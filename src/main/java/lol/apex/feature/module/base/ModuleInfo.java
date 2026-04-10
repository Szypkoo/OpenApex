package lol.apex.feature.module.base;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name(); 
    String description() default "No Description."; 
    Category category();
}