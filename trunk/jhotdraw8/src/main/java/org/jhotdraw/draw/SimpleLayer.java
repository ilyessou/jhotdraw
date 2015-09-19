/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw.draw;

import static java.lang.Math.max;
import static java.lang.Math.min;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * SimpleLayer.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLayer extends AbstractCompositeFigure implements Layer {




    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public void updateNode(DrawingRenderer v, Node n) {
       
        applyFigureProperties(n);
        ObservableList<Node> group = ((Group) n).getChildren();
        group.clear();
        for (Figure child : childrenProperty()) {
            group.add(v.getNode(child));
        }
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Group();
    }
    /** This method throws an illegal argument exception if the new
     * parent is not an instance of Drawing.
     * @param newValue the desired parent */
    protected void checkNewParent(Figure newValue) {
        if(newValue != null && ! (newValue instanceof Drawing)) {
            throw new IllegalArgumentException("A Layer can only be added as a child to a Drawing. Illegal parent: "+newValue);
        }
    }
    /** Layer figures always return false for isSelectable.
     * @return false */
    @Override
     public boolean isSelectable() { return false; }
    
}


