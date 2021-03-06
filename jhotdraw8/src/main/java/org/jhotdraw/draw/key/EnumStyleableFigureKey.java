/* @(#)EnumStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;

/**
 * EnumStyleableFigureKey.
 *
 * @author werni
 */
public class EnumStyleableFigureKey<T extends Enum> extends SimpleFigureKey<T> implements StyleableKey<T> {

    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name, enum class, mask and with
     * null as the default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask) {
        this(name, clazz, mask, null);
    }

    /**
     * Creates a new instance with the specified name, enum class, mask and 
     * default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     * @param defaultValue The default value.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask, T defaultValue) {
        super(name, clazz, mask, defaultValue);

        StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
        cssMetaData = factory.createEnumCssMetaData(clazz,
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData getCssMetaData() {
        return cssMetaData;

    }

}
