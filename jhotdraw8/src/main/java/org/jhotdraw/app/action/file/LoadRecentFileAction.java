/*
 * @(#)LoadRecentFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.app.action.Action;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Lets the user write unsaved changes of the active view, and then loads
 the specified URI into the active view.
 * <p>
 * If there is no active view, this action creates a new view and thus
 * acts the same like {@link OpenRecentFileAction}.
 * <p>
 * This action is called when the user selects an item in the Recent Files
 * submenu of the File menu. The action and the menu item is automatically
 * created by the application, when the {@code ApplicationModel} provides a
 * {@code LoadFileAction}.
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Open last URI on launch</em><br>
 * {@code LoadRecentFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully loaded a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * <p><em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code LoadRecentFileAction} prevents loading an URI which
 * is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer.
 * @version $Id: LoadRecentFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class LoadRecentFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.loadRecent";
    private URI uri;

    /** Creates a new instance. 
     * @param app the application
     * @param view the view 
     * @param uri the uri of the recent file
    */
    public LoadRecentFileAction(Application app, @Nullable View view, URI uri) {
        super(app, view);
        this.uri = uri;
        setMayCreateView(true);
        putValue(Action.NAME, URIUtil.getName(uri));
    }
/*
    @Override
    public void doIt(View v) {
        final Application app = getApplication();

        // Prevent same URI from being opened more than once
        if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
            for (View vw : getApplication().getViews()) {
                if (vw.getURI() != null && vw.getURI().equals(uri)) {
                    vw.getComponent().requestFocus();
                    return;
                }
            }
        }

        // Search for an empty view
        if (v == null) {
            View emptyView = app.getActiveView();
            if (emptyView == null
                    || emptyView.getURI() != null
                    || emptyView.hasUnsavedChanges()) {
                emptyView = null;
            }
            if (emptyView == null) {
                v = app.createView();
                app.add(v);
                app.show(v);
            } else {
                v = emptyView;
            }
        }
        final View view = v;
        app.setEnabled(true);
        view.setEnabled(false);

        // If there is another view with the same file we set the multiple open
        // id of our view to max(multiple open id) + 1.
        int multipleOpenId = 1;
        for (View aView : app.views()) {
            if (aView != view
                    && aView.getURI() != null
                    && aView.getURI().equals(uri)) {
                multipleOpenId = Math.max(multipleOpenId, aView.getMultipleOpenId() + 1);
            }
        }
        view.setMultipleOpenId(multipleOpenId);

        // Open the file
        view.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                boolean exists = true;
                try {
                    File f = new File(uri);
                    exists = f.exists();
                } catch (IllegalArgumentException e) {
                    // The URI does not denote a file, thus we can not check whether the file exists.
                }
                if (exists) {
                    view.read(uri, null);
                } else {
                    ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
                    throw new IOException(labels.getFormatted("file.read.fileDoesNotExist.message", URIUtil.getName(uri)));
                }
            }

            @Override
            protected void done() {
                final Application app = getApplication();
                view.setURI(uri);
                app.addRecentURI(uri);
                Frame w = (Frame) SwingUtilities.getWindowAncestor(view.getComponent());
                if (w != null) {
                    w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
                    w.toFront();
                }
                view.getComponent().requestFocus();
                app.setEnabled(true);
            }

            @Override
            protected void failed(Throwable error) {
                error.printStackTrace();
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");

                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.read.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                        + error,
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                    @Override
                    public void optionSelected(SheetEvent evt) {
                        // app.dispose(view);
                    }
                });
            }

            @Override
            protected void finished() {
                view.setEnabled(true);
            }
        });
    }*/

    @Override
    protected void doIt(View p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}