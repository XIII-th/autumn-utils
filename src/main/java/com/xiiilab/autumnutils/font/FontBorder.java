package com.xiiilab.autumnutils.font;

import com.annimon.stream.function.Consumer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Created by XIII-th on 06.01.2018
 */
public class FontBorder implements Consumer<FreeTypeFontParameter> {

    public Float width;
    public Color color;
    public Float gamma;
    public Boolean straight;

    @Override
    public void accept(FreeTypeFontParameter freeTypeFontParameter) {
        if (width != null)
            freeTypeFontParameter.borderWidth = width;
        if (color != null)
            freeTypeFontParameter.borderColor = color;
        if (gamma != null)
            freeTypeFontParameter.borderGamma = gamma;
        if (straight != null)
            freeTypeFontParameter.borderStraight = straight;
    }
}
