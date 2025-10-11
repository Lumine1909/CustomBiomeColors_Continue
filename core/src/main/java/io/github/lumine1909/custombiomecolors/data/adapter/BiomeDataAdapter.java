package io.github.lumine1909.custombiomecolors.data.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.lumine1909.custombiomecolors.util.object.BiomeData;
import io.github.lumine1909.custombiomecolors.util.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.util.object.ColorData;

import java.io.IOException;
import java.util.Optional;

public class BiomeDataAdapter extends TypeAdapter<BiomeData> {

    @Override
    public void write(JsonWriter writer, BiomeData data) throws IOException {
        writer.beginObject();
        writer.name("biomeKey").value(data.biomeKey().toString());
        writer.name("baseBiomeKey").value(data.baseBiomeKey().toString());

        ColorData color = data.colorData();
        writer.name("colorData").beginObject();
        writer.name("fogColor").value(color.fogColor());
        writer.name("waterColor").value(color.waterColor());
        writer.name("waterFogColor").value(color.waterFogColor());
        writer.name("skyColor").value(color.skyColor());
        writer.name("foliageColor").value(color.foliageColor().orElse(-1));
        writer.name("dryFoliageColor").value(color.dryFoliageColor().orElse(-1));
        writer.name("grassColor").value(color.grassColor().orElse(-1));
        writer.endObject();

        writer.endObject();
    }

    @Override
    public BiomeData read(JsonReader reader) throws IOException {
        BiomeKey biomeKey = null;
        BiomeKey baseBiomeKey = null;
        ColorData colorData = null;

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "biomeKey" -> biomeKey = BiomeKey.fromString(reader.nextString());
                case "baseBiomeKey" -> baseBiomeKey = BiomeKey.fromString(reader.nextString());
                case "colorData" -> colorData = readColorData(reader);
            }
        }
        reader.endObject();

        if (biomeKey == null || baseBiomeKey == null || colorData == null) {
            throw new IOException("Malformed BiomeData JSON");
        }
        return new BiomeData(biomeKey, baseBiomeKey, colorData);
    }

    private ColorData readColorData(JsonReader reader) throws IOException {
        int fogColor = 0;
        int waterColor = 0;
        int waterFogColor = 0;
        int skyColor = 0;
        Optional<Integer> foliageColor = Optional.empty();
        Optional<Integer> dryFoliageColor = Optional.empty();
        Optional<Integer> grassColor = Optional.empty();

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "fogColor" -> fogColor = reader.nextInt();
                case "waterColor" -> waterColor = reader.nextInt();
                case "waterFogColor" -> waterFogColor = reader.nextInt();
                case "skyColor" -> skyColor = reader.nextInt();
                case "foliageColor" -> {
                    int i = reader.nextInt();
                    foliageColor = i == -1 ? Optional.empty() : Optional.of(i);
                }
                case "dryFoliageColor" -> {
                    int i = reader.nextInt();
                    dryFoliageColor = i == -1 ? Optional.empty() : Optional.of(i);
                }
                case "grassColor" -> {
                    int i = reader.nextInt();
                    grassColor = i == -1 ? Optional.empty() : Optional.of(i);
                }
            }
        }
        reader.endObject();

        return new ColorData(
            fogColor, waterColor, waterFogColor, skyColor,
            foliageColor, dryFoliageColor, grassColor
        );
    }
}

