package lol.apex.util.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE) // can't let people know our stuff is skidded
@Repeatable(VeryPasted.class)
public @interface Pasted {
    /** the original source of this pasted garbage **/
    String value();
}
