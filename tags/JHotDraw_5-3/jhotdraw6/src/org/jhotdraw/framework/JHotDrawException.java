/*
 * @(#)JHotDrawException.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

/**
 * A JHotDRaw Exception.
 *
 * @version <$CURRENT_VERSION$>
 */
public class JHotDrawException extends Exception {

	public JHotDrawException(String msg) {
		super(msg);
	}
	
	public JHotDrawException(Exception nestedException) {
		this(nestedException.getLocalizedMessage());
		nestedException.fillInStackTrace();
	}
}