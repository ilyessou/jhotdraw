/*
 * @(#)JSVGDrawingAppletPanel.java  1.1  2008-03-27
 *
 * Copyright (c) 1996-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg;

import java.util.prefs.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.gui.ToolBarLayout;
import org.jhotdraw.draw.*;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * JSVGDrawingAppletPanel.
 * 
 * @author Werner Randelshofer
 * @version 1.1 2008-03-26 Tweaked toolbar area. 
 * <br>1.0 11. March 2004  Created.
 */
public class JSVGDrawingPanel extends JPanel {

    private UndoRedoManager undoManager;
    private DrawingEditor editor;
    private ResourceBundleUtil labels;
    private Preferences prefs;

    private class ItemChangeHandler implements ItemListener {

        private JToolBar toolbar;
        private String prefkey;

        public ItemChangeHandler(JToolBar toolbar, String prefkey) {
            this.toolbar = toolbar;
            this.prefkey = prefkey;
        }

        public void itemStateChanged(ItemEvent e) {
            boolean b = e.getStateChange() == ItemEvent.SELECTED;
            toolbar.setVisible(b);
            prefs.putBoolean(prefkey, b);
            validate();
        }
    }

    /** Creates new instance. */
    public JSVGDrawingPanel() {
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

        try {
            prefs = Preferences.userNodeForPackage(getClass());
        } catch (SecurityException e) {
            // prefs is null, because we are not permitted to read preferences
        }

        initComponents();
        toolsPane.setLayout(new ToolBarLayout());
        toolsPane.setBackground(new Color(0xf0f0f0));
        toolsPane.setOpaque(true);

        undoManager = new UndoRedoManager();
        editor = new DefaultDrawingEditor();
        editor.setHandleAttribute(HandleAttributeKeys.HANDLE_SIZE, new Integer(7));
        editor.add(view);

        DefaultDrawing drawing = new DefaultDrawing();
        view.setDrawing(drawing);
        drawing.addUndoableEditListener(undoManager);

        creationToolBar.setEditor(editor);
        fillToolBar.setEditor(editor);
        strokeToolBar.setEditor(editor);
        actionToolBar.setUndoManager(undoManager);
        actionToolBar.setEditor(editor);
        alignToolBar.setEditor(editor);
        arrangeToolBar.setEditor(editor);
        fontToolBar.setEditor(editor);
        linkToolBar.setEditor(editor);
        canvasToolBar.setEditor(editor);

        /* FIXME - Implement the code for handling constraints!
        toggleGridAction = actionToolBar.getToggleGridAction();
        if (prefs != null && prefs.getBoolean("gridVisible", false)) {
        view.setConstrainer(view.getOnConstrainer());
        }
        view.addPropertyChangeListener(new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("constrainer") && prefs) {
        prefs.putBoolean("gridVisible", evt.getNewValue() == toggleGridAction.getOnConstrainer());
        }
        }
        });
         */
        
        // Sort the toolbars according to the user preferences
        ArrayList<JToolBar> sortme = new ArrayList<JToolBar>();
        for (Component c : toolsPane.getComponents()) {
            if (c instanceof JToolBar) {
            sortme.add((JToolBar) c);
                    }
        }
        Collections.sort(sortme, new Comparator<JToolBar>() {
            public int compare(JToolBar tb1, JToolBar tb2) {
                int i1 = prefs.getInt("toolBarIndex." + tb1.getName(), 0);
                int i2 = prefs.getInt("toolBarIndex." + tb2.getName(), 0);
                return i1 - i2;
            }
        });
        toolsPane.removeAll();
        for (JToolBar tb : sortme) {
            toolsPane.add(tb);
        }

        toolsPane.addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                int i = 0;
                for (Component c : toolsPane.getComponents()) {
                    if (c instanceof JToolBar) {
                        JToolBar tb = (JToolBar) c;
                        prefs.putInt("toolBarIndex." + tb.getName(), i);
                        i++;
                    }
                }
            }

            public void componentRemoved(ContainerEvent e) {
            }
        });
    }

    public void setDrawing(Drawing d) {
        undoManager.discardAllEdits();
        view.getDrawing().removeUndoableEditListener(undoManager);
        view.setDrawing(d);
        d.addUndoableEditListener(undoManager);
    }

    public Drawing getDrawing() {
        return view.getDrawing();
    }

    public DrawingView getView() {
        return view;
    }

    public DrawingEditor getEditor() {
        return editor;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toolButtonGroup = new javax.swing.ButtonGroup();
        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();
        toolsPanel = new javax.swing.JPanel();
        toolsScrollPane = new javax.swing.JScrollPane();
        toolsPane = new javax.swing.JPanel();
        creationToolBar = new org.jhotdraw.samples.svg.gui.ToolsToolBar();
        actionToolBar = new org.jhotdraw.samples.svg.gui.ActionsToolBar();
        fillToolBar = new org.jhotdraw.samples.svg.gui.FillToolBar();
        strokeToolBar = new org.jhotdraw.samples.svg.gui.StrokeToolBar();
        fontToolBar = new org.jhotdraw.samples.svg.gui.FontToolBar();
        arrangeToolBar = new org.jhotdraw.samples.svg.gui.ArrangeToolBar();
        alignToolBar = new org.jhotdraw.samples.svg.gui.AlignToolBar();
        linkToolBar = new org.jhotdraw.samples.svg.gui.FigureToolBar();
        canvasToolBar = new org.jhotdraw.samples.svg.gui.CanvasToolBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        scrollPane.setViewportView(view);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        toolsPanel.setBackground(new java.awt.Color(255, 255, 255));
        toolsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        toolsPanel.setOpaque(true);
        toolsPanel.setLayout(new java.awt.GridBagLayout());

        toolsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        toolsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        toolsScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));

        toolsPane.add(creationToolBar);
        toolsPane.add(actionToolBar);
        toolsPane.add(fillToolBar);

        strokeToolBar.setMargin(new java.awt.Insets(0, 10, 0, 0));
        toolsPane.add(strokeToolBar);
        toolsPane.add(fontToolBar);
        toolsPane.add(arrangeToolBar);
        toolsPane.add(alignToolBar);
        toolsPane.add(linkToolBar);
        toolsPane.add(canvasToolBar);

        toolsScrollPane.setViewportView(toolsPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        toolsPanel.add(toolsScrollPane, gridBagConstraints);

        add(toolsPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    public JComponent getComponent() {
        return this;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jhotdraw.samples.svg.gui.ActionsToolBar actionToolBar;
    private org.jhotdraw.samples.svg.gui.AlignToolBar alignToolBar;
    private org.jhotdraw.samples.svg.gui.ArrangeToolBar arrangeToolBar;
    private org.jhotdraw.samples.svg.gui.CanvasToolBar canvasToolBar;
    private org.jhotdraw.samples.svg.gui.ToolsToolBar creationToolBar;
    private org.jhotdraw.samples.svg.gui.FillToolBar fillToolBar;
    private org.jhotdraw.samples.svg.gui.FontToolBar fontToolBar;
    private org.jhotdraw.samples.svg.gui.FigureToolBar linkToolBar;
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.samples.svg.gui.StrokeToolBar strokeToolBar;
    private javax.swing.ButtonGroup toolButtonGroup;
    private javax.swing.JPanel toolsPane;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JScrollPane toolsScrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
}