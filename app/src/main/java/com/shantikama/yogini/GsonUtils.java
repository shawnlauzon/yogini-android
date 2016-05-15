package com.shantikama.yogini;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Admin on 5/14/16.
 */
public class GsonUtils {
    public static Gson newGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
                .registerTypeAdapter(Optional.class, new OptionalDeserializer())
                .create();
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

    static class OptionalDeserializer<T>
            implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

        @Override
        public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            final JsonArray asJsonArray = json.getAsJsonArray();
            final JsonElement jsonElement = asJsonArray.get(0);
            final T value = context.deserialize(jsonElement, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
            return Optional.fromNullable(value);
        }

        @Override
        public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonElement element = context.serialize(src.orNull());
            final JsonArray result = new JsonArray();
            result.add(element);
            return result;
        }
    }
}
