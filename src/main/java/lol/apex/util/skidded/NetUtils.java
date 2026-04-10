package lol.apex.util.skidded;

import com.sun.net.httpserver.HttpExchange;
import lol.apex.util.annotation.Pasted;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;

@Pasted("Sigma Remake")
public class NetUtils {
    public static boolean isSafePath(@NonNull String path, @NonNull String prefix) {
        return path.startsWith(prefix) && !path.contains("..");
    }

    public static void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
        exchange.close();
    }

    public static void sendClasspathResource(HttpExchange exchange, String path, Class<?> resourceClass) throws IOException {
        try (InputStream stream = resourceClass.getResourceAsStream(path)) {
            if (stream == null) {
                sendNotFound(exchange);
                return;
            }

            byte[] response = IOUtils.toByteArray(stream);
            exchange.getResponseHeaders().add("Content-Type", getContentType(path));
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        }
    }

    public static String getContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".ttf")) {
            return "font/ttf";
        }
        if (lower.endsWith(".otf")) {
            return "font/otf";
        }
        if (lower.endsWith(".woff")) {
            return "font/woff";
        }
        if (lower.endsWith(".woff2")) {
            return "font/woff2";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (lower.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        return "application/octet-stream";
    }
}