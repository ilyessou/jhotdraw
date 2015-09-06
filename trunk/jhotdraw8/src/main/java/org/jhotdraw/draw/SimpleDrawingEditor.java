/* @(#)SimpleDrawingEditor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashSet;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.draw.tool.Tool;

/**
 * SimpleDrawingEditor.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingEditor implements DrawingEditor {

    private final ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            setActiveDrawingView((DrawingView) ((ReadOnlyProperty) observable).getBean());
        }
    };

    private final SetProperty<DrawingView> drawingViews = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));

    {
        drawingViews.addListener((SetChangeListener.Change<? extends DrawingView> change) -> {
            if (change.wasRemoved()) {
                change.getElementRemoved().focusedProperty().removeListener(focusListener);
                if (getActiveDrawingView().isPresent()) {
                    if (getActiveDrawingView().get() == change.getElementRemoved()) {
                        setActiveDrawingView(drawingViews.isEmpty() ? null : drawingViews.get().iterator().next());
                    }
                }
            } else if (change.wasAdded()) {
                change.getElementAdded().focusedProperty().addListener(focusListener);
                if (drawingViews.size() == 1) {
                    setActiveDrawingView(change.getElementAdded());
                }
            }

        });
    }

    private final OptionalProperty<DrawingView> activeDrawingView = new OptionalProperty<>(this,"activeDrawingView");
    private final OptionalProperty<Tool> activeTool = new OptionalProperty<Tool>(this,"activeTool") {

        @Override
        public void set(Optional<Tool> newValue) {
            super.set(newValue);
            if (getActiveDrawingView().isPresent()) {
                getActiveDrawingView().get().setTool(newValue.orElse(null));
            }
        }
    };

    @Override
    public SetProperty<DrawingView> drawingViewsProperty() {
        return drawingViews;
    }

    @Override
    public OptionalProperty<DrawingView> activeDrawingViewProperty() {
        return activeDrawingView;
    }

    @Override
    public OptionalProperty<Tool> activeToolProperty() {
        return activeTool;
    }
}