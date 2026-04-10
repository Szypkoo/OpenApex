package lol.apex.util.player;

import lol.apex.util.CommonVars;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class SlotUtil implements CommonVars {
    public static final int hotbar_start = 0; 
    public static final int hotbar_end = 8; 
    public static final int main_start = 9; 
    public static final int main_end = 35; 
    public static final int armor_start = 36; 
    public static final int armor_end = 39; 
    public static final int offhand = 40;  
    
    private SlotUtil() {

    }

    public static int indexToId(int i) {
        var player = mc.player; 
        if(player == null) return -1;

        ScreenHandler handler = player.currentScreenHandler;
        if(handler instanceof PlayerScreenHandler) return survivalInventory(i); 
        return i;
    } 

    private static int survivalInventory(int i) {
        if(isHotbar(i)) return 36 + i; 
        if(isArmor(i)) return 5 + (i - 36); 
        if(i == offhand) return 45; 
        return i;
    }

    public static boolean isHotbar(int index) {
        return index >= hotbar_start && index <= hotbar_end;
    } 

    public static boolean isMain(int index) {
        return index >= main_start && index <= main_end;
    } 

    public static boolean isArmor(int index) {
        return index >= armor_start && index <= armor_end;
    }
}
