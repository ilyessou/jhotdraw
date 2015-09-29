/* @(#)LocatorHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.geometry.Point2D;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.Locator;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to a
 * Locator object.
 *
 * @see Locator
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class LocatorHandle<F extends Figure> extends AbstractHandle<F> {

    private Locator locator;

    /**
     * Initializes the LocatorHandle with the given Locator.
     */
    public LocatorHandle(F owner, Locator l) {
        super(owner);
        locator = l;
    }

    /**
     * Returns the location in local figure coordinates.
     */
    protected Point2D getLocation() {
        return locator.locate(owner);
    }
}
