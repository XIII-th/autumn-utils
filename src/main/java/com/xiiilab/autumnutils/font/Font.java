package com.xiiilab.autumnutils.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for string path to .ttf file of font
 * <br>
 * Created by XIII-th on 05.01.2018
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Font {

    /**
     * List of locales use annotated .ttf file
     */
    String[] locales() default {};

    /**
     * Alphabet used for generation of {@link BitmapFont}. Absent symbols will not be shown
     */
    String alphabet() default "";

    /**
     * Now used to define path of system font {@link FontStyle#system}
     * @deprecated will be replaced with auto generation of {@link BitmapFont}
     */
    boolean system() default false;
}
