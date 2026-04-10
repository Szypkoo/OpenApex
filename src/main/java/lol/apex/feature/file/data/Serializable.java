package lol.apex.feature.file.data;

import com.google.gson.JsonElement;

public interface Serializable {
    JsonElement toJson();
    void fromJson(JsonElement json);
}
