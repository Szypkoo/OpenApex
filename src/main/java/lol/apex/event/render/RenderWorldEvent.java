package lol.apex.event.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.toru.clients.eventBus.Event;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.render.state.WorldRenderState;
import org.joml.Matrix4f;

@RequiredArgsConstructor
public class RenderWorldEvent extends Event {
    public final GpuBufferSlice slice;
    public final WorldRenderState renderState;
    public final Matrix4f matrix;
}
