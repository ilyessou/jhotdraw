/*
 * @(#)ODGFigure.java  1.0  April 12, 2007
 *
 * Copyright (c) 2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.draw.Figure;

/**
 * ODGFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 April 12, 2007 Created.
 */
public interface ODGFigure extends Figure {
    /**
     * Returns true, if this figure is empty for one of the following
     * reasons:
     * <ul>
     * <li>A group has no children</li>
     * <li>A path has less than two points</li>
     * <li>An ellipse or a rectangle has a width or a height of 0</li>
     * <li>A text has no characters</li>
     * </ul>
     */
    public boolean isEmpty();
}
