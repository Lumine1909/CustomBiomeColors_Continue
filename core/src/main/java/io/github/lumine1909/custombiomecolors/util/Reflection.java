package io.github.lumine1909.custombiomecolors.util;

import io.github.lumine1909.reflexion.Field;
import io.github.lumine1909.reflexion.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
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
    public static final Field<Boolean> field$MappedRegistry$frozen = Field.of(class$MappedRegistry, "frozen");
    public static final Field<Map> field$MappedRegistry$unregisteredIntrusiveHolders = Field.of(class$MappedRegistry, "unregisteredIntrusiveHolders");
    public static final Field<?> field$PalettedContainer$data = Field.of(class$PalettedContainer, "data");
    public static final Field<?> field$PalettedContainer$Data$storage = Field.of(class$PalettedContainer$Data, "storage");
    public static final Field<?> field$PalettedContainer$Data$palette = Field.of(class$PalettedContainer$Data, "palette");
    public static final Field<byte[]> field$ClientboundLevelChunkPacketData$buffer = Field.of(class$ClientboundLevelChunkPacketData, "buffer");
    public static final Field<Short> field$LevelChunkSection$nonEmptyBlockCount = Field.of(class$LevelChunkSection, "nonEmptyBlockCount");
    public static final Field<?> field$SingleValuePalette$value = Field.of(class$SingleValuePalette, "value");
    public static final Field<?> field$LinearPalette$values = Field.of(class$LinearPalette, "values");
    public static final Field<?> field$HashMapPalette$values = Field.of(class$HashMapPalette, "values");
    public static final Method<Void> method$Holder$bindTags = Method.of(class$Holder$Reference, "bindTags", void.class, Collection.class);

    // Optional value for higher version
    public static final Field<Map> field$EnvironmentAttributeSystem$attributeSamplers = Field.of("net.minecraft.world.attribute.EnvironmentAttributeSystem", "attributeSamplers", true);
    public static final Field<List> field$ValueSampler$layers = Field.of("net.minecraft.world.attribute.EnvironmentAttributeSystem$ValueSampler", "layers", true);

    public static Class<?> clazz(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}