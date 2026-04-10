package lol.apex.util;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.InterfaceModule;
import lombok.experimental.UtilityClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@UtilityClass
public class CommonUtil implements CommonVars {
    public static int getFirstClientColor() {
        InterfaceModule interfaceModule = Apex.moduleManager.getByClass(InterfaceModule.class);
        return interfaceModule.firstClientColor.getValue().getRGB();
    }

    public static int getSecondClientColor() {
        InterfaceModule interfaceModule = Apex.moduleManager.getByClass(InterfaceModule.class);
        return interfaceModule.secondClientColor.getValue().getRGB();
    }

    public static void log(String message) {
        Apex.LOGGER.info(message);
    }

    public static File getClientDir() {
        return new File(mc.runDirectory, "/Apex");
    }

    public static void warningSound() {
        mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(), 1.0f, 1.0f);
        mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(), 1.0f, 1.0f);
    }

    public static void setTitle(String title) {
        if (mc == null || mc.getWindow() == null) return;
        mc.getWindow().setTitle(title); 
    }

    public static void setIcon(String path) {
        try {
            InputStream stream = CommonUtil.class.getResourceAsStream("/assets/apex/" + path);
            if (stream == null) return;

            byte[] bytes = stream.readAllBytes();
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            try (MemoryStack stack = MemoryStack.stackPush()) {

                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer image = STBImage.stbi_load_from_memory(buffer, w, h, comp, 4);
                if (image == null) return;

                GLFWImage icon = GLFWImage.malloc(stack);
                icon.set(w.get(0), h.get(0), image);

                GLFWImage.Buffer icons = GLFWImage.malloc(1, stack);
                icons.put(0, icon);

                long window = mc.getWindow().getHandle();
                GLFW.glfwSetWindowIcon(window, icons);

                STBImage.stbi_image_free(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCommit() {
        var meta = FabricLoader.getInstance()
                .getModContainer("apex")
                .get()
                .getMetadata();

        return meta.getCustomValue("gitCommit").getAsString();
    }

    public String getServerIP() {
        String address = mc.getNetworkHandler().getConnection().getAddress().toString();
        return address;
    }
}
