/* @(#)NoLayoutNoConnectionsDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import org.jhotdraw.draw.model.DrawingModelEvent;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.draw.key.SimpleFigureKey;

/**
 * This drawing model assumes that the drawing contains no figures which perform
 * layouts and no connections between figures.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectionsNoLayoutDrawingModel extends AbstractDrawingModel {

    @Override
    public void setRoot(Drawing root) {
        this.root = root;
        fire(DrawingModelEvent.rootChanged(this, root));
    }

    @Override
    public void removeFromParent(Figure child) {
        Figure parent = child.getParent();
        if (parent != null) {
            int index = parent.children().indexOf(child);
            if (index != -1) {
                parent.children().remove(index);
                fire(DrawingModelEvent.figureRemoved(this, parent, child, index));
                fire(DrawingModelEvent.nodeInvalidated(this, parent));
            }
        }
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        parent.children().add(index, child);
        fire(DrawingModelEvent.figureAdded(this, parent, child, index));
        fire(DrawingModelEvent.nodeInvalidated(this, parent));
    }

    @Override
    public <T> void set(Figure figure, Key<T> key, T newValue) {
        T oldValue = figure.set(key, newValue);
        if (oldValue != newValue) {
            if (key instanceof FigureKey) {
                FigureKey<T> fk = (FigureKey<T>) key;
                DirtyMask dm = fk.getDirtyMask();
                if (dm.containsOneOf(DirtyBits.NODE)) {
                    fire(DrawingModelEvent.nodeInvalidated(this, figure));
                }
                if (dm.containsOneOf(DirtyBits.CONNECTION_LAYOUT)) {
                    for (Figure c : figure.connections()) {
                        fire(DrawingModelEvent.layoutInvalidated(this, figure));
                    }
                }
            }
        }
    }

    @Override
    public void reshape(Figure figure, Transform transform) {
        figure.reshape(transform);
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        for (Figure f : figure.preorderIterable()) {
            for (Figure c : f.connections()) {
                fire(DrawingModelEvent.layoutInvalidated(this, c));
            }
        }
    }

    @Override
    public void reshape(Figure figure, double x, double y, double width, double height) {
        figure.reshape(x, y, width, height);
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        for (Figure f : figure.preorderIterable()) {
            for (Figure c : f.connections()) {
                fire(DrawingModelEvent.layoutInvalidated(this, c));
            }
        }
    }

    @Override
    public void layout(Figure figure) {
        figure.layout();
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        for (Figure f : figure.preorderIterable()) {
            for (Figure c : f.connections()) {
                fire(DrawingModelEvent.layoutInvalidated(this, c));
            }
        }
    }

    @Override
    public void applyCss(Figure figure) {
        figure.applyCss();
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        for (Figure f : figure.preorderIterable()) {
            for (Figure c : f.connections()) {
                fire(DrawingModelEvent.layoutInvalidated(this, c));
            }
        }
    }
}