package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.entity.EntityRemovedEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@ModuleInfo(
        name = "Insults",
        description = "Insults players on kill.",
        category = Category.OTHER
)
public final class InsultsModule extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.TEXT);
    private final EnumSetting<InsultsType> insultsType = new EnumSetting<>("Insults Type", InsultsType.APEX);

    @RequiredArgsConstructor
    private enum Mode {
        ENTITY_REMOVAL("Entity removal"),
        TEXT("Text");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    private enum InsultsType {
        APEX("Apex"),
        NERDYASS("Nerdyass"), //on youtube btw
        BBQCHICKENALERT("BarbequeChickenAlert"),
        GOON("Goon"),
        JEFFREY_EPSTEIN("player instanceof JeffreyEpstein (= true)"),
        BACKEND("Leak in my backend");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private final Random random = new Random();
    public Entity currentTarget;
    public String lastVictimName;
    private int kills = 1;

    @SuppressWarnings("unused")
    @EventHook
    private void onWorldChange(WorldChangeEvent ignored) {
        kills = 1;
    }

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;
        if (!(event.getPacket() instanceof GameMessageS2CPacket packet)) return;
        if (mode.getValue() != Mode.TEXT) return;

        String[] killWords = {
                "Eliminating a player",
                "was slain by " + mc.player.getNameForScoreboard(),
                "was killed by " + mc.player.getNameForScoreboard()
        };

        String message = packet.content().getString();

        for (String keywords : killWords) {
            if (message.contains(keywords)) {
                lastVictimName = message.split(" ")[1];

                List<String> list = List.copyOf(getInsults());

                String insultMsg = list.get(random.nextInt(list.size()));
                mc.player.networkHandler.sendChatMessage(insultMsg);
            }
        }
    }

    @EventHook
    public void onRemoved(EntityRemovedEvent event) {
        if (mode.getValue() == Mode.ENTITY_REMOVAL) {
            if (mc.player == null) return;
            Entity entity = event.getEntity();

            if (!(entity instanceof PlayerEntity victim)) return;
            if (victim == mc.player) return;

            if (victim.getRecentDamageSource() == null) return;
            if (victim.getRecentDamageSource().getAttacker() != mc.player) return;

            currentTarget = victim;

            List<String> list = List.copyOf(getInsults());

            String message = list.get(random.nextInt(list.size()));
            mc.player.networkHandler.sendChatMessage(message);
        }
    }

    private String getVictim() {
        return currentTarget != null ? currentTarget.getName().getString() : (lastVictimName != null ? lastVictimName : "someone");
    }

    private List<String> formattedListOf(String arg, String... l) {
        return Arrays.stream(l).map(a -> String.format(a, arg)).toList();
    }


    private List<String> getInsults() {
        return switch (insultsType.getValue()) {
            case APEX -> List.of(
                    "Why waste another game without " + Apex.getName() + "?",
                    "are you bad? get " + Apex.getName() + " @ " + Apex.WEBSITE,
                    "Get Good, Get " + Apex.getName() + "!"
            );
            case NERDYASS -> List.of(
                    "LOL " + getVictim() + " GOT SNIPED BY NERDYASS ON YOUTUBE"
            );
            case BBQCHICKENALERT -> List.of(getVictim() + " barbecue chicken alert " + kills++);
            case GOON -> formattedListOf(getVictim(),
                    "%s, fuck me harder daddy", "deeper! daddy deeper! %s", "%s, Fuck yes you're so big!", "I love your cock %s!",
                    "Do not stop fucking my ass before i cum!", "Oh you're so hard for me %s", "Want to widen my ass up %s?",
                    "I love you daddy", "Make my pussy pop %s", "%s loves my pussy so much", "i made %s cum so hard with my tight pussy",
                    "Your cock is so big and juicy daddy!", "%s, Please fuck me as hard as you can", "im %s's personal femboy cumdumpster!",
                    "%s, Please shoot your hot load deep inside me daddy!",
                    "I love how %s's dick feels inside of me!", "%s gets so hard when he sees my ass!",
                    "%s really loves fucking my ass really hard!",
                    "Be a good boy for daddy %s",
                    "I love pounding your ass %s!",
                    "Give your pussy to daddy %s!",
                    "I love how you drip pre-cum while i fuck your ass %s",
                    "Slurp up and down my cock like a good boy %s",
                    "Come and jump on daddy's cock %s",
                    "I love how you look at me while you suck me off %s",
                    "%s looks so cute when i fuck him",
                    "%s's pussy is so incredibly tight!",
                    "%s takes dick like the good boy he is",
                    "I love how you shake your ass on my dick %s",
                    "%s moans so cutely when i fuck his ass",
                    "%s is the best cumdupster there is!",
                    "%s is always horny and ready for his daddy's dick",
                    "My dick gets rock hard every time i see %s"
            );
            case JEFFREY_EPSTEIN -> List.of("%s instanceof JeffreyEpstein (= true)");
            case BACKEND -> formattedListOf(getVictim(),
                    "I leaked in %s's backend!", "%s forgot to leak in my backend",
                    "%s's frontend is very tight.", "I put my frontend into %s's backend.",
                    "I wish %s could leak in my backend..."
            );
//            default -> Collections.emptyList();
        };
    }
}
