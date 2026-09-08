package io.github.lumine1909.custombiomecolors.object;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public record BiomeKey(String namespace, String value) {

    public static BiomeKey fromString(String biomeKeyString) {
        String[] split = biomeKeyString.split(":", 2);
        return new BiomeKey(split[0], split[1]);
    }

    public static BiomeKey fromKey(Key key) {
        return fromString(key.asString());
    }

    public Key toKey() {
        return Key.key(this.namespace, this.value);
    }

    @Override
    public @NotNull String toString() {
        return namespace + ":" + value;
    }
}