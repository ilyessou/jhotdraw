/* @(#)ChopEllipseConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.draw.shape.AbstractShapeFigure.STROKE;
import static org.jhotdraw.draw.shape.AbstractShapeFigure.STROKE_TYPE;
import static org.jhotdraw.draw.shape.AbstractShapeFigure.STROKE_WIDTH;
import org.jhotdraw.geom.Geom;

/**
 * ChopEllipseConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopEllipseConnector extends CenterConnector {

    @Override
    public Point2D chopStart(Figure target, Figure connection, double startX, double startY, double endX, double endY) {
        // FIXME implement me properly
        Bounds bounds = target.getBoundsInLocal();
        Rectangle2D r = new Rectangle2D(bounds.getMinX(), bounds.getMinY(),
                bounds.getWidth(), bounds.getHeight());
        if (target.getStyled(STROKE) != null) {
            double grow;
            switch (target.getStyled(STROKE_TYPE)) {
                case CENTERED:
                default:
                    grow = target.getStyled(STROKE_WIDTH) / 2d;
                    break;
                case OUTSIDE:
                    grow = target.getStyled(STROKE_WIDTH);
                    break;
                case INSIDE:
                    grow = 0d;
                    break;
            }
            r = Geom.grow(r, grow, grow);
        }
        double angle = Geom.pointToAngle(r, target.drawingToLocal(new Point2D(endX, endY)));
        return target.localToDrawing(Geom.ovalAngleToPoint(r, angle));
    }
}
