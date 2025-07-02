package me.arthed.custombiomecolors.data.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;

import java.io.IOException;

public class BiomeKeyAdapter extends TypeAdapter<BiomeKey> {

    @Override
    public void write(JsonWriter writer, BiomeKey biomeKey) throws IOException {
        writer.value(biomeKey.toString());
    }

    @Override
    public BiomeKey read(JsonReader reader) throws IOException {
        return BiomeKey.fromString(reader.nextString());
    }
}
