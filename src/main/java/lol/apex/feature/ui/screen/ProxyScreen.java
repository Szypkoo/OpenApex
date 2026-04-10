package lol.apex.feature.ui.screen;

import dev.toru.clients.eventBus.EventHook;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import lol.apex.Apex;
import lol.apex.event.packet.PipelineEvent;
import lol.apex.feature.file.impl.ProxiesFile;
import lol.apex.feature.proxies.Proxy;
import lol.apex.feature.proxies.ProxyHolder;
import lol.apex.feature.proxies.ProxyWithAuth;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import lol.apex.feature.ui.imgui.ImGuiThemes;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public final class ProxyScreen extends ImGuiScreen {

    private boolean addingProxy = false;
    private final ImString nameInput = new ImString(150);
    // host:port
    private final ImString addressInput = new ImString(500);
    private final ImString usernameInput = new ImString(2048);
    private final ImString passwordInput = new ImString(2048);
    private final ImBoolean useAuth = new ImBoolean(false);
    private final ImInt type = new ImInt(0);

    public static final SystemToast.Type ERR = new SystemToast.Type(500L);

    public ProxyScreen() {
        super(Text.empty());
    }

    private void clearInputs() {
        usernameInput.clear();
        passwordInput.clear();
        addressInput.clear();
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        ImGuiThemes.apply();

        ImGui.setNextWindowSize(550f, 400f, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Proxy Manager", ImGuiWindowFlags.NoCollapse)) {

            if (ImGui.beginTable("Proxies", 3, ImGuiTableFlags.Borders | ImGuiTableFlags.Resizable)) {
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Type");
                ImGui.tableSetupColumn("Actions");
                ImGui.tableHeadersRow();

                final var proxiesCopy = new ArrayList<>(ProxyHolder.proxies);

                for (final var proxy : proxiesCopy) {
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.text(proxy.name());

                    ImGui.tableNextColumn();
                    ImGui.text(proxy.type().name);

                    ImGui.tableNextColumn();
                    if (ImGui.button("Connect##" + proxy.name())) {
                        ProxyHandler.INSTANCE.currentProxy = proxy;
                        client.execute(() -> client.getToastManager()
                                .add(SystemToast.create(
                                        client,
                                        ERR,
                                        Text.literal("Apex"),
                                        Text.literal("Connected to proxy!"))
                                ));
                        Apex.LOGGER.info("Connected to proxy {}", proxy.name());
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Remove##" + proxy.name())) {
                        if (proxy == ProxyHandler.INSTANCE.currentProxy) {
                            ProxyHandler.INSTANCE.currentProxy = null;
                        }
                        client.execute(() -> client.getToastManager()
                                .add(SystemToast.create(
                                        client,
                                        ERR,
                                        Text.literal("Apex"),
                                        Text.literal("Removed proxy!"))
                                ));
                        ProxyHolder.proxies.remove(proxy);
                        ProxiesFile.DEFAULT.saveToFile();
                    }
                }
                ImGui.endTable();
            }

            ImGui.separator();

            if (ImGui.button("Add Proxy")) {
                addingProxy = true;
            }
            if (ImGui.button("Import proxy from clipboard")) {
                final var clipboard = GLFW.glfwGetClipboardString(client.getWindow().getHandle());
                if (clipboard == null) {
                    client.execute(() -> client.getToastManager()
                            .add(SystemToast.create(
                                    client,
                                    ERR,
                                    Text.literal("Apex"),
                                    Text.literal("clipboard = null"))
                            ));
                } else {
                    Apex.LOGGER.info("clipboard = {}", clipboard);
                    final var parsed = Proxy.parse(clipboard);
                    if (parsed != null) {
                        ProxyHolder.proxies.add(parsed);
                        ProxiesFile.DEFAULT.saveToFile();
                    }
                }
            }

            ImGui.sameLine();
            if (ProxyHandler.INSTANCE.currentProxy != null && ImGui.button("Disconnect from proxy")) {
                ProxyHandler.INSTANCE.currentProxy = null;
            }

            if (addingProxy) {
                ImGui.setNextWindowSize(400, 150, ImGuiCond.FirstUseEver);
                if (ImGui.begin("Add New Proxy", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.Modal)) {

                    final var proxyTypes = Proxy.Type.values();
                    final var values = Arrays.stream(proxyTypes).map(Enum::toString).toArray(String[]::new);
                    ImGui.combo("##Type", type, values);

                    ImGui.inputText("Name##name", nameInput);
                    ImGui.inputText("host:port##address", addressInput);
                    final var proxyType = proxyTypes[type.get()];
                    ImGui.checkbox("Authenticate##authentication", useAuth);
                    if (useAuth.get() && ImGui.collapsingHeader("Credentials")) {
                        if (proxyType.passwords) {
                            // this is literally the only way of auth for SOCKS4, you cannot auth any other way.
                            ImGui.inputText("Username##username", usernameInput, ImGuiInputTextFlags.Password);
                        } else {
                            ImGui.inputText("Username##username", usernameInput);
                            ImGui.inputText("Password##password", passwordInput, ImGuiInputTextFlags.Password);
                        }
                    }

                    ImGui.spacing();

                    if (ImGui.button("Add & Connect")) {
                        final var name = nameInput.get();
                        final var hostPort = addressInput.get();
                        final var split = hostPort.split(":", 2);
                        final var host = split[0];
                        final var port = Integer.valueOf(split[1], 10);
                        final var credentials = useAuth.get() && proxyType.passwords
                                ? new ProxyWithAuth.Credentials(usernameInput.get(), passwordInput.get())
                                : null;

                        final var proxy = proxyType.construct(name, credentials, new InetSocketAddress(host, port));

                        ProxyHolder.proxies.add(proxy);
                        ProxiesFile.DEFAULT.saveToFile();

                        addingProxy = false;
                        clearInputs();
                    }

                    ImGui.sameLine();
                    if (ImGui.button("Cancel")) {
                        addingProxy = false;
                        clearInputs();
                    }

                    ImGui.end();
                }
            }
        }
        ImGui.end();
    }

    @NullMarked
    public static final class ProxyHandler {
        public static final ProxyHandler INSTANCE = new ProxyHandler();
        public @Nullable Proxy currentProxy;

        @SuppressWarnings("unused")
        @EventHook
        private void hookPipeline(PipelineEvent e) {
            if (e.local()) return;
            final var pipeline = e.pipeline();

            if (pipeline.get("proxy") == null) {
                // holy hot garbage, Kotlin is better.
                final var handler = Optional.ofNullable(currentProxy).map(Proxy::handler).orElse(null);
                if (handler == null) return;
                pipeline.addFirst("proxy", handler);
            }
        }
    }
}