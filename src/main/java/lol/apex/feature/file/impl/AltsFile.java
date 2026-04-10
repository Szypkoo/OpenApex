package lol.apex.feature.file.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import lol.apex.feature.alt.Account;
import lol.apex.feature.alt.AccountAdapter;
import lol.apex.feature.alt.AltsHolder;
import lol.apex.feature.file.BaseFile;

import java.util.List;

public class AltsFile extends BaseFile<JsonArray> {
    public static final AltsFile DEFAULT = new AltsFile("Alts.json");
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Account.class, new AccountAdapter())
            .create();

    public AltsFile(String name) {
        super(name, JsonArray.class);
    }

    @Override
    protected void load(JsonArray in) {
        final var type = new TypeToken<List<Account>>() {}.getType();
        final List<Account> a = gson.fromJson(in, type);
        AltsHolder.accounts.addAll(a);
    }

    @Override
    protected JsonArray save() {
        return gson.toJsonTree(AltsHolder.accounts).getAsJsonArray();
    }
}
