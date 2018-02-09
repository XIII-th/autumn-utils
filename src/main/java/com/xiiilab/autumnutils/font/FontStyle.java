package com.xiiilab.autumnutils.font;

import com.annimon.stream.function.Consumer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Created by XIII-th on 05.01.2018
 */
public class FontStyle implements Consumer<FreeTypeFontParameter> {

    public boolean system;
    public Byte size;
    public Color color;
    public FontBorder border;
    public FontShadow shadow;

    @Override
    public void accept(FreeTypeFontParameter freeTypeFontParameter) {
        if (size != null)
            freeTypeFontParameter.size = size;
        if (color != null)
            freeTypeFontParameter.color = color;
        if (border != null)
            border.accept(freeTypeFontParameter);
        if (shadow != null)
            shadow.accept(freeTypeFontParameter);
    }
}
