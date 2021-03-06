/* @(#)SimpleDrawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.css.StyleableStyleManager;
import org.jhotdraw.xml.css.CSSParser;

/**
 * SimpleDrawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawing extends AbstractCompositeFigure implements Drawing {

    /**
     * The style manager is created lazily. If the stylesheet property is
     * changed, the style manager is set to null again.
     */
    private StyleableStyleManager styleManager = null;

    public SimpleDrawing() {
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        Rectangle background = new Rectangle();
        background.setId("background");
        g.getProperties().put("background", background);
        return g;
    }

    @Override
    public void updateNode(RenderContext v, Node n) {
        Group g = (Group) n;
        ObservableList<Node> children = ((Group) n).getChildren();
        children.clear();
        Rectangle2D bounds = get(BOUNDS);
        Rectangle page = (Rectangle) g.getProperties().get("background");
        page.setX(bounds.getMinX());
        page.setY(bounds.getMinY());
        page.setWidth(bounds.getWidth());
        page.setHeight(bounds.getHeight());
        page.setFill(get(BACKGROUND));
        children.add(page);

        for (Figure child : childrenProperty()) {
            children.add(v.getNode(child));
        }
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D bounds = get(BOUNDS);
        return new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

    }

    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public StyleableStyleManager getStyleManager() {
        if (styleManager == null) {
            styleManager = new StyleableStyleManager();
            if (get(STYLESHEETS) != null) {
                URL documentHome = get(DOCUMENT_HOME);
                CSSParser parser = new CSSParser();
                try {
                    for (URL url : get(STYLESHEETS)) {
                        URL absoluteUrl = (documentHome == null) ? url : new URL(documentHome, url.toString());
                        parser.parse(absoluteUrl, styleManager);
                    }
                } catch (IOException ex) {
                    System.err.println("Warning could not load stylesheet " + get(STYLESHEETS));
                    ex.printStackTrace();
                }
            }
        }
        return styleManager;
    }
}
