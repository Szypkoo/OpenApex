package lol.apex.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface VeryAIPasted {
    AIPasted[] value();
}
