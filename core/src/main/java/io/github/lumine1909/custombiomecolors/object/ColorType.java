package io.github.lumine1909.custombiomecolors.object;

import io.github.lumine1909.custombiomecolors.util.VersionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum ColorType {
    GRASS(-1),
    FOLIAGE(-1),
    WATER(-1),
    WATER_FOG(0x050533),
    SKY(0),
    FOG(0),
    DRY_FOLIAGE(-1, "1.21.5"),
    SUNRISE_SUNSET(0, "1.21.11"),
    CLOUD(0, "1.21.11"),
    SKY_LIGHT(0, "1.21.11");

    public static int CURRENT_VERSION = 0;
    public static Map<String, ColorType> BY_SERIALIZED_NAME = new HashMap<>();

    static {
        for (ColorType colorType : ColorType.values()) {
            BY_SERIALIZED_NAME.put(colorType.serializedName, colorType);
        }
    }

    private final int defaultValue;
    private final String version;
    private final int versionInt;
    private final String messageName;
    private final String serializedName;

    ColorType(int defaultValue) {
        this(defaultValue, "", 0);
    }

    ColorType(int defaultValue, String version) {
        this(defaultValue, version, VersionUtil.obtainVersion(version));
    }

    ColorType(int defaultValue, String version, int versionInt) {
        this.defaultValue = defaultValue;
        this.version = version;
        this.versionInt = versionInt;
        this.messageName = messageName(this);
        this.serializedName = serializedName(this);
    }

    private static String messageName(ColorType colorType) {
        String s = colorType.name().replace('_', ' ').toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean cap = true;
        for (char c : s.toCharArray()) {
            if (cap && Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
                cap = false;
            } else {
                sb.append(c);
            }
            if (c == ' ') cap = true;
        }
        return sb.toString();
    }

    private static String serializedName(ColorType colorType) {
        StringBuilder sb = new StringBuilder();

        String[] parts = colorType.name().split("_");
        if (parts.length == 0) return "Color";

        sb.append(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            String p = parts[i].toLowerCase();
            sb.append(Character.toUpperCase(p.charAt(0)))
                .append(p.substring(1));
        }
        return sb.append("Color").toString();
    }

    public void apply(Consumer<ColorType> supportConsumer, Consumer<ColorType> unSupportConsumer) {
        if (isSupported()) {
            supportConsumer.accept(this);
        } else {
            unSupportConsumer.accept(this);
        }
    }

    public String getSupportSince() {
        return this.version;
    }

    public boolean isDefault(Integer value) {
        return value == null || value.equals(-1);
    }

    public String messageName() {
        return messageName;
    }

    public String serializedName() {
        return serializedName;
    }

    public int defaultValue() {
        return defaultValue;
    }

    public boolean isSupported() {
        return CURRENT_VERSION >= versionInt;
    }
}