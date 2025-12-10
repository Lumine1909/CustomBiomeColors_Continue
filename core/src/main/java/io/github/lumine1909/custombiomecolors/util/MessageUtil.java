package io.github.lumine1909.custombiomecolors.util;

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

    public static Component getColorMessageGrass(Integer color, float temperature, float downfall) {
        if (color != null) {
            return getColorMessage(color, false);
        }
        int defaultColor = BiomeColorUtil.getGrassColor(temperature, downfall);
        return getColorMessage(defaultColor, true);
    }

    public static Component getColorMessageFoliage(Integer color, float temperature, float downfall) {
        if (color != null) {
            return getColorMessage(color, false);
        }
        int defaultColor = BiomeColorUtil.getFoliageColor(temperature, downfall);
        return getColorMessage(defaultColor, true);
    }

    public static Component getColorMessageDryFoliage(Integer color, float temperature, float downfall) {
        if (color != null) {
            return getColorMessage(color, false);
        }
        int defaultColor = BiomeColorUtil.getDryFoliageColor(temperature, downfall);
        return getColorMessage(defaultColor, true);
    }
}
