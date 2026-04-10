package lol.apex.feature.ui.screen;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import lol.apex.Apex;
import lol.apex.feature.alt.Account;
import lol.apex.feature.file.impl.AltsFile;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import lol.apex.util.io.FileUtil;
import lol.apex.util.skidded.CookieLoginUtil;
import lol.apex.util.skidded.MicrosoftLoginUtil;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;

import static lol.apex.feature.alt.AltsHolder.accounts;
import static lol.apex.feature.alt.AltsHolder.addAccount;

public final class AltScreen extends ImGuiScreen {
    private boolean addingAccount = false;
    // an access token I had copied was 1044 characters long
    private final ImString inputBuffer = new ImString(1044);
    public static final SystemToast.Type ERR = new SystemToast.Type(10000L);

    public AltScreen() {
        super(Text.empty());
        AltsFile.DEFAULT.loadFromFile();
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        ImGui.setNextWindowSize(550f, 400f, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Alt Manager", ImGuiWindowFlags.NoCollapse)) {
            renderAccountsTable();

            ImGui.separator();

            if (ImGui.button("Add")) {
                addingAccount = true;
                inputBuffer.set("");
            }

            if (addingAccount) {
                renderAddingAccountMenu();
            }
        }
        ImGui.end();
    }

    private void renderAccountsTable() {
        if (ImGui.beginTable("Accounts", 3, ImGuiTableFlags.Borders | ImGuiTableFlags.Resizable)) {
            ImGui.tableSetupColumn("Username");
            ImGui.tableSetupColumn("Type");
            ImGui.tableSetupColumn("Actions");
            ImGui.tableHeadersRow();

            final var accountsCopy = new ArrayList<>(accounts);
            for (final var account : accountsCopy) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(account.name());

                ImGui.tableNextColumn();
                ImGui.text(account instanceof Account.MicroslopAccount ? "Microslop" : "Cracked");

                ImGui.tableNextColumn();
                if (ImGui.button("Login##" + account.name())) {
                    account.login();
                    Apex.LOGGER.info("Logged in as: {}", account.name());
                }
                ImGui.sameLine();
                if (ImGui.button("Remove##" + account.name())) {
                    accounts.remove(account);
                }
            }
            ImGui.endTable();
        }
    }

    private enum LoginType {
        TEXT("Text"),
        COOKIE("Cookie"),
        BROWSER("Browser");
        public final String name;
        public static final Map<String, LoginType> N2T;
        public static final String[] NAMES = Arrays.stream(values()).map(x -> x.name).toArray(String[]::new);

        static {
            final var vals = values();
            N2T = new HashMap<>(vals.length);
            for (final var loginType : vals) {
                N2T.put(loginType.name, loginType);
            }
        }

        LoginType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private enum TextLoginType {
        CRACKED("Cracked account"),
        REFRESH_TOKEN("Refresh Token"),
        ACCESS_TOKEN("Access Token");

        public final String name;

        TextLoginType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @Language("regexp")
    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}$";

    /**
     * Tries to detect what type the text is using various checks.
     * @param text the text (access token, refresh token, or cracked account username)
     * @return any TextLoginType, or `null` if it didn't match any of the access token or refresh token patterns and is an invalid username.
     */
    private @Nullable TextLoginType detectFromText(@NonNull String text) {
        if (text.startsWith("M."))
            return TextLoginType.REFRESH_TOKEN;
        if (text.startsWith("eyJra") && text.length() >= 13) {
            return TextLoginType.ACCESS_TOKEN;
        }
        return text.matches(VALID_USERNAME_REGEX) ? TextLoginType.CRACKED : null;
    }

    private LoginType loginType = LoginType.TEXT;
    @SuppressWarnings("ConstantConditions")
    private final ImInt currentLoginType = new ImInt(loginType.ordinal());
    private void loginToText(@NonNull TextLoginType type, @NonNull String input) {
        switch (type) {
            case ACCESS_TOKEN -> MicrosoftLoginUtil
                    .login(input, executor)
                    .thenAccept(session -> addAccount(Account.ms(session.getUsername(), input))).exceptionally(err -> {
                        showError(err);
                        return null;
                    });

            case REFRESH_TOKEN ->
                    MicrosoftLoginUtil.loginWithRefreshToken(input, executor).thenAccept(session -> addAccount(Account.ms(
                            session.getUsername(), input,
                            session.getAccessToken(), Optional.ofNullable(session.getUuidOrNull()).orElse(UUID.randomUUID())
                    ))).exceptionally(err -> {
                        showError(err);
                        return null;
                    })
                    ;
            case CRACKED -> {
                if (!input.isBlank()) {
                    addAccount(Account.cracked(input));
                }
            }
        }
    }

    // I used catgpt for adding cookie support and uh merging the browser login with this.
    // see https://chatgpt.com/share/69d182b2-b7b4-8331-8a2f-4d19eedd8ac5
    private void renderAddingAccountMenu() {
        ImGui.setNextWindowSize(400, 150, ImGuiCond.FirstUseEver);
        if (ImGui.begin("Add New Account", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.Modal | ImGuiWindowFlags.NoDocking)) {
            if (ImGui.combo("Login Type", currentLoginType, LoginType.NAMES)) {
                loginType = LoginType.values()[currentLoginType.get()];
            }

            if (loginType == LoginType.TEXT) {
                ImGui.inputTextWithHint("##input", "(cracked) Username, (premium) refresh token, or access token.", inputBuffer);
            }

            ImGui.spacing();

            if (ImGui.button("Add & Login")) {
                final var input = inputBuffer.get();

                switch (loginType) {
                    case TEXT -> {
                        final var type = detectFromText(input);
                        if (type != null)
                            loginToText(type, input);
                        else ImGui.textColored(Color.RED.getRGB(), "failed to detect what this is");
                    }

                    case COOKIE -> makeCookieThread().start();

                    case BROWSER -> makeLoginThread().start();
                }

                addingAccount = false;
                inputBuffer.set("");
            }

            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                addingAccount = false;
                inputBuffer.set("");
            }
        }
        ImGui.end();
    }

    private Thread makeCookieThread() {
        final var t = new Thread(() -> {
            try {
                // TODO: filter to only .txt files
                File file = FileUtil.openFile("Select cookie", null);
                if (file == null) {
                    showError(new Exception("no file selected"));
                    return;
                }

                var session = CookieLoginUtil.loginWithCookie(file);
                if (session == null) {
                    showError(new Exception("session is null"));
                    return;
                }

                addAccount(Account.ms(
                        session.username, session.newRefreshToken,
                        session.token, UUID.fromString(session.playerID)
                ));

            } catch (Exception e) {
                showError(e);
            }
        }, "Cookie login thread");
        t.setDaemon(true);
        return t;
    }

    private void showError(@Nullable Throwable err) {
        if (err != null) {
            Apex.LOGGER.error("Login failed:", err);
        }

        client.execute(() -> client.getToastManager().add(SystemToast.create(
                client,
                ERR,
                Text.literal("Apex"),
                Text.literal("Failed to login to account")
        )));
    }

    private @NonNull Thread makeLoginThread() {
        final var t = new Thread(() -> {
            try (var executor = Executors.newSingleThreadExecutor()) {
                MicrosoftLoginUtil.loginWithBrowser(executor).thenAccept(session -> {
                    final var acc = Account.ms(session.getUsername(), session.getAccessToken());
                    addAccount(acc);
                }).exceptionally(err -> {
                    Apex.LOGGER.error("Browser login failed:", err);
                    client.execute(() -> client
                            .getToastManager()
                            .add(SystemToast.create(
                                    client,
                                    ERR,
                                    Text.literal("Apex"),
                                    Text.literal("Failed to login to account, check console."))
                            ));
                    return null;
                }).join();
            }
        });
        t.setName("Browser login thread");
        t.setDaemon(true);
        return t;
    }
}