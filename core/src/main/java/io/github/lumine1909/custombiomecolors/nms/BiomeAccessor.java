package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;

@SuppressWarnings("rawtypes")
public abstract class BiomeAccessor<Biome, Holder, ResourceKey> {

    protected final Holder biomeHolder;
    protected final Biome biome;
    protected final BiomeData cachedData;

    public BiomeAccessor(Holder biomeHolder, Biome biome, BiomeData cachedData) {
        this.biomeHolder = biomeHolder;
        this.biome = biome;
        this.cachedData = cachedData;
        BiomeData.updateBiome(cachedData.colorData(), this);
    }

    public BiomeAccessor(Biome tempBiome, BiomeData cachedData) {
        this.biomeHolder = null;
        this.biome = tempBiome;
        this.cachedData = cachedData;
    }

    public Holder getBiomeHolder() {
        return biomeHolder;
    }

    public Biome getBiome() {
        return biome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiomeAccessor that = (BiomeAccessor) o;
        return that.biomeHolder.equals(this.biomeHolder) && that.cachedData.equals(this.cachedData);
    }

    public BiomeData getBiomeData() {
        return cachedData;
    }

    public BiomeAccessor<Biome, Holder, ResourceKey> cloneWithDifferentColor(ServerDataHandler<Biome, Holder, ResourceKey> serverDataHandler, BiomeKey newBiomeKey, ColorData newColor) {
        return serverDataHandler.createCustomBiome(new BiomeData(newBiomeKey, cachedData.baseBiomeKey(), newColor), true);
    }

    public abstract float getTemperature();

    public abstract float getHumidity();
}