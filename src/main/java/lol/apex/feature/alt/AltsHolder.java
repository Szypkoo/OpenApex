package lol.apex.feature.alt;

import lol.apex.feature.file.impl.AltsFile;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class AltsHolder {
    public static final List<Account> accounts = new ArrayList<>();

    public static boolean addAccount(Account acc) {
        final var r = accounts.add(acc);
        AltsFile.DEFAULT.saveToFile();
        return r;
    }
}
