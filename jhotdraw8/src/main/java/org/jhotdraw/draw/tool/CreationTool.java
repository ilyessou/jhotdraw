/* @(#)CreationTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static java.lang.Math.*;
import javafx.application.Platform;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.util.Resources;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.util.ReversedList;

/**
 * CreationTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CreationTool extends AbstractTool {

    private Supplier<Figure> figureFactory;
    private Supplier<Layer> layerFactory;

    /**
     * The created figure.
     */
    private Figure figure;

    /**
     * The rubber band.
     */
    private double x1, y1, x2, y2;

    /**
     * The minimum size of a created figure (in view coordinates.
     */
    private double minSize = 2;

    public CreationTool(String name, Resources rsrc, Supplier<Figure> factory) {
        super(name, rsrc);
        this.figureFactory = factory;
        this.layerFactory = SimpleLayer::new;
    }

    public void setFactory(Supplier<Figure> factory) {
        this.figureFactory = factory;
    }

    @Override
    protected void stopEditing() {
        figure = null;
    }

    @Override
    protected void onMousePressed(MouseEvent event, DrawingView view) {
        Platform.runLater(()->view.getNode().requestFocus());
        x1 = event.getX();
        y1 = event.getY();
        x2 = x1;
        y2 = y1;
        figure = figureFactory.get();
        Point2D c = view.getConstrainer().constrainPoint(figure, view.viewToDrawing(new Point2D(x1, y1)));
        figure.reshape(c.getX(), c.getY(), 1, 1);
        DrawingModel dm = view.getModel();
        Drawing drawing = dm.getRoot();

        Layer layer = getOrCreateLayer(view, figure);
        view.setActiveLayer(layer);

        dm.addChildTo(figure, layer);
        event.consume();
    }

    @Override
    protected void onMouseReleased(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            if (abs(x2 - x1) < minSize && abs(y2 - y1) < minSize) {
                Point2D c1 = dv.getConstrainer().constrainPoint(figure, dv.viewToDrawing(x1, y1));
                Point2D c2 = dv.getConstrainer().translatePoint(figure, dv.viewToDrawing(x1
                        + minSize, y1 + minSize), Constrainer.DIRECTION_NEAREST);
                if (c2.equals(c1)) {
                    c2 = new Point2D(c1.getX() + 10, c1.getY() + 10);
                }
                DrawingModel dm = dv.getModel();
                dm.reshape(figure, c1.getX(), c1.getY(), c2.getX() - c1.getX(), c2.getY()
                        - c1.getY());
            }
            dv.selectionProperty().clear();
            dv.selectionProperty().add(figure);
            figure = null;
        }
        event.consume();
        fireToolDone();
    }

    @Override
    protected void onMouseDragged(MouseEvent event, DrawingView dv) {
        if (figure != null) {
            x2 = event.getX();
            y2 = event.getY();
            Point2D c1 = dv.getConstrainer().constrainPoint(figure, dv.viewToDrawing(x1, y1));
            Point2D c2 = dv.getConstrainer().constrainPoint(figure, dv.viewToDrawing(x2, y2));
            DrawingModel dm = dv.getModel();
            dm.reshape(figure, c1.getX(), c1.getY(), c2.getX() - c1.getX(), c2.getY()
                    - c1.getY());
        }
        event.consume();
    }

    @Override
    protected void onMouseClicked(MouseEvent event, DrawingView dv) {
    }

    /**
     * Finds a layer for the specified figure. Creates a new layer if no
     * suitable layer can be found.
     *
     * @param dv the drawing view
     * @param newFigure the figure
     * @return a suitable layer for the figure
     */
    protected Layer getOrCreateLayer(DrawingView dv, Figure newFigure) {
        // try to use the active layer
        Layer activeLayer = dv.getActiveLayer();
        if (activeLayer != null && !activeLayer.get(Figure.DISABLED) && activeLayer.isAllowsChildren()) {
            return activeLayer;
        }
        // search for a suitable layer front to back
        Layer layer = null;
        for (Figure candidate : new ReversedList<>(dv.getDrawing().getChildren())) {
            if (!candidate.get(Figure.DISABLED) && candidate.isAllowsChildren()) {
                layer = (Layer) candidate;
                break;
            }
        }
        // create a new layer if necessary
        if (layer == null) {
            layer = layerFactory.get();
            dv.getModel().addChildTo(layer, dv.getDrawing());
        }
        return layer;
    }

}
