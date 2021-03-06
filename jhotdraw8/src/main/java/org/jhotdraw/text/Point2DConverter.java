/*
 * @(#)Point2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.ParsePosition;
import javafx.geometry.Point2D;

/**
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DConverter implements Converter<Point2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} {1,number}", new XMLConverterFactory());

    @Override
    public void toString(Appendable out, Point2D value) throws IOException {
        formatter.toString(out, value.getX(), value.getY());
    }

    @Override
    public Point2D fromString(CharBuffer buf) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Point2D((double) v[0], (double) v[1]);
    }

}
