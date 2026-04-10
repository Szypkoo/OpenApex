package lol.apex.feature.proxies;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;

@NullMarked
public record ProxyDTO(String name, Proxy.Type type, String host, int port, ProxyWithAuth.@Nullable Credentials credentials) {
    public static ProxyDTO fromProxy(Proxy p) {
        final var addr = (InetSocketAddress) p.address();
        return new ProxyDTO(
                p.name(),
                p.type(),
                addr.getHostString(),
                addr.getPort(),
                p instanceof ProxyWithAuth a ? a.credentials() : null
        );
    }

    public Proxy toProxy() {
        return type.construct(name, credentials, new InetSocketAddress(host, port));
    }
}

