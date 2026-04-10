package lol.apex.feature.ui.imgui;

import imgui.ImFont;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ImGuiFonts {
    public static ImFont mainFont;
    private static final Set<String> PENDING_FONTS = new HashSet<>();
    private static final Map<String, String> EXTRACTED_FONTS = new HashMap<>();
    private static final Map<String, ImFont> BUILT_FONTS = new HashMap<>();

    public static void init() {
        mainFont = getFontImmediate("product-bold", 18);
        ImGui.getIO().getFonts().build();
    }

    public static void processPendingFonts(ImGuiImplGl3 gl3Backend) {
        if (PENDING_FONTS.isEmpty()) return;

        for (String key : PENDING_FONTS) {
            String[] split = key.split("#");
            String name = split[0];
            int size = Integer.parseInt(split[1]);

            String path = "/assets/apex/font/" + name + ".ttf";

            ImFont font = loadFont(path, size);
            BUILT_FONTS.put(key, font);
        }

        PENDING_FONTS.clear();

        ImGui.getIO().getFonts().build();
        gl3Backend.destroyFontsTexture();
        gl3Backend.createFontsTexture();
    }

    public static ImFont getFont(String name, int size) {
        String key = fontKey(name, size);

        ImFont font = BUILT_FONTS.get(key);
        if (font != null) {
            return font;
        }

        PENDING_FONTS.add(key);

        return mainFont;
    }

    public static ImFont getFontImmediate(String name, int size) {
        String key = fontKey(name, size);

        ImFont cached = BUILT_FONTS.get(key);
        if (cached != null) return cached;

        String path = "/assets/apex/font/" + name + ".ttf";
        ImFont font = loadFont(path, size);

        BUILT_FONTS.put(key, font);

        return font;
    }

    private static String fontKey(String name, int size) {
        return name + "#" + size;
    }

    private static ImFont loadFont(final String path, final int pixelSize) {
        return ImGui.getIO().getFonts().addFontFromFileTTF(extractFontToTempFile(path), pixelSize);
    }

    private static String extractFontToTempFile(final String resourcePath) {
        return EXTRACTED_FONTS.computeIfAbsent(resourcePath, path -> {
            final String extension = path.lastIndexOf('.') >= 0 ? path.substring(path.lastIndexOf('.')) : ".ttf";

            try (InputStream in = Objects.requireNonNull(
                    ImGuiImpl.class.getResourceAsStream(path),
                    "Font not found: " + path)) {

                final Path tempFile = Files.createTempFile("apex-imgui-font-", extension);
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                tempFile.toFile().deleteOnExit();
                return tempFile.toAbsolutePath().toString();
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to extract font from path: " + path, e);
            }
        });
    }
}