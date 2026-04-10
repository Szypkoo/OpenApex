package lol.apex.feature.alt;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.util.ApiServices;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.Proxy;
import java.util.Optional;
import java.util.UUID;

import static lol.apex.util.CommonVars.mc;

@NullMarked
public sealed interface Account {
    enum Type {
        CRACKED,
        MICROSLOP
    }
    Type type();
    String name();
    @SuppressWarnings("unused")
    UUID uuid();
    void login();

    static CrackedAccount cracked(String name) {
        return CrackedAccount.of(name);
    }

    static CrackedAccount cracked(String name, UUID uuid) {
        return new CrackedAccount(name, uuid);
    }

    static MicroslopAccount ms(String name, String accessToken) {
        return MicroslopAccount.of(name, accessToken);
    }

    static MicroslopAccount ms(String name, String accessToken, String refreshToken) {
        return MicroslopAccount.of(name, accessToken, refreshToken);
    }

    static MicroslopAccount ms(String name, String accessToken, String refreshToken, UUID uuid) {
        return new MicroslopAccount(name, accessToken, refreshToken, uuid);
    }

    record CrackedAccount(String name, UUID uuid, Type type) implements Account {
        public static final Type t = Type.CRACKED;

        public CrackedAccount(String name, UUID uuid) {
            this(name, uuid, t);
        }

        @Override
        public Type type() {
            return t;
        }

        public static CrackedAccount of(String name) {
            return new CrackedAccount(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()));
        }

        @Override
        public void login() {
            final var a = mc.apiServices;

            final var environment = YggdrasilEnvironment.PROD.getEnvironment();
            final var as = YggdrasilAuthenticationService.createOffline(Proxy.NO_PROXY);
            final var sessionService = as.createMinecraftSessionService();
            final var session = new Session(
                    this.name(),
                    this.uuid(),
                    "",
                    Optional.empty(),
                    Optional.empty()
            );
            mc.session = session;
            final var userAuthenticationService = new YggdrasilUserApiService(
                    session.getAccessToken(),
                    Proxy.NO_PROXY,
                    environment
            );
            final var keys = ProfileKeys.create(userAuthenticationService, session, mc.runDirectory.toPath());
            mc.apiServices = new ApiServices(
                    sessionService,
                    as.getServicesKeySet(),
                    as.createProfileRepository(),
                    a.nameToIdCache(),
                    a.profileResolver()
            );
            mc.profileKeys = keys;
        }
    }

    record MicroslopAccount(String name, @Nullable String refreshToken, String accessToken, UUID uuid, Type type) implements Account {
        public MicroslopAccount(String name, @Nullable String refreshToken, String accessToken, UUID uuid) {
            this(name, refreshToken, accessToken, uuid, t);
        }
        public static final Type t = Type.MICROSLOP;

        @Override
        public Type type() {
            return t;
        }

        public static MicroslopAccount of(String name, String accessToken) {
            return new MicroslopAccount(name, null, accessToken, UUID.randomUUID());
        }

        public static MicroslopAccount of(String name, String refreshToken, String accessToken) {
            return new MicroslopAccount(name, refreshToken, accessToken, UUID.randomUUID());
        }

        @Override
        public void login() {
            final var a = mc.apiServices;

            final var environment = YggdrasilEnvironment.PROD.getEnvironment();
            final var as = new YggdrasilAuthenticationService(Proxy.NO_PROXY, environment);
            final var sessionService = as.createMinecraftSessionService();
            final var session = new Session(
                    this.name(),
                    this.uuid(),
                    this.accessToken(),
                    Optional.empty(),
                    Optional.empty()
            );
            mc.session = session;
            final var userAuthenticationService = new YggdrasilUserApiService(
                    session.getAccessToken(),
                    Proxy.NO_PROXY,
                    environment
            );
            final var keys = ProfileKeys.create(userAuthenticationService, session, mc.runDirectory.toPath());
            mc.apiServices = new ApiServices(
                    sessionService,
                    as.getServicesKeySet(),
                    as.createProfileRepository(),
                    a.nameToIdCache(),
                    a.profileResolver()
            );
            mc.profileKeys = keys;
        }
    }

}

