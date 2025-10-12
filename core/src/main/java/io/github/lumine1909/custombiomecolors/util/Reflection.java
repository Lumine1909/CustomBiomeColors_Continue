package io.github.lumine1909.custombiomecolors.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class Reflection {

    public static final Class<?> class$MappedRegistry = clazz("net.minecraft.core.MappedRegistry");
    public static final Class<?> class$PalettedContainer = clazz("net.minecraft.world.level.chunk.PalettedContainer");
    public static final Class<?> class$PalettedContainer$Data = clazz("net.minecraft.world.level.chunk.PalettedContainer$Data");
    public static final Class<?> class$SingleValuePalette = clazz("net.minecraft.world.level.chunk.SingleValuePalette");
    public static final Class<?> class$LinearPalette = clazz("net.minecraft.world.level.chunk.LinearPalette");
    public static final Class<?> class$HashMapPalette = clazz("net.minecraft.world.level.chunk.HashMapPalette");
    public static final Class<?> class$ClientboundLevelChunkPacketData = clazz("net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData");
    public static final Class<?> class$LevelChunkSection = clazz("net.minecraft.world.level.chunk.LevelChunkSection");
    public static final Class<?> class$Holder$Reference = clazz("net.minecraft.core.Holder$Reference");
    public static final Class<?> class$Biome = clazz("net.minecraft.world.level.biome.Biome");
    public static final FieldAccessor field$MappedRegistry$frozen = new FieldAccessor(class$MappedRegistry, "frozen");
    public static final FieldAccessor field$MappedRegistry$unregisteredIntrusiveHolders = new FieldAccessor(class$MappedRegistry, "unregisteredIntrusiveHolders");
    public static final FieldAccessor field$PalettedContainer$data = new FieldAccessor(class$PalettedContainer, "data");
    public static final FieldAccessor field$PalettedContainer$Data$storage = new FieldAccessor(class$PalettedContainer$Data, "storage");
    public static final FieldAccessor field$PalettedContainer$Data$palette = new FieldAccessor(class$PalettedContainer$Data, "palette");
    public static final FieldAccessor field$ClientboundLevelChunkPacketData$buffer = new FieldAccessor(class$ClientboundLevelChunkPacketData, "buffer");
    public static final FieldAccessor field$LevelChunkSection$nonEmptyBlockCount = new FieldAccessor(class$LevelChunkSection, "nonEmptyBlockCount");
    public static final FieldAccessor field$SingleValuePalette$value = new FieldAccessor(class$SingleValuePalette, "value");
    public static final FieldAccessor field$LinearPalette$values = new FieldAccessor(class$LinearPalette, "values");
    public static final FieldAccessor field$HashMapPalette$values = new FieldAccessor(class$HashMapPalette, "values");
    public static final FieldAccessor field$Biome$specialEffects = new FieldAccessor(class$Biome, "specialEffects");
    public static final MethodAccessor method$Holder$bindTags = new MethodAccessor(class$Holder$Reference, "bindTags", Collection.class);

    public static Class<?> clazz(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class FieldAccessor {

        private final Field field;

        public FieldAccessor(Class<?> clazz, String fieldName) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> T get(Object obj) {
            try {
                return (T) field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public void set(Object obj, Object value) {
            try {
                field.set(obj, value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static class MethodAccessor {

        private final Method method;

        public MethodAccessor(Class<?> clazz, String name, Class<?>... args) {
            try {
                method = clazz.getDeclaredMethod(name, args);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> T invoke(Object instance, Object... objects) {
            try {
                return (T) method.invoke(instance, objects);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}