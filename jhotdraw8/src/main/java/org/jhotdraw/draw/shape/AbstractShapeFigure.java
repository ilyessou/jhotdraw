/* @(#)AbstractShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.util.Collections;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * Base class for all figures which render a {@code javafx.scene.shape.Shape}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractShapeFigure extends AbstractLeafFigure {

    /**
     * Defines the paint used for filling the interior of the figure. Default
     * value: {@code Color.WHITE}.
     */
    public static PaintStyleableFigureKey FILL = new PaintStyleableFigureKey("fill", Color.WHITE);
    /**
     * Defines whether anti aliasing hints are used. Default value:
     * {@code true}.
     */
    public static BooleanStyleableFigureKey SMOOTH = new BooleanStyleableFigureKey("smooth",  DirtyMask.of(DirtyBits.NODE), true);
    /**
     * Defines the distance in user coordinates for the dashing pattern. Default
     * value: {@code 0}.
     */
    public static DoubleStyleableFigureKey STROKE_DASH_OFFSET = new DoubleStyleableFigureKey("strokeDashOffset", DirtyMask.of(DirtyBits.NODE), 0.0);
    /**
     * Defines the end cap style. Default value: {@code SQUARE}.
     */
    public static EnumStyleableFigureKey<StrokeLineCap> STROKE_LINE_CAP = new EnumStyleableFigureKey<>("strokeLineCap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.BUTT);
    /**
     * Defines the style applied where path segments meet. Default value:
     * {@code MITER}.
     */
    public static EnumStyleableFigureKey<StrokeLineJoin> STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("strokeLineJoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    /**
     * Defines the limit for the {@code StrokeLineJoin.MITER} style. Default
     * value: {@code 10.0}.
     */
    public static DoubleStyleableFigureKey STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("strokeMiterLimit", DirtyMask.of(DirtyBits.NODE), 10.0);
    /**
     * Defines the paint used for filling the outline of the figure. Default
     * value: {@code Color.BLACK}.
     */
    public static PaintStyleableFigureKey STROKE = new PaintStyleableFigureKey("stroke", Color.BLACK);
    /**
     * Defines the stroke type used for drawing outline of the figure. Default
     * value: {@code StrokeType.CENTERED}.
     */
    public static EnumStyleableFigureKey<StrokeType> STROKE_TYPE = new EnumStyleableFigureKey<>("strokeType", StrokeType.class, DirtyMask.of(DirtyBits.NODE), StrokeType.CENTERED);
    /**
     * Defines the width of the outline of the figure. Default value:
     * {@code 1.0}.
     */
    public static DoubleStyleableFigureKey STROKE_WIDTH = new DoubleStyleableFigureKey("strokeWidth", DirtyMask.of(DirtyBits.NODE), 1.0);
    /**
     * Defines the dash array used. Default value: {@code empty array}.
     */
    public static DoubleListStyleableFigureKey STROKE_DASH_ARRAY = new DoubleListStyleableFigureKey("strokeDashArray", DirtyMask.of(DirtyBits.NODE), Collections.emptyList());

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    protected void applyShapeProperties(Shape shape) {
        shape.setFill(getStyled(FILL));
        shape.setSmooth(getStyled(SMOOTH));
        shape.setStrokeDashOffset(getStyled(STROKE_DASH_OFFSET));
        shape.setStrokeLineCap(getStyled(STROKE_LINE_CAP));
        shape.setStrokeLineJoin(getStyled(STROKE_LINE_JOIN));
        shape.setStrokeMiterLimit(getStyled(STROKE_MITER_LIMIT));
        shape.setStroke(getStyled(STROKE));
        shape.setStrokeType(getStyled(STROKE_TYPE));
        shape.setStrokeWidth(getStyled(STROKE_WIDTH));
        shape.getStrokeDashArray().setAll(getStyled(STROKE_DASH_ARRAY));
    }

    @Override
    public void layout() {//empty

    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

}
