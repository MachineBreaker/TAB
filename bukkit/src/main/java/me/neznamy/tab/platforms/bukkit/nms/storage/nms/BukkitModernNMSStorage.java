package me.neznamy.tab.platforms.bukkit.nms.storage.nms;

import me.neznamy.tab.platforms.bukkit.BukkitTabList;
import me.neznamy.tab.platforms.bukkit.nms.datawatcher.DataWatcher;
import me.neznamy.tab.platforms.bukkit.nms.datawatcher.DataWatcherHelper;
import me.neznamy.tab.platforms.bukkit.nms.datawatcher.DataWatcherItem;
import me.neznamy.tab.platforms.bukkit.nms.datawatcher.DataWatcherObject;
import me.neznamy.tab.platforms.bukkit.nms.storage.packet.*;
import me.neznamy.tab.platforms.bukkit.scoreboard.PacketScoreboard;
import me.neznamy.tab.shared.util.ReflectionUtils;

/**
 * NMS loader for minecraft 1.17+ using Mojang packaging and bukkit names.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BukkitModernNMSStorage extends NMSStorage {

    @Override
    public void loadNamedFieldsAndMethods() throws ReflectiveOperationException {
        ChatSerializer_DESERIALIZE = ChatSerializer.getMethod("a", String.class);
        DataWatcher.REGISTER = ReflectionUtils.getMethod(DataWatcher.CLASS, new String[]{"register", "a"}, DataWatcherObject.CLASS, Object.class); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardScore_setScore = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardScoreClass, new String[]{"setScore", "b"}, int.class); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardTeam_setAllowFriendlyFire = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardTeam, new String[]{"setAllowFriendlyFire", "a"}, boolean.class); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardTeam_setCanSeeFriendlyInvisibles = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardTeam, new String[]{"setCanSeeFriendlyInvisibles", "b"}, boolean.class); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardTeam_setPrefix = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardTeam, new String[]{"setPrefix", "b"}, IChatBaseComponent); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardTeam_setSuffix = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardTeam, new String[]{"setSuffix", "c"}, IChatBaseComponent); // {Bukkit, Bukkit 1.18+}
        PacketScoreboard.ScoreboardTeam_setNameTagVisibility = ReflectionUtils.getMethod(PacketScoreboard.ScoreboardTeam, new String[]{"setNameTagVisibility", "a"}, PacketScoreboard.EnumNameTagVisibility); // {Bukkit, Bukkit 1.18+}
        DataWatcherHelper.DataWatcherSerializer_BYTE = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("a").get(null);
        DataWatcherHelper.DataWatcherSerializer_FLOAT = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("c").get(null);
        DataWatcherHelper.DataWatcherSerializer_STRING = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("d").get(null);
        if (is1_19_3Plus()) {
            DataWatcherHelper.DataWatcherSerializer_OPTIONAL_COMPONENT = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("g").get(null);
            if (is1_19_4Plus()) {
                DataWatcherHelper.DataWatcherSerializer_BOOLEAN = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("k").get(null);
            } else {
                DataWatcherHelper.DataWatcherSerializer_BOOLEAN = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("j").get(null);
            }
         } else {
            DataWatcherHelper.DataWatcherSerializer_OPTIONAL_COMPONENT = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("f").get(null);
            DataWatcherHelper.DataWatcherSerializer_BOOLEAN = DataWatcherHelper.DataWatcherRegistry.getDeclaredField("i").get(null);
        }
        if (minorVersion >= 19) {
            PacketPlayOutSpawnEntityLivingStorage.EntityTypes_ARMOR_STAND = PacketPlayOutSpawnEntityLivingStorage.EntityTypes.getDeclaredField("d").get(null);
            (PacketPlayOutSpawnEntityLivingStorage.ENTITY_TYPE = PacketPlayOutSpawnEntityLivingStorage.CLASS.getDeclaredField("e")).setAccessible(true);
            DataWatcher.packDirty = DataWatcher.CLASS.getMethod("b");
        }
    }

    @Override
    public void loadClasses() throws ClassNotFoundException {
        ChatSerializer = Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
        World = Class.forName("net.minecraft.world.level.World");
        EntityArmorStand = Class.forName("net.minecraft.world.entity.decoration.EntityArmorStand");
        EntityHuman = Class.forName("net.minecraft.world.entity.player.EntityHuman");
        NetworkManager = Class.forName("net.minecraft.network.NetworkManager");
        IChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        Packet = Class.forName("net.minecraft.network.protocol.Packet");
        EnumChatFormat = (Class<Enum>) Class.forName("net.minecraft.EnumChatFormat");
        EntityPlayer = Class.forName("net.minecraft.server.level.EntityPlayer");
        Entity = Class.forName("net.minecraft.world.entity.Entity");
        EntityLiving = Class.forName("net.minecraft.world.entity.EntityLiving");
        PlayerConnection = Class.forName("net.minecraft.server.network.PlayerConnection");
        BukkitTabList.PacketPlayOutPlayerListHeaderFooterClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter");
        PacketScoreboard.DisplayObjectiveClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective");
        PacketScoreboard.ObjectivePacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective");
        PacketScoreboard.Scoreboard = Class.forName("net.minecraft.world.scores.Scoreboard");
        PacketScoreboard.ScorePacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore");
        PacketScoreboard.ScoreboardObjective = Class.forName("net.minecraft.world.scores.ScoreboardObjective");
        PacketScoreboard.ScoreboardScoreClass = Class.forName("net.minecraft.world.scores.ScoreboardScore");
        PacketScoreboard.IScoreboardCriteria = Class.forName("net.minecraft.world.scores.criteria.IScoreboardCriteria");
        PacketScoreboard.EnumScoreboardHealthDisplay = (Class<Enum>) Class.forName("net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay");
        PacketScoreboard.EnumScoreboardAction = (Class<Enum>) Class.forName("net.minecraft.server.ScoreboardServer$Action");
        PacketScoreboard.TeamPacketClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam");
        PacketScoreboard.ScoreboardTeam = Class.forName("net.minecraft.world.scores.ScoreboardTeam");
        PacketScoreboard.EnumNameTagVisibility = (Class<Enum>) Class.forName("net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
        PacketScoreboard.EnumTeamPush = (Class<Enum>) Class.forName("net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush");
        DataWatcher.CLASS = Class.forName("net.minecraft.network.syncher.DataWatcher");
        DataWatcherItem.CLASS = Class.forName("net.minecraft.network.syncher.DataWatcher$Item");
        DataWatcherObject.CLASS = Class.forName("net.minecraft.network.syncher.DataWatcherObject");
        DataWatcherHelper.DataWatcherRegistry = Class.forName("net.minecraft.network.syncher.DataWatcherRegistry");
        DataWatcherHelper.DataWatcherSerializer = Class.forName("net.minecraft.network.syncher.DataWatcherSerializer");
        PacketPlayOutEntityTeleportStorage.CLASS = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
        PacketPlayOutEntity = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntity");
        PacketPlayOutEntityDestroyStorage.CLASS = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
        PacketPlayOutEntityLook = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook");
        PacketPlayOutEntityMetadataStorage.CLASS = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
        PacketPlayOutNamedEntitySpawn = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn");
        BukkitTabList.EnumGamemodeClass = (Class<Enum>) Class.forName("net.minecraft.world.level.EnumGamemode");
        if (minorVersion >= 19) {
            PacketPlayOutSpawnEntityLivingStorage.EntityTypes = Class.forName("net.minecraft.world.entity.EntityTypes");
            PacketPlayOutSpawnEntityLivingStorage.CLASS = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
        } else {
            PacketPlayOutSpawnEntityLivingStorage.CLASS = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
        }
        if (is1_19_3Plus()) {
            BukkitTabList.ClientboundPlayerInfoRemovePacket = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket");
            BukkitTabList.PacketPlayOutPlayerInfoClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
            BukkitTabList.EnumPlayerInfoActionClass = (Class<Enum>) Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$a");
            BukkitTabList.PlayerInfoDataClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$b");
            BukkitTabList.RemoteChatSession$Data = Class.forName("net.minecraft.network.chat.RemoteChatSession$a");
        } else {
            BukkitTabList.PacketPlayOutPlayerInfoClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
            BukkitTabList.EnumPlayerInfoActionClass = (Class<Enum>) Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            BukkitTabList.PlayerInfoDataClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");
        }
    }
}
