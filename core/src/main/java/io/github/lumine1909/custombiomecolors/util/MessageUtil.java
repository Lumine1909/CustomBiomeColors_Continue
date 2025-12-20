package io.github.lumine1909.custombiomecolors.util;

import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class MessageUtil {

    public static Component getColorMessage(ColorType colorType, int color, float temperature, float downfall) {
        return colorType.isSpecial() ? getColorMessageSpecial(colorType, color, temperature, downfall) : getColorMessage(colorType, color, false);
    }

    private static Component getColorMessage(ColorType colorType, int color, boolean isDefault) {
        color &= colorType.mask();
        Component colorMessage = Component.text(String.format("#%0" + colorType.maskSize() + "X", color), TextColor.color(color));
        return isDefault ? colorMessage.append(Component.text(" (Default)", NamedTextColor.GRAY)) : colorMessage;
    }

    public static Component getColorMessageSpecial(ColorType colorType, Integer color, float temperature, float downfall) {
        if (color != null) {
            return getColorMessage(colorType, color, false);
        }
        int defaultColor = switch (colorType) {
            case GRASS -> BiomeColorUtil.getGrassColor(temperature, downfall);
            case FOLIAGE -> BiomeColorUtil.getFoliageColor(temperature, downfall);
            case DRY_FOLIAGE -> BiomeColorUtil.getDryFoliageColor(temperature, downfall);
            default -> throw new IllegalArgumentException("Invalid color type");
        };
        return getColorMessage(colorType, defaultColor, true);
    }
}
