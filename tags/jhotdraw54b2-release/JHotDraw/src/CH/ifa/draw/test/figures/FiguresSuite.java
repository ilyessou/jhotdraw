/*
 * @(#)FiguresSuite.java
 *
 * Project:     JHotdraw - a GUI framework for technical drawings
 *              http://www.jhotdraw.org
 *              http://jhotdraw.sourceforge.net
 * Copyright:   � by the original author(s) and all contributors
 * License:     Lesser GNU Public License (LGPL)
 *              http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.test.figures;

import junit.framework.TestSuite;
// JUnitDoclet begin import
// JUnitDoclet end import

/*
* Generated by JUnitDoclet, a tool provided by
* ObjectFab GmbH under LGPL.
* Please see www.junitdoclet.org, www.gnu.org
* and www.objectfab.de for informations about
* the tool, the licence and the authors.
*/

// JUnitDoclet begin javadoc_class
/**
* TestSuite FiguresSuite
*/
// JUnitDoclet end javadoc_class
public class FiguresSuite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// JUnitDoclet end class

	public static TestSuite suite() {

		TestSuite suite;

		suite = new TestSuite("CH.ifa.draw.test.figures");

		suite.addTestSuite(CH.ifa.draw.test.figures.NullConnectorTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.NullFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.UngroupCommandTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ShortestDistanceConnectorTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ScribbleToolTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.RectangleFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.RoundRectangleFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.PolyLineHandleTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.PolyLineConnectorTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.NumberTextFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.LineFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.InsertImageCommandTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ImageFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.GroupFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.GroupCommandTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.FontSizeHandleTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.EllipseFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ElbowHandleTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ElbowConnectionTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.TextToolTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ConnectedTextToolTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ChopEllipseConnectorTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.BorderToolTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.BorderDecoratorTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.ArrowTipTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.FigureAttributesTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.TextFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.PolyLineFigureTest.class);
		suite.addTestSuite(CH.ifa.draw.test.figures.LineConnectionTest.class);

		// JUnitDoclet begin method suite()
		// JUnitDoclet end method suite()

		return suite;
	}

	/**
	* Method to execute the TestSuite from command line
	* using JUnit's textui.TestRunner .
	*/
	public static void main(String[] args) {
		// JUnitDoclet begin method testsuite.main
		junit.textui.TestRunner.run(suite());
		// JUnitDoclet end method testsuite.main
	}
}