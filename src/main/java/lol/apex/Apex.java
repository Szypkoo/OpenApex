package lol.apex;

import dev.toru.clients.eventBus.EventBus;
import dev.toru.clients.eventBus.InitializeFabricEvents;
import lol.apex.feature.file.impl.*;
import lol.apex.feature.ui.hud.HudRenderer;
import lol.apex.feature.ui.notification.NotificationRenderer;
import lol.apex.feature.waypoint.WaypointRenderer;
import lol.apex.feature.ui.screen.ProxyScreen;
import lol.apex.manager.implementation.*;
import lol.apex.feature.anticheat.AntiCheatManager;
import lol.apex.util.game.ChatUtil;
import lol.apex.util.CommonUtil;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Apex implements ModInitializer {
	public static final String MOD_ID = "apex";
	public static final String WEBSITE = "getapex.club";
	public static final String VERSION = "v1.0";
	public static final String GITHUB_COMMIT = CommonUtil.getCommit();

	public static final Logger LOGGER = LoggerFactory.getLogger("Apex");
	public static final boolean BETA = false;

	public static String getName() {
		if (BETA) {
			return "Apex Beta";
		} else {
			return "Apex";
		} 
	}

	public static String getWindowTitle() {
		return getName() + " " + VERSION + " (" + GITHUB_COMMIT + ") @ " + WEBSITE;
	}

	public static Apex instance = new Apex();
	public static EventBus eventBus = new EventBus();

	public static ModuleManager moduleManager;
	public static BlinkManager blinkManager;
	public static CommandManager commandManager;
	public static AntiCheatManager antiCheatManager;
	public static FriendManager friendManager;
	public static SoundManager soundManager;

	public static HudRenderer hudRenderer;
	public static WaypointRenderer waypointRenderer;
	public static NotificationRenderer notificationRenderer;

	@Override
	public void onInitialize() {
        LOGGER.info("Starting {}!", getName());

		long startTime = System.nanoTime();

		InitializeFabricEvents.initialize();

		moduleManager = new ModuleManager();
		blinkManager = new BlinkManager();
		commandManager = new CommandManager();
		antiCheatManager = new AntiCheatManager();
		friendManager = new FriendManager();
		soundManager = new SoundManager();

		hudRenderer = new HudRenderer();
		notificationRenderer = new NotificationRenderer();
		waypointRenderer = new WaypointRenderer();

		moduleManager.initialize();
		soundManager.initialize();
		friendManager.initialize();
		hudRenderer.initialize();
		waypointRenderer.initialize();

		ProxiesFile.DEFAULT.loadFromFile();
		eventBus.subscribe(blinkManager);
		eventBus.subscribe(antiCheatManager);
		eventBus.subscribe(hudRenderer);
		eventBus.subscribe(ProxyScreen.ProxyHandler.INSTANCE);
		eventBus.subscribe(notificationRenderer);

        LOGGER.info("Apex initialized.");
	}

	public void stop() {
		ModulesFile.DEFAULT.saveToFile();
		HudFile.DEFAULT.saveToFile();
		WaypointsFile.DEFAULT.saveToFile();
		FriendsFile.DEFAULT.saveToFile();
		LOGGER.info("Saved preferences and configs on close!");
	}

	public static void sendChatMessage(String message) {
		ChatUtil.sendChatMessage(message);
	}
}