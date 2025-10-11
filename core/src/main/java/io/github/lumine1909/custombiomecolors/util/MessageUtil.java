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
        int realColor = color & 0xFFFFFF;
        Component colorMessage = Component.text(String.format("#%06X", realColor), TextColor.color(realColor));
        return isDefault ? colorMessage.append(Component.text(" (Default)", NamedTextColor.GRAY)) : colorMessage;
    }

    public static Component getColorMessageGrass(Optional<Integer> color, float temperature, float downfall) {
        if (color.isPresent()) {
            return getColorMessage(color.get(), false);
        }
        int realColor = BiomeColorUtil.getGrassColor(temperature, downfall);
        return getColorMessage(realColor, true);
    }

    public static Component getColorMessageFoliage(Optional<Integer> color, float temperature, float downfall) {
        if (color.isPresent()) {
            return getColorMessage(color.get(), false);
        }
        int realColor = BiomeColorUtil.getFoliageColor(temperature, downfall);
        return getColorMessage(realColor, true);
    }

    public static Component getColorMessageDryFoliage(Optional<Integer> color, float temperature, float downfall) {
        if (color.isPresent()) {
            return getColorMessage(color.get(), false);
        }
        int realColor = BiomeColorUtil.getDryFoliageColor(temperature, downfall);
        return getColorMessage(realColor, true);
    }
}
