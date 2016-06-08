package com.shantikama.yogini.utils;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Admin on 5/14/16.
 */
public class GsonUtils {
    public static final Gson GSON = newGson();

    public static Gson newGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
                .create();
    }

    public static <T> T deserialize(Context context, int resId, Class<T> clazz) {
        final Reader stream = new InputStreamReader(context.getResources().openRawResource(resId));
        return GSON.fromJson(stream, clazz);
    }

    static class ImmutableListDeserializer implements JsonDeserializer<ImmutableList<?>> {
        @Override
        public ImmutableList<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            @SuppressWarnings("unchecked")
            final TypeToken<ImmutableList<?>> immutableListToken = (TypeToken<ImmutableList<?>>) TypeToken.of(type);
            final TypeToken<? super ImmutableList<?>> listToken = immutableListToken.getSupertype(List.class);
            final List<?> list = context.deserialize(json, listToken.getType());
            return ImmutableList.copyOf(list);
        }
    }
}
