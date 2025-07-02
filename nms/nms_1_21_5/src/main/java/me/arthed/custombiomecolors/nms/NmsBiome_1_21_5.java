package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.ReflectionUtil;
import me.arthed.custombiomecolors.utils.objects.BiomeData;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import me.arthed.custombiomecolors.utils.objects.ColorData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

public class NmsBiome_1_21_5 extends NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> {

    public NmsBiome_1_21_5(Holder<Biome> biomeHolder) {
        this(biomeHolder, fetchNmsBiomeData(biomeHolder));
    }

    public NmsBiome_1_21_5(Holder<Biome> biomeHolder, BiomeData cachedData) {
        super(biomeHolder, biomeHolder.value(), cachedData);
    }

    private static BiomeData fetchNmsBiomeData(Holder<Biome> nmsBiome) {
        BiomeSpecialEffects biomeFog = ReflectionUtil.getPrivateObject(nmsBiome.value(), "specialEffects");
        assert biomeFog != null;
        ColorData colorData = new ColorData.Mutable()
            .grass(biomeFog.getGrassColorOverride())
            .foliage(biomeFog.getFoliageColorOverride())
            .dryFoliage(biomeFog.getDryFoliageColorOverride())
            .water(biomeFog.getWaterColor())
            .waterFog(biomeFog.getWaterFogColor())
            .sky(biomeFog.getSkyColor())
            .fog(biomeFog.getFogColor())
            .build();
        BiomeKey biomeKey = BiomeKey.fromString(nmsBiome.getRegisteredName());
        return new BiomeData(biomeKey, biomeKey, colorData);
    }

    public NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> cloneWithDifferentColor(NmsServer<Biome, Holder<Biome>, ResourceKey<Biome>> nmsServer, BiomeKey newBiomeKey, ColorData colorData) {
        ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(newBiomeKey.key(), newBiomeKey.value()));
        Biome.BiomeBuilder customBiomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(getBiome().getGenerationSettings())
            .mobSpawnSettings(getBiome().getMobSettings())
            .hasPrecipitation(getBiome().hasPrecipitation())
            .temperature(getBiome().climateSettings.temperature())
            .downfall(getBiome().climateSettings.downfall())
            .temperatureAdjustment(getBiome().climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder customBiomeColors = new BiomeSpecialEffects.Builder();
        customBiomeColors.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
            .waterColor(colorData.waterColor())
            .waterFogColor(colorData.waterFogColor())
            .skyColor(colorData.skyColor())
            .fogColor(colorData.fogColor());
        if (colorData.grassColor().isPresent()) {
            customBiomeColors.grassColorOverride(colorData.grassColor().get());
        }
        if (colorData.foliageColor().isPresent()) {
            customBiomeColors.foliageColorOverride(colorData.foliageColor().get());
        }

        customBiomeBuilder.specialEffects(customBiomeColors.build());
        Biome customBiome = customBiomeBuilder.build();
        Holder<Biome> holder = nmsServer.registerBiome(getBiomeHolder(), customBiome, customBiomeKey);

        return new NmsBiome_1_21_5(holder, new BiomeData(newBiomeKey, this.cachedData.baseBiomeKey(), colorData));
    }

    @Override
    public float getTemperature() {
        return biomeBase.climateSettings.temperature();
    }

    @Override
    public float getHumidity() {
        return biomeBase.climateSettings.downfall();
    }
}