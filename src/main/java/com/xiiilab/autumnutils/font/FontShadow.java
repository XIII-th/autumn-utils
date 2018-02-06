package com.xiiilab.autumnutils.font;

import com.annimon.stream.function.Consumer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Created by XIII-th on 06.01.2018
 */
public class FontShadow implements Consumer<FreeTypeFontParameter>{

    public Color color;
    public Integer xOffset;
    public Integer yOffset;

    @Override
    public void accept(FreeTypeFontParameter freeTypeFontParameter) {
        if (color != null)
            freeTypeFontParameter.shadowColor = color;
        if (xOffset != null)
            freeTypeFontParameter.shadowOffsetX = xOffset;
        if (yOffset != null)
            freeTypeFontParameter.shadowOffsetY = yOffset;
    }
}
