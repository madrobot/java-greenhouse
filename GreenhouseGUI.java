/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: GreenhouseGUI.java
 * description: GreenhouseGUI class for TME4 Part 2
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * <code>GreenhouseGUI</code> class for TME4 Part 2
 */
public class GreenhouseGUI extends JFrame {
    // Internal variables
    private static int lastId = 0;
    public final UUID id = UUID.randomUUID();
    private final Writer logWriter = new LogWriter();;
    private String programFile;
    private String dumpFile;

    // Greenhouse controller
    private static HashMap<String, GreenhouseControls> gc = new HashMap<>();

    // UI Components
    private final JLabel filenameLabel;
    private final JTextArea messagesArea;
    private JButton openFileButton;
    private JButton startButton;
    private JButton restartButton;
    private JButton terminateButton;
    private JButton suspendButton;
    private JButton resumeButton;

    private JPopupMenu popupMenu;
    private JMenuItem startMenuItem;
    private JMenuItem restartMenuItem;
    private JMenuItem terminateMenuItem;
    private JMenuItem suspendMenuItem;
    private JMenuItem resumeMenuItem;

    private JMenuItem openEventsFileMenuItem;
    private JMenuItem loadDumpFileMenuItem;

    // Callables
    Callable<Void> initialButtonState = () -> {
        startButton.setEnabled(true);
        startMenuItem.setEnabled(true);
        restartButton.setEnabled(false);
        restartMenuItem.setEnabled(false);
        terminateButton.setEnabled(false);
        terminateMenuItem.setEnabled(false);
        suspendButton.setEnabled(false);
        suspendMenuItem.setEnabled(false);
        resumeButton.setEnabled(false);
        resumeMenuItem.setEnabled(false);

        openFileButton.setEnabled(true);
        openEventsFileMenuItem.setEnabled(true);
        loadDumpFileMenuItem.setEnabled(true);
        return null;
    };

    Callable<Void> runningButtonState = () -> {
        startButton.setEnabled(false);
        startMenuItem.setEnabled(false);
        restartButton.setEnabled(true);
        restartMenuItem.setEnabled(true);
        terminateButton.setEnabled(true);
        terminateMenuItem.setEnabled(true);
        suspendButton.setEnabled(true);
        suspendMenuItem.setEnabled(true);
        resumeButton.setEnabled(false);
        resumeMenuItem.setEnabled(false);

        openFileButton.setEnabled(false);
        openEventsFileMenuItem.setEnabled(false);
        loadDumpFileMenuItem.setEnabled(false);
        return null;
    };

    Callable<Void> stoppedButtonState = () -> {
        startButton.setEnabled(true);
        startMenuItem.setEnabled(true);
        restartButton.setEnabled(true);
        restartMenuItem.setEnabled(true);
        terminateButton.setEnabled(false);
        terminateMenuItem.setEnabled(false);
        suspendButton.setEnabled(false);
        suspendMenuItem.setEnabled(false);
        resumeButton.setEnabled(false);
        resumeMenuItem.setEnabled(false);

        openFileButton.setEnabled(true);
        openEventsFileMenuItem.setEnabled(true);
        loadDumpFileMenuItem.setEnabled(true);
        return null;
    };

    Callable<Void> suspendedButtonState = () -> {
        startButton.setEnabled(false);
        startMenuItem.setEnabled(false);
        restartButton.setEnabled(true);
        restartMenuItem.setEnabled(true);
        terminateButton.setEnabled(false);
        terminateMenuItem.setEnabled(false);
        suspendButton.setEnabled(false);
        suspendMenuItem.setEnabled(false);
        resumeButton.setEnabled(true);
        resumeMenuItem.setEnabled(true);

        openFileButton.setEnabled(false);
        openEventsFileMenuItem.setEnabled(false);
        loadDumpFileMenuItem.setEnabled(false);
        return null;
    };

    Callable<Void> shutdownHook = () -> {
        // Set the buttons to the completed state
        stoppedButtonState.call();

        // Remove the GreenhouseControls object from the list
        synchronized (GreenhouseGUI.class) {
            gc.remove(id.toString());
        }

        // Nothing else to do
        return null;
    };

    /**
     * <code>GreenhouseGUI</code> constructor
     * Creates a new <code>GreenhouseGUI</code> object
     */
    public GreenhouseGUI(int number) {
        // Set the look and feel to the system's default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Set the window's properties
        setTitle("COMP308 TME4 Part 2 Demo - Window " + number);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the components
        filenameLabel = new JLabel("Selected Program: None");
        messagesArea = new JTextArea();
        createMenuBar();
        createPopupMenu();

        // Set the layout
        setLayout(new BorderLayout());

        // Add the components to the window
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(messagesArea), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Add window listener to warn on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (GreenhouseGUI.class) {
                    if (!gc.isEmpty()) {
                        int result = JOptionPane.showConfirmDialog(GreenhouseGUI.this,
                                "Program still running, close window?",
                                "Close Window",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            // Stop all the programs
                            for (GreenhouseControls g : gc.values()) {
                                g.stop();
                            }

                            // Close the window
                            setVisible(false);
                            dispose();
                        } else {
                            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        }
                    }
                }
            }
        });

        // Add mouse listeners to the window and the messages area
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        messagesArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // Make the window visible
        setVisible(true);

        // Focus the "Open Program" button
        openFileButton.requestFocus();

        // Log the initial message
        try {
            logWriter.write("ℹ️ Select a program file or a dump file to start\n");
            logWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <code>createHeaderPanel</code> method
     * Creates the header panel
     * @return the header panel
     */
    private JPanel createHeaderPanel() {
        // Create the header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create the "Open Program" button
        openFileButton = new JButton("Open Program");
        openFileButton.addActionListener(e -> selectProgramFile());

        // Add the components to the header panel
        headerPanel.add(openFileButton);
        headerPanel.add(filenameLabel);

        return headerPanel;
    }

    /**
     * <code>createFooterPanel</code> method
     * Creates the footer panel
     * @return the footer panel
     */
    private JPanel createFooterPanel() {
        // Create the footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Create the buttons
        startButton = new JButton(UIAction.START.getAction());
        startButton.addActionListener(new ActionListener(UIAction.START.getAction()));
        footerPanel.add(startButton);
        startButton.setEnabled(false);

        restartButton = new JButton(UIAction.RESTART.getAction());
        restartButton.addActionListener(new ActionListener(UIAction.RESTART.getAction()));
        footerPanel.add(restartButton);
        restartButton.setEnabled(false);

        terminateButton = new JButton(UIAction.TERMINATE.getAction());
        terminateButton.addActionListener(new ActionListener(UIAction.TERMINATE.getAction()));
        footerPanel.add(terminateButton);
        terminateButton.setEnabled(false);

        suspendButton = new JButton(UIAction.SUSPEND.getAction());
        suspendButton.addActionListener(new ActionListener(UIAction.SUSPEND.getAction()));
        footerPanel.add(suspendButton);
        suspendButton.setEnabled(false);

        resumeButton = new JButton(UIAction.RESUME.getAction());
        resumeButton.addActionListener(new ActionListener(UIAction.RESUME.getAction()));
        footerPanel.add(resumeButton);
        resumeButton.setEnabled(false);

        return footerPanel;
    }

    /**
     * <code>createMenuBar</code> method
     * Creates the menu bar
     * Adds the file menu to the menu bar
     */
    private void createMenuBar() {
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Create the file menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Add the menu items to the file menu
        JMenuItem newWindowMenuItem = new JMenuItem(UIAction.NEW_WINDOW.getAction());
        newWindowMenuItem.addActionListener(new ActionListener(UIAction.NEW_WINDOW.getAction()));
        newWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(newWindowMenuItem);

        JMenuItem closeWindowMenuItem = new JMenuItem(UIAction.CLOSE_WINDOW.getAction());
        closeWindowMenuItem.addActionListener(new ActionListener(UIAction.CLOSE_WINDOW.getAction()));
        closeWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(closeWindowMenuItem);

        fileMenu.addSeparator();

        openEventsFileMenuItem = new JMenuItem(UIAction.OPEN_EVENTS_FILE.getAction());
        openEventsFileMenuItem.addActionListener(new ActionListener(UIAction.OPEN_EVENTS_FILE.getAction()));
        openEventsFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(openEventsFileMenuItem);

        loadDumpFileMenuItem = new JMenuItem(UIAction.LOAD_DUMP_FILE.getAction());
        loadDumpFileMenuItem.addActionListener(new ActionListener(UIAction.LOAD_DUMP_FILE.getAction()));
        loadDumpFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(loadDumpFileMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem(UIAction.EXIT.getAction());
        exitMenuItem.addActionListener(new ActionListener(UIAction.EXIT.getAction()));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(exitMenuItem);
    }

    /**
     * <code>createPopupMenu</code> method
     * Creates a popup menu
     */
    private void createPopupMenu() {
        // Create the popup menu
        popupMenu = new JPopupMenu();

        // Add the menu items to the popup menu
        startMenuItem = new JMenuItem(UIAction.START.getAction());
        startMenuItem.addActionListener(new ActionListener(UIAction.START.getAction()));
        popupMenu.add(startMenuItem);
        startMenuItem.setEnabled(false);

        restartMenuItem = new JMenuItem(UIAction.RESTART.getAction());
        restartMenuItem.addActionListener(new ActionListener(UIAction.RESTART.getAction()));
        popupMenu.add(restartMenuItem);
        restartMenuItem.setEnabled(false);

        terminateMenuItem = new JMenuItem(UIAction.TERMINATE.getAction());
        terminateMenuItem.addActionListener(new ActionListener(UIAction.TERMINATE.getAction()));
        popupMenu.add(terminateMenuItem);
        terminateMenuItem.setEnabled(false);

        suspendMenuItem = new JMenuItem(UIAction.SUSPEND.getAction());
        suspendMenuItem.addActionListener(new ActionListener(UIAction.SUSPEND.getAction()));
        popupMenu.add(suspendMenuItem);
        suspendMenuItem.setEnabled(false);

        resumeMenuItem = new JMenuItem(UIAction.RESUME.getAction());
        resumeMenuItem.addActionListener(new ActionListener(UIAction.RESUME.getAction()));
        popupMenu.add(resumeMenuItem);
        resumeMenuItem.setEnabled(false);
    }

    /**
     * <code>selectProgramFile</code> method
     * Opens a file chooser dialog to select a file
     */
    private void selectProgramFile() {
        // Create a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();

        // Set the file filter
        fileChooser.setFileFilter(new FileNameExtensionFilter("Program Files", "txt"));

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            String selectedFile = fileChooser.getSelectedFile().getName();
            programFile = fileChooser.getSelectedFile().getAbsolutePath();
            dumpFile = null;

            // Update the filename label
            filenameLabel.setText("Selected Program: " + selectedFile);

            // Set the buttons state
            try {
                initialButtonState.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <code>selectDumpFile</code> method
     * Opens a file chooser dialog to select a file
     */
    private void selectDumpFile() {
        // Create a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();

        // Set the file filter
        fileChooser.setFileFilter(new FileNameExtensionFilter("Dump Files", "out"));

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            String selectedFile = fileChooser.getSelectedFile().getName();
            dumpFile = fileChooser.getSelectedFile().getAbsolutePath();
            programFile = null;

            // Update the filename label
            filenameLabel.setText("Loaded Dump: " + selectedFile);

            // Set the buttons state
            try {
                initialButtonState.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <code>startController</code> method
     * Starts the greenhouse controller
     */
    private void startController() {
        try {
            if (programFile != null) {
                // Clear the messages area
                messagesArea.setText("");

                // Create a new greenhouse controller
                synchronized (GreenhouseGUI.class) {
                    // Create a new greenhouse controller
                    GreenhouseControls controls = new GreenhouseControls(logWriter);

                    // Set the shutdown hook and load the program
                    controls.setShutdownHook(shutdownHook);
                    controls.loadProgram(programFile, logWriter);

                    // Add the greenhouse controller to the list
                    gc.put(id.toString(), controls);
                }
            } else if (dumpFile != null) {
                // Clear the messages area
                synchronized (GreenhouseGUI.class) {
                    gc.put(id.toString(), GreenhouseControls.restore(dumpFile, logWriter, shutdownHook));
                }
            } else {
                messagesArea.append("❗️ Error: cannot start. No program or dump file selected\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * <code>ActionListener</code> class
     * Handles the menu actions
     */
    private class ActionListener implements java.awt.event.ActionListener {
        // Internal variables
        private final String action;

        /**
         * <code>ActionListener</code> constructor
         * Creates a new <code>ActionListener</code> object
         * @param action the action to handle
         */
        public ActionListener(String action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle the menu actions
            try {
                if (UIAction.NEW_WINDOW.getAction().equals(action)) {
                    new GreenhouseGUI(++lastId);
                } else if (UIAction.CLOSE_WINDOW.getAction().equals(action)) {
                    dispatchEvent(new WindowEvent(GreenhouseGUI.this, WindowEvent.WINDOW_CLOSING));
                } else if (UIAction.OPEN_EVENTS_FILE.getAction().equals(action)) {
                    selectProgramFile();
                } else if (UIAction.LOAD_DUMP_FILE.getAction().equals(action)) {
                    selectDumpFile();
                } else if (UIAction.EXIT.getAction().equals(action)) {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    dispatchEvent(new WindowEvent(GreenhouseGUI.this, WindowEvent.WINDOW_CLOSING));
                } else if (UIAction.START.getAction().equals(action)) {
                    logWriter.write("ℹ️ Starting program " + (programFile != null ? programFile : dumpFile) + "\n");
                    logWriter.flush();
                    startController();
                    runningButtonState.call();
                } else if (UIAction.RESTART.getAction().equals(action)) {
                    logWriter.write("ℹ️ Restarting program " + programFile + "\n");
                    logWriter.flush();
                    synchronized (GreenhouseGUI.class) {
                        GreenhouseControls controls = gc.get(id.toString());
                        if (controls != null) {
                            controls.stop();
                        }
                    }
                    startController();
                    runningButtonState.call();
                } else if (UIAction.TERMINATE.getAction().equals(action)) {
                    logWriter.write("ℹ️ Terminating program\n");
                    logWriter.flush();
                    synchronized (GreenhouseGUI.class) {
                        GreenhouseControls controls = gc.get(id.toString());
                        if (controls != null) {
                            controls.stop();
                        }
                    }
                    stoppedButtonState.call();
                } else if (UIAction.SUSPEND.getAction().equals(action)) {
                    logWriter.write("ℹ️ Suspending program\n");
                    logWriter.flush();
                    synchronized (GreenhouseGUI.class) {
                        GreenhouseControls controls = gc.get(id.toString());
                        if (controls != null) {
                            controls.suspend();
                        }
                    }
                    suspendedButtonState.call();
                } else if (UIAction.RESUME.getAction().equals(action)) {
                    logWriter.write("ℹ️ Resuming program\n");
                    logWriter.flush();
                    synchronized (GreenhouseGUI.class) {
                        GreenhouseControls controls = gc.get(id.toString());
                        if (controls != null) {
                            controls.resume();
                        }
                    }
                    runningButtonState.call();
                } else {
                    messagesArea.append("❗️ Error: unsupported action\n");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * <code>LogWriter</code> class
     * Writes log messages to the messages area
     */
    private class LogWriter extends Writer {
        // Internal variables
        private final StringBuilder buffer = new StringBuilder();

        /**
         * <code>write</code> method
         * Writes the log message to the messages area
         * @param cbuf the character buffer
         * @param off the offset
         * @param len the length
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            buffer.append(cbuf, off, len);
        }

        /**
         * <code>flush</code> method
         * Flushes the log message to the messages area
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void flush() throws IOException {
            messagesArea.append(buffer.toString());
            buffer.setLength(0);
        }

        /**
         * <code>close</code> method
         * Closes the log writer
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void close() throws IOException {
            flush();
        }
    }

    /**
     * <code>main</code> method
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // Create a new greenhouse GUI
        SwingUtilities.invokeLater(() -> new GreenhouseGUI(++lastId));
    }
}
