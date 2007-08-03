/*
 * @(#)ToggleGridAction.java  2.0 2007-07-31
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

package org.jhotdraw.draw.action;

import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.util.*;
import javax.swing.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
/**
 * ToggleGridAction.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2007-07-31 Rewritten to act on a GridProject instead 
 * of acting directly on DrawingView. 
 * <br>1.2 2007-04-16 Added getOffConstrainer, getOnConstrainer methods. 
 * <br>1.1 2006-04-21 Constructor with DrawingEditor paremeter added.
 * <br>1.0 January 16, 2006 Created.
 */
public class ToggleGridAction extends AbstractProjectAction {
    public final static String ID = "alignGrid";
    private String label;
    /**
     * Creates a new instance.
     */
    public ToggleGridAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
           putValue(Actions.SELECTED_KEY, false);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
       if (getCurrentProject() instanceof GridProject) {
           GridProject p = (GridProject) getCurrentProject();
           p.setGridVisible(! p.isGridVisible());
       }
    }

    protected void updateProperty() {
       if (getCurrentProject() instanceof GridProject) {
           GridProject p = (GridProject) getCurrentProject();
           putValue(Actions.SELECTED_KEY, p.isGridVisible());
       }
    }
}