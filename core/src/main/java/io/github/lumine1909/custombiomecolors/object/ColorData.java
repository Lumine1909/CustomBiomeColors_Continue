package io.github.lumine1909.custombiomecolors.object;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public record ColorData(Map<ColorType, Integer> data) {

    public Builder mutable() {
        return new Builder(new HashMap<>(data));
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

    public record Builder(Map<ColorType, Integer> data) {

        public Builder() {
            this(new HashMap<>());
        }

        public Builder set(ColorType colorType, Integer color) {
            if (color != null) {
                data.put(colorType, color);
            }
            return this;
        }

        public Builder remove(ColorType colorType) {
            data.remove(colorType);
            return this;
        }

        public boolean has(ColorType colorType) {
            return data.containsKey(colorType);
        }

        public Integer get(ColorType colorType) {
            return data.get(colorType);
        }

        public ColorData build() {
            return new ColorData(new HashMap<>(data));
        }
    }
}