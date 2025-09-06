package io.github.lumine1909.custombiomecolors.utils.objects;

import java.util.Optional;

public record ColorData(
    int fogColor, int waterColor, int waterFogColor, int skyColor,
    Optional<Integer> foliageColor, Optional<Integer> dryFoliageColor, Optional<Integer> grassColor
) {

    public ColorData setColor(BiomeColorType colorType, int color) {
        return switch (colorType) {
            case GRASS -> this.setGrassColor(color);
            case FOLIAGE -> this.setFoliageColor(color);
            case WATER -> this.setWaterColor(color);
            case WATER_FOG -> this.setWaterFogColor(color);
            case SKY -> this.setSkyColor(color);
            case FOG -> this.setFogColor(color);
            case DRY_FOLIAGE -> this.setDryFoliageColor(color);
        };
    }

    private Mutable mutable() {
        return new Mutable(
            fogColor, waterColor, waterFogColor, skyColor,
            foliageColor, dryFoliageColor, grassColor
        );
    }

    public ColorData setGrassColor(int grassColor) {
        return mutable().grass(Optional.of(grassColor)).build();
    }

    public ColorData setFoliageColor(int foliageColor) {
        return mutable().foliage(Optional.of(foliageColor)).build();
    }

    public ColorData setDryFoliageColor(int dryFoliageColor) {
        return mutable().foliage(Optional.of(dryFoliageColor)).build();
    }

    public ColorData setWaterColor(int waterColor) {
        return mutable().water(waterColor).build();
    }

    public ColorData setWaterFogColor(int waterFogColor) {
        return mutable().waterFog(waterFogColor).build();
    }

    public ColorData setSkyColor(int skyColor) {
        return mutable().sky(skyColor).build();
    }

    public ColorData setFogColor(int fogColor) {
        return mutable().fog(fogColor).build();
    }


    public static class Mutable {

        private int fogColor;
        private int waterColor;
        private int waterFogColor;
        private int skyColor;
        private Optional<Integer> foliageColor = Optional.empty();
        private Optional<Integer> dryFoliageColor = Optional.empty();
        private Optional<Integer> grassColor = Optional.empty();

        public Mutable() {

        }

        public Mutable(
            int fogColor, int waterColor, int waterFogColor, int skyColor,
            Optional<Integer> foliageColor, Optional<Integer> dryFoliageColor, Optional<Integer> grassColor
        ) {
            this.fogColor = fogColor;
            this.waterColor = waterColor;
            this.waterFogColor = waterFogColor;
            this.skyColor = skyColor;
            this.foliageColor = foliageColor;
            this.dryFoliageColor = dryFoliageColor;
            this.grassColor = grassColor;
        }

        public Mutable fog(int fogColor) {
            this.fogColor = fogColor;
            return this;
        }

        public Mutable water(int waterColor) {
            this.waterColor = waterColor;
            return this;
        }

        public Mutable waterFog(int waterFogColor) {
            this.waterFogColor = waterFogColor;
            return this;
        }

        public Mutable sky(int skyColor) {
            this.skyColor = skyColor;
            return this;
        }

        public Mutable foliage(Optional<Integer> foliageColor) {
            this.foliageColor = foliageColor;
            return this;
        }

        public Mutable dryFoliage(Optional<Integer> dryFoliageColor) {
            this.dryFoliageColor = dryFoliageColor;
            return this;
        }

        public Mutable grass(Optional<Integer> grassColor) {
            this.grassColor = grassColor;
            return this;
        }

        public ColorData build() {
            return new ColorData(
                fogColor, waterColor, waterFogColor, skyColor,
                foliageColor, dryFoliageColor, grassColor
            );
        }
    }
}
