package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Collection;
import java.util.Map;


public class ServerDataHandler_1_21_11 implements ServerDataHandler<Biome, Holder<Biome>, ResourceKey<Biome>> {

    static final Map<ColorType, EnvironmentAttribute<Integer>> COLOR_ATTRIBUTE = Map.of(
        ColorType.SKY, EnvironmentAttributes.SKY_COLOR,
        ColorType.FOG, EnvironmentAttributes.FOG_COLOR,
        ColorType.WATER_FOG, EnvironmentAttributes.WATER_FOG_COLOR,
        ColorType.CLOUD, EnvironmentAttributes.CLOUD_COLOR,
        ColorType.SUNRISE_SUNSET, EnvironmentAttributes.SUNRISE_SUNSET_COLOR,
        ColorType.SKY_LIGHT, EnvironmentAttributes.SKY_LIGHT_COLOR
    );
    private static final MappedRegistry<Biome> BIOME_REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final Holder.Reference<Biome> PLAINS = BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromKey(BiomeKey biomeKey) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_11(BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> wrapToAccessor(Holder<Biome> biomeBase) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_11(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<Biome> holder = BIOME_REGISTRY.get(ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElse(PLAINS);

        Biome biome = holder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<Biome> resourceKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()));
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biome.getGenerationSettings())
            .mobSpawnSettings(biome.getMobSettings())
            .hasPrecipitation(biome.hasPrecipitation())
            .temperature(biome.climateSettings.temperature())
            .downfall(biome.climateSettings.downfall())
            .temperatureAdjustment(biome.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        builder.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE).waterColor(colorData.get(ColorType.WATER));
        colorData.apply(ColorType.GRASS, builder::grassColorOverride);
        colorData.apply(ColorType.FOLIAGE, builder::foliageColorOverride);
        colorData.apply(ColorType.DRY_FOLIAGE, builder::dryFoliageColorOverride);
        biomeBuilder.specialEffects(builder.build());
        EnvironmentAttributeMap.Builder attributesBuilder = EnvironmentAttributeMap.builder().putAll(biome.getAttributes());
        COLOR_ATTRIBUTE.forEach((color, attribute) -> {
            EnvironmentAttributeMap.Entry<Integer, ?> entry = biome.getAttributes().get(attribute);
            Integer defaultValue = entry == null ? null : entry.applyModifier(0);
            colorData.apply(color, v -> attributesBuilder.set(attribute, v), defaultValue);
        });
        biomeBuilder.putAttributes(attributesBuilder);
        Biome customBiome = biomeBuilder.build();

        return new BiomeAccessor_1_21_11(this.registerBiome(holder, customBiome, resourceKey), biomeData);
    }

    @Override
    public MappedRegistry<Biome> getRegistry() {
        return BIOME_REGISTRY;
    }

    @Override
    public Collection<?> getTagList(Holder<Biome> original) {
        return original.tags().toList();
    }

    @Override
    public ColorData getDimensionColor(Location location) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        EnvironmentAttributeSystem attributes = level.environmentAttributes();
        Vec3 vec3 = new Vec3(location.x(), location.y(), location.z());
        ColorData.Builder builder = new ColorData.Builder();
        COLOR_ATTRIBUTE.forEach((color, attribute) -> builder.set(color, attributes.getValue(attribute, vec3)));
        return builder.build();
    }

    /*
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void modifyEnvironmentSampler(World world) {
        ServerLevel level = ((CraftWorld) world).getHandle();
        EnvironmentAttributeSystem attributeSystem = level.environmentAttributes();
        // Map<EnvironmentAttribute<?>, ValueSampler<?>> attributeSamplers;
        Map attributeSamplers = field$EnvironmentAttributeSystem$attributeSamplers.get(attributeSystem);

        COLOR_ATTRIBUTE.values().forEach(attribute -> {
            Object sampler = attributeSamplers.get(attribute);
            List<EnvironmentAttributeLayer> layers = new ArrayList<>(field$ValueSampler$layers.get(sampler));
            layers.add((EnvironmentAttributeLayer.Positional) (_, pos, biomeInterpolator) -> {
                if (biomeInterpolator != null && attribute.isSpatiallyInterpolated()) {
                    return biomeInterpolator.applyAttributeLayer(attribute, attribute.defaultValue());
                } else {
                    Holder<Biome> noiseBiomeAtPosition = level.getBiomeManager().getNoiseBiomeAtPosition(pos.x, pos.y, pos.z);
                    return noiseBiomeAtPosition.value().getAttributes().applyModifier(attribute, attribute.defaultValue());
                }
            });
            field$ValueSampler$layers.set(sampler, List.copyOf(layers));
        });
    }
     */
}