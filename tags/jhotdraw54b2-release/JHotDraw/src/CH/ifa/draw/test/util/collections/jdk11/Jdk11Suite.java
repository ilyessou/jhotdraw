/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.test.util.collections.jdk11;

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
* TestSuite Jdk11Suite
*/
// JUnitDoclet end javadoc_class
public class Jdk11Suite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// JUnitDoclet end class

	public static TestSuite suite() {

		TestSuite suite;

		suite = new TestSuite("CH.ifa.draw.test.util.collections.jdk11");

		suite.addTestSuite(CH.ifa.draw.test.util.collections.jdk11.SetWrapperTest.class);
		suite.addTestSuite(CH.ifa.draw.test.util.collections.jdk11.CollectionsFactoryJDK11Test.class);
		suite.addTestSuite(CH.ifa.draw.test.util.collections.jdk11.MapWrapperTest.class);
		suite.addTestSuite(CH.ifa.draw.test.util.collections.jdk11.ListWrapperTest.class);
		suite.addTestSuite(CH.ifa.draw.test.util.collections.jdk11.IteratorWrapperTest.class);

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
