package io.github.lumine1909.custombiomecolors.object;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public record ColorData(Map<ColorType, Integer> data) {

    public ColorData() {
        this(new HashMap<>());
    }

    public ColorData clone() {
        return new ColorData(new HashMap<>(data));
    }

    public ColorData set(ColorType colorType, Integer color) {
        if (color != null) {
            data.put(colorType, color);
        }
        return this;
    }

    public boolean has(ColorType colorType) {
        return data.containsKey(colorType);
    }

    public Integer get(ColorType colorType) {
        return data.get(colorType);
    }

    public void forEach(BiConsumer<ColorType, Integer> consumer) {
        data.forEach(consumer);
    }

    public ColorData remove(ColorType colorType) {
        data.remove(colorType);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ColorData(Map<ColorType, Integer> data1))) return false;
        return data.equals(data1);
    }
}