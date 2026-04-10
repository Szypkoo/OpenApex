package lol.apex.feature.ui.screen;

import lol.apex.Apex;
import lol.apex.util.CommonUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// TODO(RouteWarningScreen): Skip warning option
public class RouteWarningScreen extends WarningScreen {
    private static final Text TITLE_TEXT = MutableText
            .of(PlainTextContent.of("Apex supports proxies"))
            .formatted(Formatting.BOLD)
            .withColor(CommonUtil.getFirstClientColor());

    private final ServerInfo info;

    public enum RouteType {
        LIQUID_PROXY("LiquidProxy"),
        BIT_PROXY("BitProxy");
        public final String name;

        RouteType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private final Screen parent;

    public RouteWarningScreen(Screen parent, RouteType type, ServerInfo info) {
        super(TITLE_TEXT, Text.of(String.format("""
You are connecting to a server through a %s route.
While %s does support connecting to routes or any normal Minecraft server, proxy providers like %s has a proxy option.
When used, %s will route your all of your server-related traffic through the proxy like a route.
We recommend you use the proxy manager found in the multiplayer menu.
Why use proxies instead of routes?
- They apply to ALL servers you connect to.
- They can't be detected via `prevent-proxy-connections` being set to true in server.properties in a vanilla server
  (auth traffic gets sent through proxy)
- No need to create a route for every single server you want to connect to via LiquidProxy.
""", type, Apex.getName(), type, Apex.getName())), TITLE_TEXT);
        this.info = info;
        this.parent = parent;
    }

    @Override
    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.PROCEED, button -> {
            ConnectScreen.connect(
                    this, this.client,
                    ServerAddress.parse(this.info.address), this.info,
                    false, null
            );
        }).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, button -> client.setScreen(this.parent)).build());
        return directionalLayoutWidget;
    }
}
