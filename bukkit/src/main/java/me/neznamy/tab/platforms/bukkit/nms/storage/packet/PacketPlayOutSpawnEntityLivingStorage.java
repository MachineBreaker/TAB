package me.neznamy.tab.platforms.bukkit.nms.storage.packet;

import lombok.SneakyThrows;
import me.neznamy.tab.platforms.bukkit.nms.datawatcher.DataWatcher;
import me.neznamy.tab.platforms.bukkit.nms.storage.nms.NMSStorage;
import me.neznamy.tab.shared.backend.EntityData;
import me.neznamy.tab.shared.backend.Location;
import me.neznamy.tab.shared.util.ReflectionUtils;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.UUID;

/**
 * Custom class for holding data used in PacketPlayOutSpawnEntityLiving minecraft packet.
 */
public class PacketPlayOutSpawnEntityLivingStorage {

    /** NMS Fields */
    public static Class<?> CLASS;
    public static Class<?> EntityTypes;
    public static Constructor<?> CONSTRUCTOR;
    public static Field ENTITY_ID;
    public static Field ENTITY_TYPE;
    public static Field YAW;
    public static Field PITCH;
    public static Field UUID;
    public static Field X;
    public static Field Y;
    public static Field Z;
    public static Field DATA_WATCHER;
    public static Object EntityTypes_ARMOR_STAND;

    /** Entity types */
    public static final EnumMap<EntityType, Integer> entityIds = new EnumMap<>(EntityType.class);

    /**
     * Loads all required Fields and throws Exception if something went wrong
     *
     * @param   nms
     *          NMS storage reference
     * @throws  NoSuchMethodException
     *          If something fails
     */
    public static void load(NMSStorage nms) throws NoSuchMethodException {
        if (nms.getMinorVersion() >= 19) {
            entityIds.put(EntityType.ARMOR_STAND, 2);
        } else if (nms.getMinorVersion() >= 13) {
            entityIds.put(EntityType.ARMOR_STAND, 1);
        } else {
            entityIds.put(EntityType.WITHER, 64);
            if (nms.getMinorVersion() >= 8) {
                entityIds.put(EntityType.ARMOR_STAND, 30);
            }
        }
        
        if (nms.getMinorVersion() >= 17) {
            CONSTRUCTOR = CLASS.getConstructor(nms.is1_19_3Plus() ? nms.Entity : nms.EntityLiving);
        } else {
            CONSTRUCTOR = CLASS.getConstructor();
        }
        ENTITY_ID = ReflectionUtils.getFields(CLASS, int.class).get(0);
        YAW = ReflectionUtils.getFields(CLASS, byte.class).get(0);
        PITCH = ReflectionUtils.getFields(CLASS, byte.class).get(1);
        if (nms.getMinorVersion() >= 9) {
            UUID = ReflectionUtils.getOnlyField(CLASS, UUID.class);
            if (nms.getMinorVersion() >= 19) {
                X = ReflectionUtils.getFields(CLASS, double.class).get(2);
                Y = ReflectionUtils.getFields(CLASS, double.class).get(3);
                Z = ReflectionUtils.getFields(CLASS, double.class).get(4);
            } else {
                X = ReflectionUtils.getFields(CLASS, double.class).get(0);
                Y = ReflectionUtils.getFields(CLASS, double.class).get(1);
                Z = ReflectionUtils.getFields(CLASS, double.class).get(2);
            }
        } else {
            X = ReflectionUtils.getFields(CLASS, int.class).get(2);
            Y = ReflectionUtils.getFields(CLASS, int.class).get(3);
            Z = ReflectionUtils.getFields(CLASS, int.class).get(4);
        }
        if (nms.getMinorVersion() < 19) {
            ENTITY_TYPE = ReflectionUtils.getFields(CLASS, int.class).get(1);
        }
        if (nms.getMinorVersion() <= 14) {
            DATA_WATCHER = ReflectionUtils.getOnlyField(CLASS, DataWatcher.CLASS);
        }
    }

    /**
     * Converts this class into NMS packet
     *
     * @return  NMS packet
     */
    @SneakyThrows
    public static Object build(int entityId, UUID id, Object entityType, Location location, EntityData data) {
        NMSStorage nms = NMSStorage.getInstance();
        Object nmsPacket;
        if (nms.getMinorVersion() >= 17) {
            nmsPacket = CONSTRUCTOR.newInstance(nms.dummyEntity);
        } else {
            nmsPacket = CONSTRUCTOR.newInstance();
        }
        ENTITY_ID.set(nmsPacket, entityId);
        YAW.set(nmsPacket, (byte)(location.getYaw() * 256.0f / 360.0f));
        PITCH.set(nmsPacket, (byte)(location.getPitch() * 256.0f / 360.0f));
        if (nms.getMinorVersion() <= 14) {
            DATA_WATCHER.set(nmsPacket, data.build());
        }
        if (nms.getMinorVersion() >= 9) {
            UUID.set(nmsPacket, id);
            X.set(nmsPacket, location.getX());
            Y.set(nmsPacket, location.getY());
            Z.set(nmsPacket, location.getZ());
        } else {
            X.set(nmsPacket, floor(location.getX()*32));
            Y.set(nmsPacket, floor(location.getY()*32));
            Z.set(nmsPacket, floor(location.getZ()*32));
        }
        if (nms.getMinorVersion() >= 19) {
            ENTITY_TYPE.set(nmsPacket, EntityTypes_ARMOR_STAND); // :(
        } else {
            ENTITY_TYPE.set(nmsPacket, entityIds.get((EntityType) entityType));
        }
        return nmsPacket;
    }

    private static int floor(double paramDouble) {
        int i = (int)paramDouble;
        return paramDouble < i ? i - 1 : i;
    }
}