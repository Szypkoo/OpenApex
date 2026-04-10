package lol.apex.feature.module.implementation.combat;


import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Module; 
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.player.InventoryUtil.FindItemResult;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "AutoTotem",
    description = "Automatically equips a totem in your offhand.",
    category = Category.COMBAT
)
public class AutoTotemModule extends Module {
    private EnumSetting<Mode> mode = new EnumSetting<AutoTotemModule.Mode>("Mode", Mode.LEGIT);

    @RequiredArgsConstructor
    private enum Mode {
        LEGIT("Legit"),
        AUTO("Auto");
    //    Hover

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    } 

    private final int offhand = 40;

    private boolean waitingForMove; 
    private boolean manuallyMoved;
    private Screen lastScreen; 

    @EventHook
    private void onTick(ClientTickEvent event) {
        if(mc.player == null || mc.world == null) return; 

        if (mc.currentScreen == null && lastScreen != null) {
            manuallyMoved = false; 
            waitingForMove = true;
        } 

        lastScreen = mc.currentScreen; 
        var result = InventoryUtil.findItem(Items.TOTEM_OF_UNDYING); 
        if(mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        } 
        if(!result.found()) return; 

        switch(mode.getValue()) {
            case LEGIT -> legitAutoTotem(result);
            case AUTO -> blatentAutoTotem(result);
        }
    }

    private void blatentAutoTotem(FindItemResult result) {
        if(result.found()) {
            InventoryUtil.move(result.slot(), offhand);
        }
    }

    private void legitAutoTotem(FindItemResult result) {
        if(!manuallyMoved) waitingForMove = true; 
        if(waitingForMove && mc.currentScreen instanceof InventoryScreen) {
            InventoryUtil.move(result.slot(), offhand); 

            waitingForMove = false;
            manuallyMoved = true;
        }
    }    

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}