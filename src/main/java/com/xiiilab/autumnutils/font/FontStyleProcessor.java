package com.xiiilab.autumnutils.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIII-th on 05.01.2018
 */
@Processor
public class FontStyleProcessor extends AbstractAnnotationProcessor<FontStylePath> {

    private Map<String, FontStyle> _styles;

    @Override
    public Class<FontStylePath> getSupportedAnnotationType() {
        return FontStylePath.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void doBeforeScanning(ContextInitializer initializer) {
        initializer.scanFor(FontStylePath.class);
        _styles = new HashMap<>();
    }

    /**
     * Механизм загрузки позаимствован из {@link com.badlogic.gdx.scenes.scene2d.ui.Skin#getJsonLoader(FileHandle)}
     */
    @Override
    public void processField(Field field, FontStylePath annotation, Object component, Context context,
            ContextInitializer initializer, ContextDestroyer contextDestroyer) {
        if (field.getType() != String.class)
            throw new IllegalStateException("Only String type supported for " + FontStylePath.class);
        String filePath;
        try {
            filePath = Reflection.getFieldValue(field, component, String.class);
        } catch (ReflectionException e) {
            throw new IllegalStateException("Unable to obtain font styles file path", e);
        }

        final Map<Class<?>, Map<String, Object>> cache = new HashMap<>();
        Json json = new Json() {
            public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
                // If the JSON is a string but the type is not, look up the actual value by name.
                if (jsonData.isString() && !ClassReflection.isAssignableFrom(CharSequence.class, type))
                    return (T) cache.get(type).get(jsonData.asString());
                return super.readValue(type, elementType, jsonData);
            }
        };
        json.setSerializer(FontStyleProcessor.class, new ReadOnlySerializer<FontStyleProcessor>() {
            @Override
            public FontStyleProcessor read(Json json, JsonValue jsonData, Class type) {
                for (JsonValue valueMap = jsonData.child; valueMap != null; valueMap = valueMap.next) {
                    try {
                        for (JsonValue entry = valueMap.child; entry != null; entry = entry.next) {
                            Class<?> cls = ClassReflection.forName(valueMap.name());
                            Object object = json.readValue(cls, entry);
                            Map<String, Object> typeCache = cache.get(cls);
                            if (typeCache == null)
                                cache.put(cls, typeCache = new HashMap<>());
                            typeCache.put(entry.name(), object);
                        }
                    } catch (ReflectionException ex) {
                        throw new SerializationException(ex);
                    }
                }
                return null;
            }
        });
        json.setSerializer(Byte.class, new ReadOnlySerializer<Byte>() {
            @Override
            public Byte read(Json json, JsonValue jsonData, Class type) {
                return jsonData.get("value").asByte();
            }
        });
        json.setSerializer(Color.class, new ReadOnlySerializer<Color>() {
            public Color read(Json json, JsonValue jsonData, Class type) {
                if (jsonData.isString())
                    return (Color) cache.get(jsonData.asString());

                String hex = json.readValue("hex", String.class, (String) null, jsonData);
                if (hex != null)
                    return Color.valueOf(hex);

                float r = json.readValue("r", float.class, 0f, jsonData);
                float g = json.readValue("g", float.class, 0f, jsonData);
                float b = json.readValue("b", float.class, 0f, jsonData);
                float a = json.readValue("a", float.class, 1f, jsonData);
                return new Color(r, g, b, a);
            }
        });
        json.fromJson(FontStyleProcessor.class, Gdx.files.internal(filePath));
        _styles = (Map) cache.get(FontStyle.class);
    }

    public Map<String, FontStyle> getFontStyles() {
        return _styles;
    }
}
