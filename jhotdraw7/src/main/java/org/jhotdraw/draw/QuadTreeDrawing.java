/*
 * @(#)QuadTreeDrawing.java  2.2  2007-04-09
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.QuadTree;
import org.jhotdraw.util.ReversedList;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.util.*;
import java.util.*;
/**
 * QuadTreeDrawing uses a QuadTree2DDouble to improve responsiveness of drawings
 * which contain many children.
 *
 * @author Werner Randelshofer
 * @version 2.2 2007-04-09 Added methods setCanvasSize, getCanvasSize.
 * <br>2.1 2007-02-09 Moved FigureListener and UndoableEditListener into
 * inner class.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class QuadTreeDrawing extends AbstractDrawing {
    private QuadTree<Figure> quadTree = new QuadTree<Figure>();
    private boolean needsSorting = false;
    private Dimension2DDouble canvasSize;
    
    public int indexOf(Figure figure) {
        return children.indexOf(figure);
    }
    
    public void basicAdd(int index, Figure figure) {
        super.basicAdd(index, figure);
        quadTree.add(figure, figure.getDrawingArea());
        needsSorting = true;
    }
    public int basicRemove(Figure figure) {
        int index = super.basicRemove(figure);
        quadTree.remove(figure);
        needsSorting = true;
        return index;
    }
    
    public void draw(Graphics2D g) {
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            Collection<Figure> c = quadTree.findIntersects(clipBounds);
            Collection<Figure> toDraw = sort(c);
            draw(g, toDraw);
        } else {
            draw(g, children);
        }
    }
    
    /**
     * Implementation note: Sorting can not be done for orphaned children.
     */
    public java.util.List<Figure> sort(Collection<Figure> c) {
        ensureSorted();
        ArrayList<Figure> sorted = new ArrayList<Figure>(c.size());
        for (Figure f : children) {
            if (c.contains(f)) {
                sorted.add(f);
            }
        }
        return sorted;
    }
    
    public void draw(Graphics2D g, Collection<Figure> c) {
        for (Figure f : c) {
            f.draw(g);
        }
    }
    
    
    
    public java.util.List<Figure> getChildren(Rectangle2D.Double bounds) {
        return new LinkedList(quadTree.findInside(bounds));
    }
    
    public java.util.List<Figure> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    public Figure findFigureInside(Point2D.Double p) {
        Collection<Figure> c = quadTree.findContains(p);
        for (Figure f : getFiguresFrontToBack()) {
            if (c.contains(f) && f.contains(p)){
                return f.findFigureInside(p);
            }
        }
        return null;
        
    }
    
    /**
     * Returns an iterator to iterate in
     * Z-order front to back over the children.
     */
    public java.util.List<Figure> getFiguresFrontToBack() {
        ensureSorted();
        return new ReversedList<Figure>(children);
    }
    
    public Figure findFigure(Point2D.Double p) {
        Collection<Figure> c = quadTree.findContains(p);
        switch (c.size()) {
            case 0 :
                return null;
            case 1: {
                Figure f = c.iterator().next();
                return (f.contains(p)) ? f : null;
            }
            default : {
                for (Figure f : getFiguresFrontToBack()) {
                    if (c.contains(f) && f.contains(p)) return f;
                }
                return null;
            }
        }
    }
    public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
        Collection<Figure> c = quadTree.findContains(p);
        switch (c.size()) {
            case 0 : {
                return null;
            }
            case 1: {
                Figure f = c.iterator().next();
                return (f == ignore || ! f.contains(p)) ? null : f;
            }
            default : {
                for (Figure f : getFiguresFrontToBack()) {
                    if (f != ignore && f.contains(p)) return f;
                }
                return null;
            }
        }
    }
    public Figure findFigureExcept(Point2D.Double p, Collection ignore) {
        Collection<Figure> c = quadTree.findContains(p);
        switch (c.size()) {
            case 0 : {
                return null;
            }
            case 1: {
                Figure f = c.iterator().next();
                return (! ignore.contains(f) || ! f.contains(p)) ? null : f;
            }
            default : {
                for (Figure f : getFiguresFrontToBack()) {
                    if (! ignore.contains(f) && f.contains(p)) return f;
                }
                return null;
            }
        }
    }
    public Figure findFigureBehind(Point2D.Double p, Figure figure) {
        boolean isBehind = false;
        for (Figure f : getFiguresFrontToBack()) {
            if (isBehind) {
                if (f.isVisible() && f.contains(p)) {
                    return f;
                }
            } else {
                isBehind = figure == f;
            }
        }
        return null;
    }
    public Figure findFigureBehind(Point2D.Double p, Collection<Figure> children) {
        int inFrontOf = children.size();
        for (Figure f : getFiguresFrontToBack()) {
            if (inFrontOf == 0) {
                if (f.isVisible() && f.contains(p)) {
                    return f;
                }
            } else {
                if (children.contains(f)) {
                    inFrontOf--;
                }
            }
        }
        return null;
    }
    
    public java.util.List<Figure> findFigures(Rectangle2D.Double r) {
        LinkedList<Figure> c = new LinkedList<Figure>(quadTree.findIntersects(r));
        switch (c.size()) {
            case 0 :
                // fall through
            case 1:
                return c;
            default :
                return sort(c);
        }
    }
    public java.util.List<Figure> findFiguresWithin(Rectangle2D.Double bounds) {
        LinkedList<Figure> contained = new LinkedList<Figure>();
        for (Figure f : children) {
            Rectangle2D r = f.getBounds();
            if (AttributeKeys.TRANSFORM.get(f) != null) {
                r = AttributeKeys.TRANSFORM.get(f).createTransformedShape(r).getBounds2D();
            }
            if (f.isVisible() && bounds.contains(r)) {
                contained.add(f);
            }
        }
        return contained;
    }
    
    public void bringToFront(Figure figure) {
        if (children.remove(figure)) {
            children.add(figure);
            needsSorting = true;
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    public void sendToBack(Figure figure) {
        if (children.remove(figure)) {
            children.add(0, figure);
            needsSorting = true;
            fireAreaInvalidated(figure.getDrawingArea());
        }
    }
    
    public boolean contains(Figure f) {
        return children.contains(f);
    }
    
    /**
     * Ensures that the children are sorted in z-order sequence.
     */
    private void ensureSorted() {
        if (needsSorting) {
            Collections.sort(children, FigureLayerComparator.INSTANCE);
            needsSorting = false;
        }
    }
    
    public void setCanvasSize(Dimension2DDouble newValue) {
        Dimension2DDouble oldValue = canvasSize;
        canvasSize = newValue;
        firePropertyChange("canvasSize", oldValue, newValue);
    }
    
    public Dimension2DDouble getCanvasSize() {
        return canvasSize;
    }

    public QuadTreeDrawing clone() {
        QuadTreeDrawing that = (QuadTreeDrawing) super.clone();
        return that;
    }
    protected EventHandler createEventHandler() {
        return new QuadTreeEventHandler();
    }
    

    
    /**
     * Handles all figure events fired by Figures contained in the Drawing.
     */
    protected class QuadTreeEventHandler extends AbstractCompositeFigure.EventHandler {
        @Override public void figureChanged(FigureEvent e) {
            quadTree.remove(e.getFigure());
            quadTree.add(e.getFigure(), e.getFigure().getDrawingArea());
            needsSorting = true;
            fireAreaInvalidated(e.getInvalidatedArea());
        }
    }
}