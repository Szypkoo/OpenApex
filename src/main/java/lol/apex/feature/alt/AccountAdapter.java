package lol.apex.feature.alt;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class AccountAdapter implements JsonDeserializer<Account> {

    private static final Gson DELEGATE = new Gson();

    @Override
    public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final var obj = json.getAsJsonObject();
        final var type = Account.Type.valueOf(obj.get("type").getAsString());

        return switch (type) {
            case Account.Type.CRACKED -> DELEGATE.fromJson(obj, Account.CrackedAccount.class);
            case Account.Type.MICROSLOP -> DELEGATE.fromJson(obj, Account.MicroslopAccount.class);
        };
    }
}