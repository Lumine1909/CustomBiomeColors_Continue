package io.github.lumine1909.custombiomecolors.util;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class BiomeColorUtil {

    private static BufferedImage grassColorMap;
    private static BufferedImage foliageColorMap;
    private static BufferedImage dryFoliageColorMap;

    public static void loadColorMaps() {
        try (
            InputStream grassStream = BiomeColorUtil.class.getResourceAsStream("/grass.png");
            InputStream foliageStream = BiomeColorUtil.class.getResourceAsStream("/foliage.png");
            InputStream dryFoliageStream = BiomeColorUtil.class.getResourceAsStream("/dry_foliage.png");
        ) {
            if (grassStream == null || foliageStream == null || dryFoliageStream == null) {
                throw new IOException("Missing colormap images in JAR root");
            }

            grassColorMap = ImageIO.read(grassStream);
            foliageColorMap = ImageIO.read(foliageStream);
            dryFoliageColorMap = ImageIO.read(dryFoliageStream);

            if (grassColorMap.getWidth() != 256 || grassColorMap.getHeight() != 256 ||
                foliageColorMap.getWidth() != 256 || foliageColorMap.getHeight() != 256 ||
                dryFoliageColorMap.getWidth() != 256 || dryFoliageColorMap.getHeight() != 256
            ) {
                throw new IOException("Colormap images must be 256x256 pixels");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getGrassColor(float temperature, float downfall) {
        return getColorFromMap(grassColorMap, temperature, downfall);
    }

    public static int getFoliageColor(float temperature, float downfall) {
        return getColorFromMap(foliageColorMap, temperature, downfall);
    }

    public static int getDryFoliageColor(float temperature, float downfall) {
        return getColorFromMap(dryFoliageColorMap, temperature, downfall);
    }

    private static int getColorFromMap(@NotNull BufferedImage map, float temperature, float downfall) {
        temperature = Math.clamp(temperature, 0.0f, 1.0f);
        downfall = Math.clamp(downfall, 0.0f, 1.0f) * temperature;

        int x = (int) ((1.0f - temperature) * 255);
        int y = (int) ((1.0f - downfall) * 255);

        int rgb = map.getRGB(x, y);

        return rgb & 0x00FFFFFF;
    }
}
