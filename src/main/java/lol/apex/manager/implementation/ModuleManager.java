package lol.apex.manager.implementation;

import lol.apex.Apex;
import lol.apex.feature.file.impl.ModulesFile;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.implementation.combat.*;
import lol.apex.feature.module.implementation.legit.*;
import lol.apex.feature.module.implementation.movement.*;
import lol.apex.feature.module.implementation.other.*;
import lol.apex.feature.module.implementation.player.*;
import lol.apex.feature.module.implementation.visual.*;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.manager.ListManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ModuleManager extends ListManager<Module> {

    public ModuleManager() {
        // Combat
        add(new AuraRecodeModule());

        add(new FastBowModule());
        add(new CriticalsModule());
        add(new RegenModule()); // hi add vulcan mode pls
        add(new MaceDamageModule());
        add(new BacktrackModule());
        add(new FakeLagModule());
        add(new AutoTotemModule());
        add(new ElytraSwapModule());
        add(new AutoSoupModule());

        // 1.21 Combat 
        add(new AutoPearlCatchModule());
        add(new AutoMaceModule()); 
        add(new AutoCrystalModule());
        add(new ShieldBreakerModule());
        add(new AutoCartModule());
        add(new AttributeSwapModule());

        // Legit
        add(new FastUseModule());
        add(new TapModule());
        add(new AutoJumpResetModule());
        add(new ReachModule());
        add(new EagleModule());
        add(new KeepSprintModule()); // this is very VERY VERY simple
        add(new CrystalOptimizerModule());
        add(new AutoClickerModule());
        add(new AntiMissModule());

        // Movement
        add(new SprintModule());
        add(new SpeedModule());
        add(new NoWebModule());
        add(new WallClimbModule());
        add(new JesusModule());
        add(new HighJumpModule());
        add(new InventoryMoveModule());
        add(new FlyModule());
        add(new VehicleFlyModule()); 
        add(NoSlowModule.INSTANCE);
        add(new AutoHeadHitterModule());
        add(new FastStopModule());
        add(new FastClimbModule());
        add(new AutoRespawnModule());
        add(new AntiAFKModule());

        // Other
        add(new SecurityFeaturesModule());
        add(new AntiExploitModule());
        add(new MovementCorrectionModule());
        add(new DiscordRPCModule());
        add(new DisablerModule());
        add(new PingSpoofModule());
        add(new AntiBotModule());
        add(new InsultsModule());
        add(new AntiCheatModule());
        add(new IRCModule()); // make backend after delete this comment - remi
        add(new MCFModule());
        add(new LagBackDetector());
        add(new SpammerModule());
        add(new StaffDetectorModule());

        // Player
        add(new NoFallModule());
        add(new VelocityModule());
        add(new ChestStealerModule());
        add(new InventoryManagerModule());
        add(new ScaffoldModule());
        add(new AutoArmorModule());
        add(new HealthAlertModule());
        add(new AutoAuthModule());
        add(new BreakerModule());
        add(new ChestAuraModule());
        add(new TimerModule());
        add(new AntiTrapModule());
        add(new XCarryModule());
        add(new DamageModule());
        add(new SpinBotModule());
        add(new AntiHungerModule());
        add(new AutoToolModule());
        add(new DeathCoordsModule());

        // Visual
        add(new ClickGuiModule());
        add(new AnimationsModule());
        add(new InterfaceModule());
        add(new TargetHUDModule());
        add(new NametagsModule());
        add(new OutlineESPModule());
        add(new ESPModule());
        add(new NoRenderModule());
        add(new WaypointsModule());
        add(new AmbienceModule());
        add(new ResourcePackSpoofModule());
        add(new SimulatedPlayerTestModule());
        add(new CapesModule());
    }

    public void initialize() {
        ModulesFile.DEFAULT.loadFromFile();
    }

    @Override
    public void add(Module value) {
        try {
            findSettings(value);
        } catch (IllegalAccessException e) {
            Apex.LOGGER.error("Failed to load {} settings", value.getName(), e);
        }
        super.add(value);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getByClass(Class<T> clazz) {
        return (T) getFirst(m -> m.getClass().equals(clazz)).orElse(null);
    }

    public List<Module> getModulesByCategory(String category) {
        return getAll(m -> m.getCategory().toString().equals(category));
    }

    public List<Module> getModulesByCategory(Category category) {
        return getAll(m -> m.getCategory() == category);
    }

    public Optional<Module> getModuleByName(String name) {
        return getFirst(m -> m.getName().trim().equalsIgnoreCase(name.trim().toLowerCase()));
    }

    private void findSettings(Module module) throws IllegalAccessException {
        for (Field field : module.getClass().getDeclaredFields()) {
            if (BaseSetting.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                module.getBaseSettings().add((BaseSetting<?>) field.get(module));
            }
        }
    }

}
