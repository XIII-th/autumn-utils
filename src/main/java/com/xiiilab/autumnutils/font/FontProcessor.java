package com.xiiilab.autumnutils.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

/**
 * Created by XIII-th on 05.01.2018
 */
@Processor
public class FontProcessor extends AbstractAnnotationProcessor<Font> {

    private final String UNKNOWN_SYMBOLS;
    private final String COMMON_SYMBOLS;
    private final FontStyleProcessor _fontStyleProcessor;
    private final LocaleService _localeService;
    private Map<Font, String> _fontFiles;
    private Map<String, Font> _fontLocales;
    private Map<FontStyle, BitmapFont> _fontCache;
    private String _systemFontFile;

    public FontProcessor(FontStyleProcessor fontStyleProcessor, LocaleService localeService) {
        _fontStyleProcessor = fontStyleProcessor;
        _localeService = localeService;
        String defaultAlphabet = "abcdefghijklmnopqrstuvwxyz";
        UNKNOWN_SYMBOLS = "\ufffd¤#";
        COMMON_SYMBOLS = UNKNOWN_SYMBOLS + defaultAlphabet + defaultAlphabet.toUpperCase() +
                "1234567890\"!`?'.,;:()[]{}<>|/@\\^$-%+=_&~*№";
    }

    @Override
    public Class<Font> getSupportedAnnotationType() {
        return Font.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void doBeforeScanning(ContextInitializer initializer) {
        initializer.scanFor(Font.class);
        _fontFiles = new HashMap<>();
        _fontLocales = new HashMap<>();
        _fontCache = new HashMap<>();
    }

    @Override
    public void doAfterScanning(ContextInitializer initializer, Context context, ContextDestroyer destroyer) {
        if (_systemFontFile == null)
            throw new IllegalArgumentException("System font is not defined");
    }

    @Override
    public void processField(Field field, Font annotation, Object component, Context context,
            ContextInitializer initializer, ContextDestroyer contextDestroyer) {
        if (field.getType() != String.class)
            throw new IllegalStateException("Only string fields can be annotated with " + Font.class);
        String fontFilePath;
        try {
            fontFilePath = Reflection.getFieldValue(field, component, String.class);
        } catch (ReflectionException e) {
            throw new IllegalStateException("Unable to obtain font file path", e);
        }

        if (annotation.system()) {
            if (_systemFontFile != null)
                throw new IllegalStateException("Only one system font available");
            _systemFontFile = fontFilePath;
        } else if (annotation.locales().length > 0 && !annotation.alphabet().isEmpty()) {
            _fontFiles.put(annotation, fontFilePath);
            for (String locale : annotation.locales())
                _fontLocales.put(locale, annotation);
        } else
            throw new IllegalStateException("Locales and alphabet required for non system font");
    }

    public BitmapFont getFont(FontStyle style) {
        BitmapFont font = _fontCache.get(style);
        if (font == null) {
            if (style.system)
                font = getFont(style, null, _systemFontFile);
            else {
                String locale = _localeService.getCurrentLocale().getLanguage();
                Font annotation = _fontLocales.get(locale);
                if (annotation == null)
                    throw new IllegalStateException("Font file undefined for locale " + locale);
                font = getFont(style, annotation, _fontFiles.get(annotation));
            }
        }
        return font;
    }

    public void setupFonts(Skin skin) {
        for (Entry<String, FontStyle> style : _fontStyleProcessor.getFontStyles().entrySet())
            skin.add(style.getKey(), getFont(style.getValue()));
    }

    public void clearCache() {
        Map<FontStyle, BitmapFont> disposable = _fontCache;
        _fontCache = new HashMap<>();
        // очистим кэш в фоне, чтобы не отображать артефакты при переключении языков
        new Thread(() -> {
            try {
                Thread.sleep((long) (2000F * InterfaceService.DEFAULT_FADING_TIME));
            } catch (InterruptedException ignored) {
            }
            Gdx.app.postRunnable(() -> {
                for (BitmapFont font : disposable.values())
                    font.dispose();
            });
        }, "FONT_CACHE_CLEAR_TASK").start();
    }

    private BitmapFont getFont(FontStyle style, Font annotation, String fontFileName) {
        FileHandle fontFile = Gdx.files.internal(fontFileName);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.minFilter = Linear;
        parameter.magFilter = Linear;
        style.accept(parameter);
        parameter.characters = style.system ? getSystemAlphabet() : getCharSet(annotation.alphabet());
        BitmapFont font = generator.generateFont(parameter);
        for (int i = 0; i < UNKNOWN_SYMBOLS.length(); i++) {
            Glyph glyph = font.getData().getGlyph(UNKNOWN_SYMBOLS.charAt(i));
            if (glyph != null) {
                font.getData().missingGlyph = glyph;
                break;
            }
        }

        generator.dispose();
        _fontCache.put(style, font);
        return font;
    }

    private String getCharSet(String alphabet) {
        return COMMON_SYMBOLS + alphabet + alphabet.toUpperCase();
    }

    private String getSystemAlphabet() {
        // STOPSHIP: 06.01.2018 не получится налету подгружать шривт с набором символов для конкретного лейбла. Стиль со шрифтом может применяться к нескольким лейблам
        StringBuilder builder = new StringBuilder(_fontLocales.size() + 1);
        builder.append(COMMON_SYMBOLS);
        for (Font font : _fontLocales.values())
            builder.append(font.alphabet()).append(font.alphabet().toUpperCase());
        return builder.toString();
    }
}
