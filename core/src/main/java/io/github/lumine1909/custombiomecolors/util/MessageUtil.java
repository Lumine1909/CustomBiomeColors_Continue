package io.github.lumine1909.custombiomecolors.util;

import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Optional;

public class MessageUtil {

    public static Component getColorMessage(int color) {
        return getColorMessage(color, false);
    }

    private static Component getColorMessage(int color, boolean isDefault) {
        Component colorMessage = Component.text(String.format("#%08X", color), TextColor.color(color));
        return isDefault ? colorMessage.append(Component.text(" (Default)", NamedTextColor.GRAY)) : colorMessage;
    }

    public static Component getColorMessageSpecial(Integer color, ColorType colorType, float temperature, float downfall) {
        if (color != null) {
            return getColorMessage(color, false);
        }
        int defaultColor = switch (colorType) {
            case GRASS -> BiomeColorUtil.getGrassColor(temperature, downfall);
            case FOLIAGE -> BiomeColorUtil.getFoliageColor(temperature, downfall);
            case DRY_FOLIAGE -> BiomeColorUtil.getDryFoliageColor(temperature, downfall);
            default -> throw new IllegalArgumentException("Invalid color type");
        };
        return getColorMessage(defaultColor, true);
    }
}
