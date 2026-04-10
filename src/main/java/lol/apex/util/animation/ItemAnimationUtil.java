package lol.apex.util.animation;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.AnimationsModule;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ItemAnimationUtil {
    @Getter @Setter
    public static boolean isBlocking;

    @Getter
    @Setter
    public static ItemStack spoofedItem;

    public static float height = -0.1f;

    public static void animate(MatrixStack matrix, float swingProgress, float f) {
        float sine = (float) Math.sin(MathHelper.sqrt(swingProgress) * Math.PI);

        AnimationsModule animationsModule = Apex.moduleManager.getByClass(AnimationsModule.class);

        switch (animationsModule.mode.getValue()) {
            case EXHIBITION -> {
                matrix.translate(0.1, 0, -0.1);
                matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-sine * 50));
                matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-sine * 30));
                break;
            }

            case VANILLA -> {
                matrix.translate(0.1, 0, -0.1);
                matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f + f * -20.0f));
                matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sine * -20.0f));
                matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sine * -80.0f));
                matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f));
                break;
            }
        }
    }
}
