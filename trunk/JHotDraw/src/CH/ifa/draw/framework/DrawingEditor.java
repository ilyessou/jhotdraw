/*
 * @(#)DrawingEditor.java 5.2
 *
 */

package CH.ifa.draw.framework;

import java.awt.*;

/**
 * DrawingEditor defines the interface for coordinating
 * the different objects that participate in a drawing editor.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld022.htm>Mediator</a></b><br>
 * DrawingEditor is the mediator. It decouples the participants
 * of a drawing editor.
 *
 * @see Tool
 * @see DrawingView
 * @see Drawing
 */
public interface DrawingEditor extends FigureSelectionListener {

    /**
     * Gets the editor's drawing view.
     */
    public DrawingView view();

    /**
     * Gets the editor's drawing.
     */
    public Drawing drawing();

    /**
     * Gets the editor's current tool.
     */
    public Tool tool();

    /**
     * Informs the editor that a tool has done its interaction.
     * This method can be used to switch back to the default tool.
     */
    public void toolDone();

    /**
     * Informs that the current selection has changed.
     * Override this method to handle selection changes.
     */
    public void figureSelectionChanged(DrawingView view);

    /**
     * Shows a status message in the editor's user interface
     */
    public void showStatus(String string);

}