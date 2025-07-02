package me.arthed.custombiomecolors.utils;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class ReflectionUtil {

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
}