package me.arthed.custombiomecolors.utils;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtils {

    @Nullable
    public static <T> T getPrivateObject(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getPrivateInteger(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPrivateOptionalInteger(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Optional<?> value = (Optional<?>) field.get(object);
            if (value.isPresent()) {
                return ((Optional<Integer>) field.get(object)).get().intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}