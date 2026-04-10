package lol.apex.util.java;

import lol.apex.util.annotation.Pasted;

@Pasted("https://ihateregex.io/expr/uuid/")
public class StringUtil {
    public static String formatUuid(String uuid) {
        return uuid.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
        );
    }
}