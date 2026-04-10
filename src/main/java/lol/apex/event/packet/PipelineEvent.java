package lol.apex.event.packet;

import io.netty.channel.ChannelPipeline;

/** Used like only in the proxy manager **/
public record PipelineEvent(ChannelPipeline pipeline, boolean local) {
}
