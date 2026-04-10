package lol.apex.feature.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lol.apex.Apex;
import lol.apex.feature.file.BaseFile;
import lol.apex.feature.proxies.Proxy;
import lol.apex.feature.proxies.ProxyDTO;
import lol.apex.feature.proxies.ProxyHolder;

import java.util.UUID;

public class ProxiesFile extends BaseFile<JsonArray> {
    public static final ProxiesFile DEFAULT = new ProxiesFile("Proxies.json");
    public static final Gson GSON = new Gson();

    public ProxiesFile(String file) {
        super(file, JsonArray.class);
    }

    @Override
    protected void load(JsonArray object) {
        final var dtos = GSON.fromJson(object, ProxyDTO[].class);
        Apex.LOGGER.info("Loading proxy DTOs");

        if (dtos != null) {
            ProxyHolder.proxies.clear();
            for (final var dto : dtos) {
                ProxyHolder.proxies.add(dto.toProxy());
            }
        } else {
            Apex.LOGGER.error("proxy DTOs are null");
        }
    }

    @Override
    protected JsonArray save() {
        return GSON.toJsonTree(ProxyHolder.proxies.stream().map(Proxy::toDTO).toArray()).getAsJsonArray();
    }
}
