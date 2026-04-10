package lol.apex.feature.proxies;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.SocketAddress;

@NullMarked
public interface ProxyWithAuth extends Proxy {
    @NullMarked
    record Credentials(String username, String password) {}
    @Nullable Credentials credentials();

    @NullMarked
    record SOCKS5Proxy(String name, @Nullable Credentials credentials, SocketAddress address) implements ProxyWithAuth {
        @Override
        public Type type() {
            return Type.SOCKS5;
        }

        @Override
        public ProxyHandler handler() {
            return credentials != null
                    ? new Socks5ProxyHandler(address, credentials.username, credentials.password)
                    : new Socks5ProxyHandler(address);
        }
    }

    @NullMarked
    record HTTPProxy(String name, @Nullable Credentials credentials, SocketAddress address) implements ProxyWithAuth {
        @Override
        public Type type() {
            return Type.HTTP;
        }

        @Override
        public ProxyHandler handler() {
            return credentials != null
                    ? new HttpProxyHandler(address, credentials.username, credentials.password)
                    : new HttpProxyHandler(address);
        }
    }
}
