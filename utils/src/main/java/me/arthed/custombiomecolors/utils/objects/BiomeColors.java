package me.arthed.custombiomecolors.utils.objects;

public class BiomeColors {

    private int grassColor;
    private int foliageColor;
    private int waterColor;
    private int waterFogColor;
    private int skyColor;
    private int fogColor;

    public void setColor(BiomeColorType colorType, int color) {
        if (colorType.equals(BiomeColorType.GRASS)) {
            this.setGrassColor(color);
        } else if (colorType.equals(BiomeColorType.FOLIAGE)) {
            this.setFoliageColor(color);
        } else if (colorType.equals(BiomeColorType.WATER)) {
            this.setWaterColor(color);
        } else if (colorType.equals(BiomeColorType.WATER_FOG)) {
            this.setWaterFogColor(color);
        } else if (colorType.equals(BiomeColorType.SKY)) {
            this.setSkyColor(color);
        } else if (colorType.equals(BiomeColorType.FOG)) {
            this.setFogColor(color);
        }
    }

    public int getGrassColor() {
        return grassColor;
    }

    public BiomeColors setGrassColor(int grassColor) {
        this.grassColor = grassColor;
        return this;
    }

    public int getFoliageColor() {
        return foliageColor;
    }

    public BiomeColors setFoliageColor(int foliageColor) {
        this.foliageColor = foliageColor;
        return this;
    }

    public int getWaterColor() {
        return waterColor;
    }

    public BiomeColors setWaterColor(int waterColor) {
        this.waterColor = waterColor;
        return this;
    }

    public int getWaterFogColor() {
        return waterFogColor;
    }

    public BiomeColors setWaterFogColor(int waterFogColor) {
        this.waterFogColor = waterFogColor;
        return this;
    }

    public int getSkyColor() {
        return skyColor;
    }

    public BiomeColors setSkyColor(int skyColor) {
        this.skyColor = skyColor;
        return this;
    }

    public int getFogColor() {
        return fogColor;
    }

    public BiomeColors setFogColor(int fogColor) {
        this.fogColor = fogColor;
        return this;
    }
}
