package lol.apex.util.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@Repeatable(VeryIdeaSkidded.class)
public @interface IdeaSkidded {
    /** Where this was idea-skidded from **/
    String value();
}
