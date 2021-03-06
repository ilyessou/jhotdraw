/* @(#)CSSPaintConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * CSSPaintConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paint := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * LinearGradient := [ ("from", Point, "to", Point) |  "to", SideOrCorner], "," ],
 *                   [ ( "repeat" | "reflect" ),"," ] ColorStop,{"," ColorStop})
 * SideOrCorner := ("left"|"right"),("top"|bottom");
 * Color := (NamedColor|LookedUpColor|RgbColor|HsbColor|ColorFunction)
 * NamedColor := Word
 * LookedUpColor := Word
 * RgbColor := ("#",Digit,Digit,Digit
 *             | "#",Digit,Digit,Digit,Digit,Digit,Digit
 *             | "rgb(", Integer, ",", Integer, ",", Integer, ")"
 *             | "rgb(" Integer, "%", ",", Integer,"%","," Integer,"%" ")"
 *             | "rgba(", Integer, ",", Integer, "," Integer, ",", Double )
 *             | "rgba(", Integer "%" "," Integer, "%" "," Integer "%" "," Double )
 *  ...TODO...
 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CSSPaintConverter implements Converter<Paint> {

    public void toString(Appendable out, Paint value) throws IOException {
        if (value instanceof Color) {
            Color c = (Color) value;
            if (c.getOpacity() == 1.0) {
                int rgb = ((((int) (c.getRed() * 255)) & 0xff) << 16)
                        | ((((int) (c.getGreen() * 255)) & 0xff) << 8)
                        | ((((int) (c.getBlue() * 255)) & 0xff) << 0);
                String hex = "000000" + Integer.toHexString(rgb);
                out.append("#");
                out.append(hex.substring(hex.length() - 6));
            } else {
                out.append("rgba(");
                out.append(Integer.toString((int) (c.getRed() * 255)));
                out.append(',');
                out.append(Integer.toString((int) (c.getGreen() * 255)));
                out.append(',');
                out.append(Integer.toString((int) (c.getBlue() * 255)));
                out.append(',');
                out.append(Double.toString(c.getOpacity()));
                out.append(')');
            }
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    public Paint fromString(CharBuffer buf) throws ParseException, IOException {
        try {
            Color c = Color.web(buf.toString());
            buf.position(buf.limit());
            return c;
        } catch (IllegalArgumentException e) {
            ParseException pe = new ParseException("not a color:" + buf, buf.position());
            pe.initCause(e);
            throw pe;
        }
    }
}
