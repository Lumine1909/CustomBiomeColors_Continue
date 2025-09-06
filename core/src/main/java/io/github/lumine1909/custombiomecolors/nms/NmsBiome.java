package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.utils.objects.BiomeData;
import io.github.lumine1909.custombiomecolors.utils.objects.BiomeKey;
import io.github.lumine1909.custombiomecolors.utils.objects.ColorData;

public abstract class NmsBiome<Biome, Holder, ResourceKey> {

    protected final Holder biomeHolder;
    protected final Biome biomeBase;
    protected final BiomeData cachedData;

    public NmsBiome(Holder biomeHolder, Biome biome, BiomeData cachedData) {
        this.biomeHolder = biomeHolder;
        this.biomeBase = biome;
        this.cachedData = cachedData;
        BiomeData.updateBiome(cachedData.colorData(), this);
    }

    public Holder getBiomeHolder() {
        return biomeHolder;
    }

    public Biome getBiome() {
        return biomeBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NmsBiome that = (NmsBiome) o;
        return that.biomeHolder.equals(this.biomeHolder) && that.cachedData.equals(this.cachedData);
    }

    public BiomeData getBiomeData() {
        return cachedData;
    }

    public abstract float getTemperature();

    public abstract float getHumidity();

    public abstract NmsBiome<Biome, Holder, ResourceKey> cloneWithDifferentColor(NmsServer<Biome, Holder, ResourceKey> nmsServer, BiomeKey newBiomeKey, ColorData newColor);
}