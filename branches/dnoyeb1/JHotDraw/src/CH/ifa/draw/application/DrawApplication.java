/*
 * @(#)DrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.application;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.contrib.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * DrawApplication defines a standard presentation for
 * standalone drawing editors. The presentation is
 * customized in subclasses.
 * The application is started as follows:
 * <pre>
 * public static void main(String[] args) {
 *     MayDrawApp window = new MyDrawApp();
 *     window.open();
 * }
 * </pre>
 *
 * @version <$CURRENT_VERSION$>
 */
public	class DrawApplication
		extends JFrame
		implements DrawingEditor, PaletteListener, VersionRequester {

	private Tool					fTool;
	private Iconkit					fIconkit;

	private JTextField				fStatusLine;
	private ToolButton				fDefaultToolButton;
	private ToolButton				fSelectedToolButton;

	private static String			fApplicationName;
	private StorageFormatManager	fStorageFormatManager;
	private UndoManager				myUndoManager;
	private String                  fgUntitled = "untitled";
	private final EventListenerList listenerList = new EventListenerList();
	private DesktopListener     fDesktopListener;
	/**
	 * This component acts as a desktop for the content.
	 */
	private Desktop              fDesktop;

	// the image resource path
	private static final String		fgDrawPath = "/CH/ifa/draw/";
	public static final String		IMAGES = fgDrawPath + "images/";
	protected static int 			winCount = 0;

	/**
	 * The index of the file menu in the menu bar.
	 */
	public static final int			FILE_MENU = 0;
	/**
	 * The index of the edit menu in the menu bar.
	 */
	public static final int			EDIT_MENU = 1;
	/**
	 * The index of the alignment menu in the menu bar.
	 */
	public static final int			ALIGNMENT_MENU = 2;
	/**
	 * The index of the attributes menu in the menu bar.
	 */
	public static final int			ATTRIBUTES_MENU = 3;

	/**
	 * Constructs a drawing window with a default title.
	 */
	public DrawApplication() {
		this("JHotDraw");
	}

	/**
	 * Constructs a drawing window with the given title.
	 */
	public DrawApplication(String title) {
		super(title);
		setApplicationName(title);
		winCount++;
	}

	/**
	 * Factory method which can be overriden by subclasses to
	 * create an instance of their type.
	 *
	 * @return	newly created application
	 */
	protected DrawApplication createApplication() {
		return new DrawApplication();
	}

	/**
	 * Open a new view for this application containing a
	 * view of the drawing of the currently activated window.
	 */
	public void newView() {
        DrawingView dv = getDesktop().getActiveDrawingView();
		if (dv == null || !dv.isInteractive()) {//this should be ASSERT and otherwise handled by context sensitive menus.
			return;
		}
		DrawApplication window = createApplication();
		window.open();
        window.newWindow( dv.drawing() );

/*		if (dv.drawing().getTitle() != null ) {
			window.setDrawingTitle(dv.drawing().getTitle() + " (View)");
		}
		else {
			window.setDrawingTitle(getDefaultDrawingTitle() + " (View)");
		}*/
	}

	/**
	 * Open a new window for this application containing the passed in drawing,
	 * or a new drawing if the passed in drawing is null.
	 */
	public void newWindow(Drawing newDrawing) {
        getDesktop().removeAllFromDesktop(Desktop.PRIMARY);
		getDesktop().addToDesktop( createDrawingView( newDrawing ), Desktop.PRIMARY);
		toolDone();
	}
	public final void newWindow() {
        newWindow( createDrawing() );
	}
	/**
	 * Opens a new window with a drawing view.
	 */
	public void open() {
		getVersionControlStrategy().assertCompatibleVersion();
		setUndoManager(new UndoManager());
		setIconkit(createIconkit());
		getContentPane().setLayout(new BorderLayout());

		// status line must be created before a tool is set
		setStatusLine(createStatusLine());
		getContentPane().add(getStatusLine(), BorderLayout.SOUTH);

        //Initialize Desktop, must be done before tools
		setDesktopListener(createDesktopListener());
		setDesktop(createDesktop());

        //Initialize Tools
        // create dummy tool until the default tool is activated during toolDone()
        //why do we need a dummy tool?
		setTool(new NullTool(this), "");
		JToolBar tools = createToolPalette();
		createTools(tools);
        
        JPanel activePanel = new JPanel();
		activePanel.setAlignmentX(LEFT_ALIGNMENT);
		activePanel.setAlignmentY(TOP_ALIGNMENT);
		activePanel.setLayout(new BorderLayout());
		activePanel.add(tools, BorderLayout.NORTH);
		activePanel.add((Component)getDesktop(), BorderLayout.CENTER);
		getContentPane().add(activePanel, BorderLayout.CENTER);


 
        //Initialize Menus
        JMenuBar mb = new JMenuBar();
		createMenus(mb);
		setJMenuBar(mb);

		Dimension d = defaultSize();
		if (d.width > mb.getPreferredSize().width) {
			setSize(d.width, d.height);
		}
		else {
			setSize(mb.getPreferredSize().width, d.height);
		}
		addListeners();
		setStorageFormatManager(createStorageFormatManager());
		setVisible(true);
		
		Runnable r = new Runnable() {
			public void run() {
				toolDone();
				checkCommandMenu();
			}
		};

		if(java.awt.EventQueue.isDispatchThread() == false) {
			try {
				java.awt.EventQueue.invokeAndWait( r );
			}
			catch(java.lang.InterruptedException ie){
				System.err.println(ie.getMessage());
				exit();
			}
			catch(java.lang.reflect.InvocationTargetException ite){
				System.err.println(ite.getMessage());
				exit();
			}
		}
		else {
			r.run();
		}
	}

	/**
	 * Registers the listeners for this window
	 */
	protected void addListeners() {
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					exit();
				}
			}
		);
	}

	/**
	 * Creates the standard menus. Clients override this
	 * method to add additional menus.
	 */
	protected void createMenus(JMenuBar mb) {
		addMenuIfPossible(mb, createFileMenu());
		addMenuIfPossible(mb, createEditMenu());
		addMenuIfPossible(mb, createAlignmentMenu());
		addMenuIfPossible(mb, createAttributesMenu());
		addMenuIfPossible(mb, createDebugMenu());
	}

	protected void addMenuIfPossible(JMenuBar mb, JMenu newMenu) {
		if (newMenu != null) {
			mb.add(newMenu);
		}
	}

	/**
	 * Creates the file menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createFileMenu() {
		CommandMenu menu = new CommandMenu("File");
		Command cmd = new AbstractCommand("New", this, false) {
			public void execute() {
				promptNew();
			}
		};
		menu.add(cmd, new MenuShortcut('n'));

		cmd = new AbstractCommand("Open...", this, false) {
			public void execute() {
				promptOpen();
			}
		};
		menu.add(cmd, new MenuShortcut('o'));

		cmd = new AbstractCommand("Save As...", this, true) {
			public void execute() {
				promptSaveAs();
			}
		};
		menu.add(cmd, new MenuShortcut('s'));
		menu.addSeparator();

		cmd = new AbstractCommand("Print...", this, true) {
			public void execute() {
				print();
			}
		};
		menu.add(cmd, new MenuShortcut('p'));
		menu.addSeparator();

		cmd = new AbstractCommand("Exit", this, true) {
			public void execute() {
				exit();
			}
		};
		menu.add(cmd);
		return menu;
	}

	/**
	 * Creates the edit menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createEditMenu() {
		CommandMenu menu = new CommandMenu("Edit");
		menu.add(new UndoableCommand(
			new SelectAllCommand("Select All", this)), new MenuShortcut('a'));
		menu.addSeparator();
		menu.add(new UndoableCommand(
			new CutCommand("Cut", this)), new MenuShortcut('x'));
		menu.add(new CopyCommand("Copy", this), new MenuShortcut('c'));
		menu.add(new UndoableCommand(
			new PasteCommand("Paste", this)), new MenuShortcut('v'));
		menu.addSeparator();
		menu.add(new UndoableCommand(
			new DuplicateCommand("Duplicate", this)), new MenuShortcut('d'));
		menu.add(new UndoableCommand(new DeleteCommand("Delete", this)));
		menu.addSeparator();
		menu.add(new UndoableCommand(new GroupCommand("Group", this)));
		menu.add(new UndoableCommand(new UngroupCommand("Ungroup", this)));
		menu.addSeparator();
		menu.add(new UndoableCommand(new SendToBackCommand("Send to Back", this)));
		menu.add(new UndoableCommand(new BringToFrontCommand("Bring to Front", this)));
		menu.addSeparator();
		menu.add(new UndoCommand("Undo Command", this));
		menu.add(new RedoCommand("Redo Command", this));
		return menu;
	}

	/**
	 * Creates the alignment menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createAlignmentMenu() {
		CommandMenu menu = new CommandMenu("Align");
		menu.addCheckItem(new ToggleGridCommand("Toggle Snap to Grid", this, new Point(4,4)));
		menu.addSeparator();
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.LEFTS, this)));
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.CENTERS, this)));
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.RIGHTS, this)));
		menu.addSeparator();
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.TOPS, this)));
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.MIDDLES, this)));
		menu.add(new UndoableCommand(
			new AlignCommand(AlignCommand.Alignment.BOTTOMS, this)));
		return menu;
	}

	/**
	 * Creates the debug menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createDebugMenu() {
		CommandMenu menu = new CommandMenu("Debug");

		Command cmd = new AbstractCommand("Simple Update", this) {
			public void execute() {
				getDesktop().getActiveDrawingView().setDisplayUpdate(new SimpleUpdateStrategy());
			}
		};
		menu.add(cmd);

		cmd = new AbstractCommand("Buffered Update", this) {
			public void execute() {
				getDesktop().getActiveDrawingView().setDisplayUpdate(new BufferedUpdateStrategy());
			}
		};
		menu.add(cmd);
		return menu;
	}

	/**
	 * Creates the attributes menu and its submenus. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createAttributesMenu() {
		CommandMenu menu = new CommandMenu("Attributes");
		menu.add(createColorMenu("Fill Color", FigureAttributeConstant.FILL_COLOR));
		menu.add(createColorMenu("Pen Color", FigureAttributeConstant.FRAME_COLOR));
		menu.add(createArrowMenu());
		menu.addSeparator();
		menu.add(createFontMenu());
		menu.add(createFontSizeMenu());
		menu.add(createFontStyleMenu());
		menu.add(createColorMenu("Text Color", FigureAttributeConstant.TEXT_COLOR));
		return menu;
	}

	/**
	 * Creates the color menu.
	 */
	protected JMenu createColorMenu(String title, FigureAttributeConstant attribute) {
		CommandMenu menu = new CommandMenu(title);
		for (int i=0; i<ColorMap.size(); i++)
			menu.add(
				new UndoableCommand(
					new ChangeAttributeCommand(
						ColorMap.name(i),
						attribute,
						ColorMap.color(i),
						this
					)
				)
			);
		return menu;
	}

	/**
	 * Creates the arrows menu.
	 */
	protected JMenu createArrowMenu() {
		FigureAttributeConstant arrowMode = FigureAttributeConstant.ARROW_MODE;
		CommandMenu menu = new CommandMenu("Arrow");
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("none", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_NONE), this)));
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("at Start", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_START), this)));
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("at End", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_END), this)));
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("at Both", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_BOTH), this)));
		return menu;
	}

	/**
	 * Creates the fonts menus. It installs all available fonts
	 * supported by the toolkit implementation.
	 */
	protected JMenu createFontMenu() {
		CommandMenu menu = new CommandMenu("Font");
		String fonts[] = Toolkit.getDefaultToolkit().getFontList();
		for (int i = 0; i < fonts.length; i++) {
			menu.add(new UndoableCommand(
				new ChangeAttributeCommand(fonts[i], FigureAttributeConstant.FONT_NAME, fonts[i],  this)));
		}
		return menu;
	}

	/**
	 * Creates the font style menu with entries (Plain, Italic, Bold).
	 */
	protected JMenu createFontStyleMenu() {
		FigureAttributeConstant fontStyle = FigureAttributeConstant.FONT_STYLE;
		CommandMenu menu = new CommandMenu("Font Style");
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("Plain", fontStyle, new Integer(Font.PLAIN), this)));
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("Italic", fontStyle, new Integer(Font.ITALIC), this)));
		menu.add(new UndoableCommand(
			new ChangeAttributeCommand("Bold", fontStyle, new Integer(Font.BOLD), this)));
		return menu;
	}

	/**
	 * Creates the font size menu.
	 */
	protected JMenu createFontSizeMenu() {
		CommandMenu menu = new CommandMenu("Font Size");
		int sizes[] = { 9, 10, 12, 14, 18, 24, 36, 48, 72 };
		for (int i = 0; i < sizes.length; i++) {
		   menu.add(
				new UndoableCommand(
					new ChangeAttributeCommand(
						Integer.toString(sizes[i]),
						FigureAttributeConstant.FONT_SIZE,
						new Integer(sizes[i]),
						this
					)
				)
			);
		}
		return menu;
	}

	/**
	 * Create a menu which allows the user to select a different look and feel at runtime.
	 */
	public JMenu createLookAndFeelMenu() {
		CommandMenu menu = new CommandMenu("Look'n'Feel");

		UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();

		for (int i = 0; i < lafs.length; i++) {
			final String lnfClassName = lafs[i].getClassName();
			Command cmd = new AbstractCommand(lafs[i].getName(), this) {
				public void execute() {
					newLookAndFeel(lnfClassName);
				}
			};
			menu.add(cmd);
		}
		return menu;
	}

	/**
	 * Creates the tool palette.
	 */
	protected JToolBar createToolPalette() {
		JToolBar palette = new JToolBar();
		palette.setBackground(Color.lightGray);
		// use standard FlowLayout for JToolBar
		// palette.setLayout(new PaletteLayout(2,new Point(2,2)));
		return palette;
	}

	/**
	 * Creates the tools. By default only the selection tool is added.
	 * Override this method to add additional tools.
	 * Call the inherited method to include the selection tool.
	 * @param palette the palette where the tools are added.
	 */
	protected void createTools(JToolBar palette) {
		setDefaultTool(createDefaultTool());
		palette.add(fDefaultToolButton);
	}

	/**
	 * Creates the selection tool used in this editor. Override to use
	 * a custom selection tool.
	 */
	protected Tool createSelectionTool() {
		return new SelectionTool(this);
	}

	protected Tool createDefaultTool() {
		return createSelectionTool();
	}

	protected void setDefaultTool(Tool newDefaultTool) {
		if (newDefaultTool != null) {
			fDefaultToolButton = createToolButton(IMAGES+"SEL", "Selection Tool", newDefaultTool);
		}
		else {
			fDefaultToolButton = null;
		}
	}

	public Tool getDefaultTool() {
		if (fDefaultToolButton != null) {
			return fDefaultToolButton.tool();
		}
		else {
			return null;
		}
	}

	/**
	 * Creates a tool button with the given image, tool, and text
	 */
	protected ToolButton createToolButton(String iconName, String toolName, Tool tool) {
		return new ToolButton(this, iconName, toolName, tool);
	}

	/**
	 * Creates the drawing view used in this application.
	 * You need to override this method to use a DrawingView
	 * subclass in your application. By default a standard
	 * DrawingView is returned.
     * Made this final so no one indavertently overrides it.  If you need to
     * override, override createDrawingView(Drawing newDrawing) below.
     * dnoyeb@users.sourceforge.net
	 */
	protected final DrawingView createDrawingView() {
		return createDrawingView( createDrawing() );
	}

	protected DrawingView createDrawingView(Drawing newDrawing) {
		Dimension d = getDrawingViewSize();
		DrawingView newDrawingView = new StandardDrawingView(newDrawing, this, d.width, d.height);
		// notify listeners about created view when the view is added to the desktop
		//fireViewCreatedEvent(newDrawingView);
		return newDrawingView;
	}

	/**
	 * Create the DrawingView that is active when the application is started.
	 * This initial DrawingView might be different from DrawingView created
	 * by the application, so subclasses can override this method to provide
	 * a special drawing view for application startup time, e.g. a NullDrawingView
	 * which does not display an internal frame in a multiple document interface
	 * (MDI) application.
	 *
	 * @return drawing view that is active at application startup time
	 */
	protected DrawingView createInitialDrawingView() {
		return createDrawingView();
	}

	/**
	 * Override to define the dimensions of the drawing view.
	 */
	protected Dimension getDrawingViewSize() {
		return new Dimension(800, 800);
	}

	/**
	 * Creates the drawing used in this application.
	 * You need to override this method to use a Drawing
	 * subclass in your application. By default a standard
	 * Drawing is returned.
	 */
	protected Drawing createDrawing() {
        Drawing dwg = new StandardDrawing();
        dwg.setTitle( getDefaultDrawingTitle() );
		return dwg;
	}

	protected Desktop createDesktop() {
		return new JPanelDesktop();
//		return new JScrollPaneDesktop();
	}

	protected void setDesktop(Desktop newDesktop) {
		newDesktop.addDesktopListener(getDesktopListener());
		fDesktop = newDesktop;
	}

	/**
	* Get the component, in which the content is embedded. This component
	* acts as a desktop for the content.
	*/
	public Desktop getDesktop() {
		return fDesktop;
	}

	/**
	 * Factory method to create a StorageFormatManager for supported storage formats.
	 * Different applications might want to use different storage formats and can return
	 * their own format manager by overriding this method.
	 */
	public StorageFormatManager createStorageFormatManager() {
		StorageFormatManager storageFormatManager = new StorageFormatManager();
		storageFormatManager.setDefaultStorageFormat(new StandardStorageFormat());
		storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat());
		storageFormatManager.addStorageFormat(new SerializationStorageFormat());
//		storageFormatManager.addStorageFormat(new JDOStorageFormat());
		return storageFormatManager;
	}

	/**
	 * Set the StorageFormatManager. The StorageFormatManager is used when storing and
	 * restoring Drawing from the file system.
	 */
	protected final void setStorageFormatManager(StorageFormatManager newStorageFormatManager) {
		fStorageFormatManager = newStorageFormatManager;
	}

	/**
	 * Return the StorageFormatManager for this application.The StorageFormatManager is
	 * used when storing and restoring Drawing from the file system.
	 */
	public StorageFormatManager getStorageFormatManager() {
		return fStorageFormatManager;
	}

	/**
	 * Gets the default size of the window.
	 */
	protected Dimension defaultSize() {
		return new Dimension(600,450);
	}

	/**
	 * Creates the status line.
	 */
	protected JTextField createStatusLine() {
		JTextField field = new JTextField("No Tool", 40);
		field.setBackground(Color.white);
		field.setEditable(false);
		return field;
	}

	private void setStatusLine(JTextField newStatusLine) {
		fStatusLine = newStatusLine;
	}

	protected JTextField getStatusLine() {
		return fStatusLine;
	}

	/**
	 * Handles a user selection in the palette.
	 * @see PaletteListener
	 */
	public void paletteUserSelected(PaletteButton paletteButton) {
		ToolButton toolButton = (ToolButton)paletteButton;
		setTool(toolButton.tool(), toolButton.name());
		setSelected(toolButton);
	}

	/**
	 * Handles when the mouse enters or leaves a palette button.
	 * @see PaletteListener
	 */
	public void paletteUserOver(PaletteButton paletteButton, boolean inside) {
		ToolButton toolButton = (ToolButton)paletteButton;
		if (inside) {
			showStatus(toolButton.name());
		}
		else if (fSelectedToolButton != null) {
			showStatus(fSelectedToolButton.name());
		}
	}

	/**
	 * Gets the current tool.
	 * @see DrawingEditor
	 */
	public Tool tool() {
		return fTool;
	}

	/**
	 * Retrieve the active view from the window
	 * Gets the current drawing view.
	 * @see DrawingEditor
	 */
	public DrawingView view() {
		DrawingView dv = getDesktop().getActiveDrawingView();
		if(dv == null) {
			dv = NullDrawingView.getManagedDrawingView(this);
		}
		return dv;
	}

/*	protected void setView(DrawingView newView) {
		DrawingView oldView = fView;
		fView = newView;
		fireViewSelectionChangedEvent(oldView, view());
	}*/

	public DrawingView[] views() {
		return new DrawingView[] { view() };
	}

	/**
	 * Sets the default tool of the editor.
	 * @see DrawingEditor
	 */
	public void toolDone() {
		System.out.println("ToolDone");
		if (fDefaultToolButton != null) {
			setTool(fDefaultToolButton.tool(), fDefaultToolButton.name());
			setSelected(fDefaultToolButton);
		}
	}

	public void figureSelectionChanged(DrawingView view) {
		fireFigureSelectionChanged(view);
		checkCommandMenu();//commands already know about this event, why do we need this?
		//maybe the menu should listen to the Command it contains.
	}
	
	protected void checkCommandMenu(){
		JMenuBar mb = getJMenuBar();

		for (int x = 0; x < mb.getMenuCount(); x++) {
			JMenu jm = mb.getMenu(x);
			if (jm instanceof CommandMenu) {
				((CommandMenu)jm).checkEnabled();
			}
		}
	}

	/**
	 * Register to hear when the active view is changed.  For Single document
	 * interface, this will happen when a new drawing is created.
	 */
	public void addViewChangeListener(ViewChangeListener vsl) {
		listenerList.add(ViewChangeListener.class, vsl);
	}

	/**
	 * Remove listener
	 */
	public void removeViewChangeListener(ViewChangeListener vsl) {
		listenerList.remove(ViewChangeListener.class, vsl);
	}
	public void addFigureSelectionListener(FigureSelectionListener fsl){
		listenerList.add(FigureSelectionListener.class, fsl);
	}
	public void removeFigureSelectionListener(FigureSelectionListener fsl){
		listenerList.remove(FigureSelectionListener.class, fsl);
	}
	
	protected void fireFigureSelectionChanged(DrawingView view){
		final Object[] listeners = listenerList.getListenerList();
		FigureSelectionListener fsl = null;
		for (int i = listeners.length-2; i>=0 ; i-=2) {
			if (listeners[i] == FigureSelectionListener.class) {
				fsl = (FigureSelectionListener)listeners[i+1];
				fsl.figureSelectionChanged(view);
			}
		}		
	}
	
	/**
	 * An appropriate event is triggered and all registered observers
	 * are notified if the drawing view has been changed, e.g. by
	 * switching between several internal frames.  This method is
	 * usually invoked only when DrawingViews are created in SDI environments.
	 *
	 */
	protected void fireViewSelectionChangedEvent(DrawingView oldView, DrawingView newView) {
		final Object[] listeners = listenerList.getListenerList();
		ViewChangeListener vsl = null;
		for (int i = listeners.length-2; i>=0 ; i-=2) {
			if (listeners[i] == ViewChangeListener.class) {
				vsl = (ViewChangeListener)listeners[i+1];
				vsl.viewSelectionChanged(oldView, newView);
			}
		}
	}

	protected void fireViewCreatedEvent(DrawingView view) {
		final Object[] listeners = listenerList.getListenerList();
		ViewChangeListener vsl = null;
		for (int i = listeners.length-2; i>=0 ; i-=2) {
			if (listeners[i] == ViewChangeListener.class) {
				vsl = (ViewChangeListener)listeners[i+1];
				vsl.viewCreated(view);
			}
		}
	}

	protected void fireViewDestroyingEvent(DrawingView view) {
		final Object[] listeners = listenerList.getListenerList();
		ViewChangeListener vsl = null;
		for (int i = listeners.length-2; i>=0 ; i-=2) {
			if (listeners[i] == ViewChangeListener.class) {
				vsl = (ViewChangeListener)listeners[i+1];
				vsl.viewDestroying( view );
			}
		}
	}

	/**
	 * Shows a status message.
	 * @see DrawingEditor
	 */
	public void showStatus(String string) {
		getStatusLine().setText(string);
	}

	/**
	 * Note: it is inconsistent to directly assign a variable but when using it
	 * use it from a method.  (assignment:  fTool = t, usage: tool()) dnoyeB-4/8/02
	 * Note:  should we check that the tool is inactive before we activate it?
	 * this would be consistent with how we do deactivate.  I think we should do
	 * this now and not wait till a bug pops up. even if their is no bug, its
	 * consistent and adds understandability to the code.  dnoyeB-4/8/02
	 */
	public void setTool(Tool t, String name) {
		// SF bug-tracker id: #490665

		// deactivate only those tools that have been activated before
		if ((tool() != null) && (tool().isActive())) {
			tool().deactivate();
		}
		fTool = t;
		if (tool() != null) {
			showStatus(name);
			tool().activate();
		}
	}

	private void setSelected(ToolButton button) {
		if (fSelectedToolButton != null) {
			fSelectedToolButton.reset();
		}
		fSelectedToolButton = button;
		if (fSelectedToolButton != null) {
			fSelectedToolButton.select();
		}
	}

	/**
	 * Exits the application. You should never override this method
	 */
	public void exit() {
		destroy();
		setVisible(false);      // hide the JFrame
		dispose();   // tell windowing system to free resources
		winCount--;
		if (winCount == 0) {
			System.exit(0);
		}
	}

	/**
	 * Handles additional clean up operations. Override to destroy
	 * or release drawing editor resources.
	 */
	protected void destroy() {
	}

	/**
	 * Resets the drawing to a new empty drawing.
	 */
	public void promptNew() {
        newWindow( );
	}

	/**
	 * Shows a file dialog and opens a drawing.
	 */
	public void promptOpen() {
		toolDone();
		JFileChooser openDialog = createOpenFileChooser();
		getStorageFormatManager().registerFileFilters(openDialog);
		if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(openDialog.getFileFilter());
			if (foundFormat != null) {
				loadDrawing(foundFormat, openDialog.getSelectedFile().getAbsolutePath());
			}
			else {
				showStatus("Not a valid file format: " + openDialog.getFileFilter().getDescription());
			}
		}
	}

	/**
	 * Shows a file dialog and saves drawing.
	 */
	public void promptSaveAs() {
		if (getDesktop().getActiveDrawingView() != null) {
			toolDone();
			JFileChooser saveDialog = createSaveFileChooser();
			getStorageFormatManager().registerFileFilters(saveDialog);

			if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(saveDialog.getFileFilter());
				if (foundFormat != null) {
					saveDrawing(foundFormat, saveDialog.getSelectedFile().getAbsolutePath());
				}
				else {
					showStatus("Not a valid file format: " + saveDialog.getFileFilter().getDescription());
				}
			}
		}
	}

	/**
	 * Create a file chooser for the open file dialog. Subclasses may override this
	 * method in order to customize the open file dialog.
	 */
	protected JFileChooser createOpenFileChooser() {
		JFileChooser openDialog = new JFileChooser();
		openDialog.setDialogTitle("Open File...");
		return openDialog;
	}

	/**
	 * Create a file chooser for the save file dialog. Subclasses may override this
	 * method in order to customize the save file dialog.
	 */
	protected JFileChooser createSaveFileChooser() {
		JFileChooser saveDialog = new JFileChooser();
		saveDialog.setDialogTitle("Save File...");
		return saveDialog;
	}

	/**
	 * Prints the drawing.
	 */
	public void print() {
		tool().deactivate();
		PrintJob printJob = getToolkit().getPrintJob(this, "Print Drawing", null);

		if (printJob != null) {
			Graphics pg = printJob.getGraphics();

			if (pg != null) {
				((StandardDrawingView)getDesktop().getActiveDrawingView()).printAll(pg);
				pg.dispose(); // flush page
			}
			printJob.end();
		}
		tool().activate();
	}

	/**
	 * Save a Drawing in a file
	 */
	protected void saveDrawing(StorageFormat storeFormat, String file) {
		// Need a better alert than this.
        DrawingView dv = getDesktop().getActiveDrawingView();
		if (dv != null && dv.isInteractive() ) {
            try {
                String name = storeFormat.store(file, dv.drawing());
                dv.drawing().setTitle(name);
                updateApplicationTitle();
            }
            catch (IOException e) {
                showStatus(e.toString());
            }
        }
	}

	/**
	 * Load a Drawing from a file
	 */
	protected void loadDrawing(StorageFormat restoreFormat, String file) {
		try {
			Drawing restoredDrawing = restoreFormat.restore(file);
			if (restoredDrawing != null) {
				restoredDrawing.setTitle(file);
				newWindow(restoredDrawing);
			}
			else {
			   showStatus("Unknown file type: could not open file '" + file + "'");
			}
		}
		catch (IOException e) {
			showStatus("Error: " + e);
		}
	}

	/**
	 * Switch to a new Look&Feel
	 */
	private void newLookAndFeel(String landf) {
		try {
			UIManager.setLookAndFeel(landf);
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Set the title of the currently selected drawing
	 */
	protected void updateApplicationTitle() {
        DrawingView dv = getDesktop().getActiveDrawingView();
        if(dv != null && dv.isInteractive()){ //mrfloppy, we can do away with null check and ASSERT once their is always at least a NullDrawingView
            String drawingTitle = dv.drawing().getTitle();
            if (!getDefaultDrawingTitle().equals(drawingTitle)) {
                setTitle(getApplicationName() + " - " + drawingTitle);
            }
        }
        else {
            setTitle(getApplicationName());
        }
	}

	/**
	 * Set the name of the application build from this skeleton application
	 */
	public void setApplicationName(String applicationName) {
		fApplicationName = applicationName;
	}

	/**
	 * Return the name of the application build from this skeleton application
	 */
	public static String getApplicationName() {
		return fApplicationName;
	}

	protected void setUndoManager(UndoManager newUndoManager) {
		myUndoManager = newUndoManager;
	}

	public UndoManager getUndoManager() {
		return myUndoManager;
	}

	protected VersionControlStrategy getVersionControlStrategy() {
		return new StandardVersionControlStrategy(this);
	}

	/**
	 * Subclasses should override this method to specify to which versions of
	 * JHotDraw they are compatible. A string array is returned so it is possible
	 * to specify several version numbers of JHotDraw to which the application
	 * is compatible with.
	 *
	 * @return all versions number of JHotDraw the application is compatible with.
	 */
	public String[] getRequiredVersions() {
		String[] requiredVersions = new String[1];
		// return the version of the package we are in
		requiredVersions[0] = VersionManagement.getPackageVersion(DrawApplication.class.getPackage());
		return requiredVersions;
	}

    /**
     *  I made this protected because it should only be used by the DrawApplication.
     *  This is because all created drawings will be created through createDrawing()
     *  which is a method of DrawApplication.
     *  dnoyeb@users.sourceforge.net 12/31/02
     */
	protected String getDefaultDrawingTitle() {
		return fgUntitled;
	}

	protected DesktopListener getDesktopListener() {
		return fDesktopListener;
	}

	protected void setDesktopListener(DesktopListener desktopPaneListener) {
		fDesktopListener = desktopPaneListener;
	}
	/**
	 * @todo We need to use DrawingEditor events instead of or in addition to
	 *       these.
	 */
	protected DesktopListener createDesktopListener() {
	    return new DesktopListener() {
			public void drawingViewAdded(DesktopEvent dpe) {
				fireViewCreatedEvent( dpe.getDrawingView() );
			}
			public void drawingViewRemoved(DesktopEvent dpe) {
				fireViewDestroyingEvent( dpe.getDrawingView() );
			}
			public void drawingViewSelected(DrawingView oldView, DesktopEvent dpe) {
				DrawingView dv = dpe.getDrawingView();
				//get the current selection and unfreeze it.
				if (dv != null) {
					if (dv.drawing() != null)
						dv.unfreezeView();
				}
                fireViewSelectionChangedEvent(oldView, dv);
                updateApplicationTitle();
				if(oldView != null)
					oldView.removeFigureSelectionListener( DrawApplication.this );
				if(dv != null)
					dv.addFigureSelectionListener( DrawApplication.this );
				figureSelectionChanged(dv);//new view, new figure selection is active(this will cause extra events sometimes )
				//because when tools deactivate due to viewchange they will clear selection and fire event too
				//not hurting now, but need work.
			}
	    };
	}

	protected Iconkit createIconkit() {
		return new Iconkit(this);
	}

	protected void setIconkit(Iconkit newIconkit) {
		fIconkit = newIconkit;
	}

	protected Iconkit getIconkit() {
		return fIconkit;
	}
}