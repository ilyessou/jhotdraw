/**
 * @(#)EditDrawingPanel.java  1.0  2007-12-18
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.draw.action;

import ch.randelshofer.quaqua.QuaquaLookAndFeel;
import java.awt.Color;
import java.beans.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * The EditDrawingPanel can be used to edit the attributes of a Drawing.
 *
 * @see org.jhotdraw.draw.Drawing
 * 
 * @author Werner Randelshofer
 * @version 1.0 2007-12-18 Created.
 */
public class EditDrawingPanel extends javax.swing.JPanel {

    private ResourceBundleUtil labels;
    private Drawing drawing;
    private JSlider backgroundOpacitySlider;
    private JColorChooser colorChooser;

    /** Creates new form. */
    public EditDrawingPanel() {
        labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        initComponents();
        colorButton.putClientProperty("Quaqua.Button.style", "colorWell");
        backgroundOpacitySlider = new JSlider(JSlider.VERTICAL, 0, 100, 100);
        opacityPopupButton.add(backgroundOpacitySlider);
        opacityPopupButton.putClientProperty("JButton.buttonType", "toolbar");
        add(opacityPopupButton);

        backgroundOpacitySlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                opacityField.setValue(backgroundOpacitySlider.getValue());
                if (!backgroundOpacitySlider.getModel().getValueIsAdjusting()) {
            drawing.fireUndoableEditHappened(
                    CANVAS_FILL_OPACITY.setUndoable(drawing, opacityField.getValue() / 100d, labels)
                    );
                }
            }
        });

        opacityField.setMaximum(100);
        opacityField.setMinimum(0);
        opacityField.setValue(100);
        opacityField.addPropertyChangeListener("value", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (!backgroundOpacitySlider.getModel().getValueIsAdjusting()) {
                    backgroundOpacitySlider.setValue((int) opacityField.getValue());
            drawing.fireUndoableEditHappened(
                    CANVAS_FILL_OPACITY.setUndoable(drawing, opacityField.getValue() / 100d, labels)
                    );
                }
            }
        });
    }

    private JColorChooser getColorChooser() {
            if (colorChooser == null) {
                colorChooser = new JColorChooser();
            }
            return colorChooser;
    }
    /**
     * Sets the GridConstrainer to be edited by this panel.
     * 
     * @param newValue The GridConstrainer.
     */
    public void setDrawing(Drawing newValue) {
        drawing = newValue;
        updatePanel();
    }

    /**
     * Updates the drawing due to changes made on this panel.
     */
    private void updateDrawing() {
        if (drawing != null) {
            drawing.fireUndoableEditHappened(
                    CANVAS_FILL_OPACITY.setUndoable(drawing, opacityField.getValue() / 100d, labels)
                    );
            drawing.fireUndoableEditHappened(
                    CANVAS_FILL_COLOR.setUndoable(drawing, colorButton.getBackground(), labels)
                    );
        }
    }

    /**
     * Updates the panel due to changes made on the drawing.
     */
    private void updatePanel() {
        if (drawing != null) {
            opacityField.setValue(CANVAS_FILL_OPACITY.get(drawing) * 100d);
            colorButton.setBackground(CANVAS_FILL_COLOR.get(drawing));
        }
    }

    /**
     * Returns the GridConstrainer currently being edited by this panel.
     * 
     * @return The GridConstrainer.
     */
    public Drawing getDrawing() {
        return drawing;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new QuaquaLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame f = new JFrame("Drawing Settings2");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new EditDrawingPanel());
        f.pack();
        f.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bgColorButtonGroup = new javax.swing.ButtonGroup();
        colorLabel = new javax.swing.JLabel();
        colorButton = new javax.swing.JButton();
        opacityLabel = new javax.swing.JLabel();
        opacityField = new org.jhotdraw.gui.JDoubleTextField();
        opacityPopupButton = new org.jhotdraw.gui.JPopupButton();

        setLayout(new java.awt.GridBagLayout());

        colorLabel.setText(labels.getString("attribute.backgroundColor")); // NOI18N
        add(colorLabel, new java.awt.GridBagConstraints());

        colorButton.setText(" ");
        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorButtonPerformed(evt);
            }
        });
        add(colorButton, new java.awt.GridBagConstraints());

        opacityLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jhotdraw/draw/action/images/attributeOpacity.png"))); // NOI18N
        opacityLabel.setToolTipText(labels.getString("opacity")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(opacityLabel, gridBagConstraints);

        opacityField.setColumns(4);
        add(opacityField, new java.awt.GridBagConstraints());

        opacityPopupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jhotdraw/draw/action/images/popupIcon.png"))); // NOI18N
        add(opacityPopupButton, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents
    private void colorButtonPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorButtonPerformed
     if (drawing != null) {
        Color color = getColorChooser().showDialog(this, labels.getString("attribute.backgroundColor"), 
               CANVAS_FILL_COLOR.get(drawing)
               );
        colorButton.setBackground(color);
        updateDrawing();
        }
}//GEN-LAST:event_colorButtonPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgColorButtonGroup;
    private javax.swing.JButton colorButton;
    private javax.swing.JLabel colorLabel;
    private org.jhotdraw.gui.JDoubleTextField opacityField;
    private javax.swing.JLabel opacityLabel;
    private org.jhotdraw.gui.JPopupButton opacityPopupButton;
    // End of variables declaration//GEN-END:variables
}
