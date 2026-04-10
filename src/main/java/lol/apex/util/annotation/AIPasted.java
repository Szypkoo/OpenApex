package lol.apex.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface AIPasted {
    /** the LLM that made this garbage code and/or maybe some additional context **/
    String value();
}
