package io.github.lumine1909.custombiomecolors.data.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;

import java.io.IOException;

public class BiomeDataAdapter extends TypeAdapter<BiomeData> {

    @Override
    public void write(JsonWriter writer, BiomeData data) throws IOException {
        writer.beginObject();
        writer.name("biomeKey").value(data.biomeKey().toString());
        writer.name("baseBiomeKey").value(data.baseBiomeKey().toString());

        ColorData color = data.colorData();
        writer.name("colorData").beginObject();
        color.forEach((colorType, value) -> {
            try {
                writer.name(colorType.serializedName()).value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
        reader.beginObject();
        ColorData.Builder data = new ColorData.Builder();
        while (reader.hasNext()) {
            String name = reader.nextName();
            int value = reader.nextInt();
            data.set(ColorType.BY_SERIALIZED_NAME.get(name), value);
        }
        reader.endObject();
        return data.build();
    }
}

