package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.BlockCollideEvent;
import lol.apex.event.entity.EntityBlockCollideEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.NoWebModule;
import net.minecraft.block.CobwebBlock;
import net.minecraft.util.shape.VoxelShapes;

public class SolidNoWeb extends SubModule {
    public SolidNoWeb() {
        super("Solid");
    }

    public static void onBlockCollide(NoWebModule parent, BlockCollideEvent event) {
        if (event.getBlockState().getBlock() instanceof CobwebBlock){

            event.getCallback().setReturnValue(VoxelShapes.fullCube());
        }
    }

    public static void onEntityBlockCollide(NoWebModule parent, EntityBlockCollideEvent event) {
        if (event.getBlockState().getBlock() instanceof CobwebBlock){
            event.getCallback().cancel();
        }
    }

}
