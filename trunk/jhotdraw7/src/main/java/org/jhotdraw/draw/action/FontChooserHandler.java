/**
 * @(#)FontChooserHandler.java  1.0  22.05.2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package org.jhotdraw.draw.action;

import java.awt.event.ComponentEvent;
import javax.swing.undo.*;
import org.jhotdraw.app.action.Actions;
import org.jhotdraw.undo.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.beans.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.gui.JFontChooser;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * FontChooserHandler.
 *
 * @author Werner Randelshofer
 * @version 1.0 22.05.2008 Created.
 */
public class FontChooserHandler extends AbstractSelectedAction
        implements PropertyChangeListener {

    protected AttributeKey key;
    protected JFontChooser fontChooser;
    protected JPopupMenu popupMenu;
    //protected Map<AttributeKey, Object> attributes;

    /** Creates a new instance. */
    public FontChooserHandler(DrawingEditor editor, AttributeKey key, JFontChooser fontChooser, JPopupMenu popupMenu) {
        super(editor);
        this.key = key;
        this.fontChooser = fontChooser;
        this.popupMenu = popupMenu;

        fontChooser.addActionListener(this);
        fontChooser.addPropertyChangeListener(this);
        updateEnabledState();
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getActionCommand() == JFontChooser.APPROVE_SELECTION) {
            applySelectedFontToFigures();
        } else if (evt.getActionCommand() == JFontChooser.CANCEL_SELECTION) {
        }
        popupMenu.setVisible(false);
    }

    protected void applySelectedFontToFigures() {
        /*for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
            getEditor().setDefaultAttribute(entry.getKey(), entry.getValue());
        }*/

        final ArrayList<Figure> selectedFigures = new ArrayList(getView().getSelectedFigures());
        final ArrayList<Object> restoreData = new ArrayList<Object>(selectedFigures.size());
        for (Figure figure : selectedFigures) {
            restoreData.add(figure.getAttributesRestoreData());
            figure.willChange();
            /*
            for (Map.Entry<AttributeKey, Object> entry : attributes.entrySet()) {
                entry.getKey().basicSet(figure, entry.getValue());
            }*/
            key.basicSet(figure, fontChooser.getSelectedFont());
            getEditor().setDefaultAttribute(key, fontChooser.getSelectedFont());
            figure.changed();
        }
        final Font undoValue = fontChooser.getSelectedFont();
        UndoableEdit edit = new AbstractUndoableEdit() {

            public String getPresentationName() {
                String name = (String) getValue(Actions.UNDO_PRESENTATION_NAME_KEY);
                if (name == null) {
                    name = (String) getValue(AbstractAction.NAME);
                }
                if (name == null) {
                    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
                    name = labels.getString("attribute");
                }
                return name;
            }

            public void undo() {
                super.undo();
                Iterator<Object> iRestore = restoreData.iterator();
                for (Figure figure : selectedFigures) {
                    figure.willChange();
                    figure.restoreAttributesTo(iRestore.next());
                    figure.changed();
                }
            }

            public void redo() {
                super.redo();
                for (Figure figure : selectedFigures) {
                    restoreData.add(figure.getAttributesRestoreData());
                    figure.willChange();
                    key.basicSet(figure, undoValue);
                    figure.changed();
                }
            }
        };
        fireUndoableEditHappened(edit);
    }

    @Override
    protected void updateEnabledState() {
        setEnabled(getEditor().isEnabled());
        if (getView() != null && fontChooser != null && popupMenu != null) {
        fontChooser.setEnabled(getView().getSelectionCount() > 0);
        popupMenu.setEnabled(getView().getSelectionCount() > 0);
        if (getView().getSelectionCount() > 0) {
            for (Figure f : getView().getSelectedFigures()) {
                if (f instanceof TextHolderFigure) {
                    TextHolderFigure thf = (TextHolderFigure) f;
                    fontChooser.setSelectedFont(thf.getFont());
                    break;
                }
            }
        }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == JFontChooser.SELECTED_FONT_PROPERTY) {
            applySelectedFontToFigures();
        }
    }
}
