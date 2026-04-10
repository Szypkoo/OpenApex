package lol.apex.feature.proxies;

import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import lol.apex.Apex;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lol.apex.feature.proxies.ProxyWithAuth.Credentials;

@NullMarked
public interface Proxy {
    /**
     * the name of this proxy
     **/
    String name();

    /**
     * the type of this proxy
     **/
    Type type();

    SocketAddress address();

    ProxyHandler handler();

    default ProxyDTO toDTO() {
        return ProxyDTO.fromProxy(this);
    }
    static @Nullable Proxy parse(String s) {
        try {
            final var uri = new URI(s);
            final var type = Type.BY_PROTOCOLS.get(uri.getScheme());
            if (type == null) {
                Apex.LOGGER.error("Type for scheme '{}' not found", uri.getScheme());
                return null;
            }
            final var userInfo = uri.getUserInfo();
            final var credentials = Optional.ofNullable(userInfo).map(x -> {
                final var split = x.split(":", 2);
                if (split.length != 2) {
                    return null;
                }
                return new Credentials(split[0], split[1]);
            }).orElse(null);
            return type.construct(
                    "Imported " + type.name + " proxy",
                    credentials,
                    new InetSocketAddress(uri.getHost(), uri.getPort())
            );
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @FunctionalInterface
    interface ProxyConstructor {
        Proxy construct(String name, @Nullable Credentials credentials, SocketAddress address);
    }

    enum Type {
        HTTP("HTTP", ProxyWithAuth.HTTPProxy::new, false, "http"),
        //        HTTPS("HTTPS"), // netty doesn't support HTTPS proxies :(
        SOCKS4("SOCKS4", (n, ignored, a) ->
                new SOCKS4Proxy(n, ignored != null ? ignored.username() : null, a), true, "socks4", "socks4a"),
        SOCKS5("SOCKS5", ProxyWithAuth.SOCKS5Proxy::new, false, "socks5", "socks5h");
        public final String name;
        private final ProxyConstructor constructor;
        public final boolean passwords;
        public final String[] protocols;
        private static final Map<String, Type> BY_PROTOCOLS;

        static {
            final var vals = values();
            BY_PROTOCOLS = new HashMap<>(vals.length);

            for (final var t : vals) {
                for (final var protocol : t.protocols) {
                    BY_PROTOCOLS.put(protocol, t);
                }
            }
        }

        Type(String name, ProxyConstructor constructor, boolean passwords, String... protocols) {
            this.name = name;
            this.constructor = constructor;
            this.passwords = passwords;
            this.protocols = protocols;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public Proxy construct(String name, @Nullable Credentials credentials, SocketAddress address) {
            return this.constructor.construct(name, credentials, address);
        }
    }

    @NullMarked
    record SOCKS4Proxy(String name, @Nullable String user, SocketAddress address) implements Proxy {
        public SOCKS4Proxy(String name, @Nullable Credentials credentials, SocketAddress address) {
            this(name, credentials != null ? credentials.username() : null, address);
        }
        @Override
        public Type type() {
            return Type.SOCKS4;
        }

        @Override
        public ProxyHandler handler() {
            return user != null ? new Socks4ProxyHandler(address, user) : new Socks4ProxyHandler(address);
        }
    }
}
