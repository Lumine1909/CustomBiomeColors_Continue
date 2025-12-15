package io.github.lumine1909.custombiomecolors.object;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public record BiomeKey(String key, String value) {

    public static BiomeKey fromString(String biomeKeyString) {
        String[] split = biomeKeyString.split(":", 2);
        return new BiomeKey(split[0], split[1]);
    }

    public static BiomeKey fromKey(Key key) {
        return fromString(key.asString());
    }

    public Key toKey() {
        return Key.key(this.key, this.value);
    }

    public BiomeKey createSuffix(String valueSuffix) {
        return fromString(this.key + ":" + this.value + valueSuffix);
    }

    @Override
    public @NotNull String toString() {
        return key + ":" + value;
    }
}