package lol.apex.util.io;

import java.net.HttpURLConnection;

public record AutoCloseableURLConnection<T extends HttpURLConnection>(T connection) implements AutoCloseable {
    @Override
    public void close() {
        connection.disconnect();
    }
}
