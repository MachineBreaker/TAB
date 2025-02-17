package me.neznamy.tab.platforms.velocity.features;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AllArgsConstructor;
import me.neznamy.tab.platforms.velocity.VelocityTAB;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.features.redis.RedisSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Redis implementation for Velocity
 */
@AllArgsConstructor
public class VelocityRedisSupport extends RedisSupport {

    /** Plugin reference for registering listener */
    @NotNull private final VelocityTAB plugin;
    @NotNull private final ProxyServer server;

    @Subscribe
    public void onMessage(PubSubMessageEvent e) {
        if (!e.getChannel().equals(TabConstants.REDIS_CHANNEL_NAME)) return;
        processMessage(e.getMessage());
    }

    @Override
    public void register() {
        server.getEventManager().register(plugin, this);
        RedisBungeeAPI.getRedisBungeeApi().registerPubSubChannels(TabConstants.REDIS_CHANNEL_NAME);
    }

    @Override
    public void unregister() {
        server.getEventManager().unregisterListener(plugin, this);
        RedisBungeeAPI.getRedisBungeeApi().unregisterPubSubChannels(TabConstants.REDIS_CHANNEL_NAME);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage(TabConstants.REDIS_CHANNEL_NAME, message);
    }
}