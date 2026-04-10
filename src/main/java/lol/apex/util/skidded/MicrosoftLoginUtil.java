package lol.apex.util.skidded;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import lol.apex.util.annotation.AIPasted;
import lol.apex.util.annotation.Pasted;
import lol.apex.util.io.AutoCloseableURLConnection;
import lol.apex.util.java.StringUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Pasted("Sigma Remake")
@AIPasted("Refactored to use URLConnection-s")
@UtilityClass
public final class MicrosoftLoginUtil {

    public static final int CONNECT_TIMEOUT = 30_000;
    public static final int READ_TIMEOUT = 30_000;

    public static final String CLIENT_ID = "42a60a84-599d-44b2-a7c6-b00cdef1d6a2";
    public static final int PORT = 25575;

    public static CompletableFuture<String> acquireMSAuthCode(final Executor executor) {
        return acquireMSAuthCode(Util.getOperatingSystem()::open, executor);
    }

    public static CompletableFuture<String> acquireMSAuthCode(
            final Consumer<URI> browserAction,
            final Executor executor) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                final String state = RandomStringUtils.secure().nextAlphanumeric(8);
                final HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                final CountDownLatch latch = new CountDownLatch(1);
                final AtomicReference<String> authCode = new AtomicReference<>(null);
                final AtomicReference<String> errorMsg = new AtomicReference<>(null);

                server.createContext("/callback", exchange -> {
                    final String queryString = exchange.getRequestURI().getQuery();
                    final Map<String, String> query = parseQuery(queryString);

                    if (!state.equals(query.get("state"))) {
                        errorMsg.set(String.format("State mismatch! Expected '%s' but got '%s'.", state, query.get("state")));
                    } else if (query.containsKey("code")) {
                        authCode.set(query.get("code"));
                    } else if (query.containsKey("error")) {
                        errorMsg.set(String.format("%s: %s", query.get("error"), query.get("error_description")));
                    }

                    NetUtils.sendClasspathResource(exchange, "/assets/apex/callback.html", MicrosoftLoginUtil.class);
                    latch.countDown();
                });

                server.createContext("/callback.css", exchange ->
                        NetUtils.sendClasspathResource(exchange, "/assets/apex/callback.css", MicrosoftLoginUtil.class));

                server.createContext("/assets/", exchange -> {
                    String path = exchange.getRequestURI().getPath();
                    if (!NetUtils.isSafePath(path, "/assets/")) {
                        NetUtils.sendNotFound(exchange);
                        return;
                    }
                    NetUtils.sendClasspathResource(exchange, path, MicrosoftLoginUtil.class);
                });

                final URI uri = new URI("https://login.live.com/oauth20_authorize.srf?"
                        + "client_id=" + CLIENT_ID
                        + "&response_type=code"
                        + "&redirect_uri=http://localhost:" + PORT + "/callback"
                        + "&scope=XboxLive.signin%20XboxLive.offline_access"
                        + "&state=" + state
                        + "&prompt=select_account");

                browserAction.accept(uri);

                try {
                    server.start();
                    latch.await();

                    return Optional.ofNullable(authCode.get())
                            .filter(code -> !StringUtils.isBlank(code))
                            .orElseThrow(() -> new Exception(
                                    Optional.ofNullable(errorMsg.get())
                                            .orElse("There was no auth code or error description present.")
                            ));
                } finally {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ignored) {
                        }
                        server.stop(2);
                    }, "MsAuthCallbackServerStopper").start();
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Microsoft auth code acquisition was cancelled!");
            } catch (Exception e) {
                throw new CompletionException("Unable to acquire Microsoft auth code!", e);
            }
        }, executor);
    }

    private static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        p -> URLDecoder.decode(p[0], StandardCharsets.UTF_8),
                        p -> p.length > 1 ? URLDecoder.decode(p[1], StandardCharsets.UTF_8) : "",
                        (a, b) -> b
                ));
    }

    private static String postJson(String urlString, String jsonBody) throws IOException, URISyntaxException {
        return post(urlString, jsonBody, "application/json");
    }

    private static String postForm(String urlString, Map<String, String> params) throws IOException, URISyntaxException {
        String formBody = params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        return post(urlString, formBody, "application/x-www-form-urlencoded");
    }

    private static String post(String urlString, String body, String contentType) throws IOException, URISyntaxException {
        final var url = new URI(urlString).toURL();
        try (final var wrap = new AutoCloseableURLConnection<>((HttpURLConnection) url.openConnection())) {
            final var conn = wrap.connection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Accept", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            return readResponse(conn);
        }
    }

    private static String get(String urlString, String authorization) throws IOException, URISyntaxException {
        final var url = new URI(urlString).toURL();
        try (final var wrap = new AutoCloseableURLConnection<>((HttpURLConnection) url.openConnection())) {
            final var conn = wrap.connection();
            conn.disconnect();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization);
            }
            conn.setRequestProperty("Accept", "application/json");

            return readResponse(conn);
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public static CompletableFuture<String> acquireMSAccessToken(final String authCode, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", CLIENT_ID);
                params.put("grant_type", "authorization_code");
                params.put("code", authCode);
                params.put("redirect_uri", "http://localhost:" + PORT + "/callback");

                String response = postForm("https://login.live.com/oauth20_token.srf", params);
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return Optional.ofNullable(json.get("access_token"))
                        .map(JsonElement::getAsString)
                        .filter(token -> !StringUtils.isBlank(token))
                        .orElseThrow(() -> new Exception(
                                json.has("error") ? String.format("%s: %s",
                                        json.get("error").getAsString(),
                                        json.get("error_description").getAsString())
                                        : "There was no access token or error description present."
                        ));
            } catch (Exception e) {
                throw new CompletionException("Unable to acquire Microsoft access token!", e);
            }
        }, executor);
    }

    public static CompletableFuture<String> acquireXboxAccessToken(final String accessToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject entity = new JsonObject();
                JsonObject properties = new JsonObject();
                properties.addProperty("AuthMethod", "RPS");
                properties.addProperty("SiteName", "user.auth.xboxlive.com");
                properties.addProperty("RpsTicket", "d=" + accessToken);
                entity.add("Properties", properties);
                entity.addProperty("RelyingParty", "http://auth.xboxlive.com");
                entity.addProperty("TokenType", "JWT");

                String response = postJson("https://user.auth.xboxlive.com/user/authenticate", entity.toString());
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return Optional.ofNullable(json.get("Token"))
                        .map(JsonElement::getAsString)
                        .filter(token -> !StringUtils.isBlank(token))
                        .orElseThrow(() -> new Exception(
                                json.has("XErr") ? String.format("%s: %s",
                                        json.get("XErr").getAsString(),
                                        json.get("Message").getAsString())
                                        : "There was no access token or error description present."
                        ));
            } catch (Exception e) {
                throw new CompletionException("Unable to acquire Xbox Live access token!", e);
            }
        }, executor);
    }

    public static CompletableFuture<Map<String, String>> acquireXboxXstsToken(final String accessToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject entity = new JsonObject();
                JsonObject properties = new JsonObject();
                JsonArray userTokens = new JsonArray();
                userTokens.add(new JsonPrimitive(accessToken));
                properties.addProperty("SandboxId", "RETAIL");
                properties.add("UserTokens", userTokens);
                entity.add("Properties", properties);
                entity.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                entity.addProperty("TokenType", "JWT");

                String response = postJson("https://xsts.auth.xboxlive.com/xsts/authorize", entity.toString());
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return Optional.ofNullable(json.get("Token"))
                        .map(JsonElement::getAsString)
                        .filter(token -> !StringUtils.isBlank(token))
                        .map(token -> {
                            String uhs = json.getAsJsonObject("DisplayClaims")
                                    .getAsJsonArray("xui")
                                    .get(0).getAsJsonObject()
                                    .get("uhs").getAsString();

                            Map<String, String> result = new HashMap<>();
                            result.put("Token", token);
                            result.put("uhs", uhs);
                            return result;
                        })
                        .orElseThrow(() -> new Exception(
                                json.has("XErr") ? String.format("%s: %s",
                                        json.get("XErr").getAsString(),
                                        json.get("Message").getAsString())
                                        : "There was no access token or error description present."
                        ));
            } catch (Exception e) {
                throw new CompletionException("Unable to acquire Xbox Live XSTS token!", e);
            }
        }, executor);
    }

    public static CompletableFuture<String> acquireMCAccessToken(
            final String xstsToken, final String userHash, final Executor executor) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                String body = String.format("{\"identityToken\": \"XBL3.0 x=%s;%s\"}", userHash, xstsToken);
                String response = postJson("https://api.minecraftservices.com/authentication/login_with_xbox", body);

                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return Optional.ofNullable(json.get("access_token"))
                        .map(JsonElement::getAsString)
                        .filter(token -> !StringUtils.isBlank(token))
                        .orElseThrow(() -> new Exception(
                                json.has("error") ? String.format("%s: %s",
                                        json.get("error").getAsString(),
                                        json.get("errorMessage").getAsString())
                                        : "There was no access token or error description present."
                        ));
            } catch (Exception e) {
                throw new CompletionException("Unable to acquire Minecraft access token!", e);
            }
        }, executor);
    }

    public static CompletableFuture<Session> login(final String mcToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = get("https://api.minecraftservices.com/minecraft/profile", "Bearer " + mcToken);
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return Optional.ofNullable(json.get("id"))
                        .map(JsonElement::getAsString)
                        .filter(uuid -> !StringUtils.isBlank(uuid))
                        .map(uuid -> new Session(
                                json.get("name").getAsString(),
                                UUID.fromString(StringUtil.formatUuid(uuid)),
                                mcToken,
                                Optional.empty(),
                                Optional.empty()
                        ))
                        .orElseThrow(() -> new Exception(
                                json.has("error") ? String.format("%s: %s",
                                        json.get("error").getAsString(),
                                        json.get("errorMessage").getAsString())
                                        : "There was no profile or error description present."
                        ));
            } catch (Exception e) {
                throw new CompletionException("Unable to fetch Minecraft profile!", e);
            }
        }, executor);
    }

    public static CompletableFuture<Session> loginWithBrowser(Executor executor) {
        return acquireMSAuthCode(executor)
                .thenCompose(code -> acquireMSAccessToken(code, executor))
                .thenCompose(msToken -> acquireXboxAccessToken(msToken, executor))
                .thenCompose(xboxToken -> acquireXboxXstsToken(xboxToken, executor))
                .thenCompose(xsts -> acquireMCAccessToken(xsts.get("Token"), xsts.get("uhs"), executor))
                .thenCompose(mcToken -> login(mcToken, executor));
    }

    @AIPasted("https://chatgpt.com/share/69d182b2-b7b4-8331-8a2f-4d19eedd8ac5")
    public static CompletableFuture<Session> loginWithRefreshToken(String refreshToken, Executor executor) {
        return refreshMSAccessToken(refreshToken, executor)
                .thenCompose(msToken -> acquireXboxAccessToken(msToken, executor))
                .thenCompose(xboxToken -> acquireXboxXstsToken(xboxToken, executor))
                .thenCompose(xsts -> acquireMCAccessToken(xsts.get("Token"), xsts.get("uhs"), executor))
                .thenCompose(mcToken -> login(mcToken, executor));
    }

    @AIPasted("https://chatgpt.com/share/69d182b2-b7b4-8331-8a2f-4d19eedd8ac5")
    public static CompletableFuture<String> refreshMSAccessToken(String refreshToken, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", CLIENT_ID);
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", refreshToken);

                String response = postForm("https://login.live.com/oauth20_token.srf", params);
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                return json.get("access_token").getAsString();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}