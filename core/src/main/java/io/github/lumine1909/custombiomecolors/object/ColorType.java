package io.github.lumine1909.custombiomecolors.object;

import io.github.lumine1909.custombiomecolors.util.VersionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum ColorType {
    GRASS(6, true),
    FOLIAGE(6, true),
    DRY_FOLIAGE(6, "1.21.5", true),
    WATER(6),
    WATER_FOG(6),
    SKY(6),
    FOG(6),
    SUNRISE_SUNSET(8, "1.21.11"),
    CLOUD(8, "1.21.11"),
    SKY_LIGHT(6, "1.21.11");

    public static int CURRENT_VERSION = 0;
    public static Map<String, ColorType> BY_SERIALIZED_NAME = new HashMap<>();

    static {
        for (ColorType colorType : ColorType.values()) {
            BY_SERIALIZED_NAME.put(colorType.serializedName, colorType);
        }
    }

    private final String version;
    private final int versionInt;
    private final int maskSize;
    private final int mask;
    private final boolean isSpecial;
    private final String messageName;
    private final String serializedName;

    ColorType(int maskSize) {
        this(maskSize, "", 0, false);
    }

    ColorType(int maskSize, boolean isSpecial) {
        this(maskSize, "", 0, isSpecial);
    }

    ColorType(int maskSize, String version) {
        this(maskSize, version, VersionUtil.obtainVersion(version), false);
    }

    ColorType(int maskSize, String version, boolean isSpecial) {
        this(maskSize, version, VersionUtil.obtainVersion(version), isSpecial);
    }


    ColorType(int maskSize, String version, int versionInt, boolean isSpecial) {
        this.maskSize = maskSize;
        this.mask = maskSize == 8 ? 0xFFFF_FFFF : 0xFF_FFFF;
        this.version = version;
        this.versionInt = versionInt;
        this.isSpecial = isSpecial;
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

    public String messageName() {
        return messageName;
    }

    public String serializedName() {
        return serializedName;
    }

    public int mask() {
        return mask;
    }

    public int maskSize() {
        return maskSize;
    }

    public boolean isSupported() {
        return CURRENT_VERSION >= versionInt;
    }

    public boolean isSpecial() {
        return isSpecial;
    }
}