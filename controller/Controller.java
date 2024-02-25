/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Controller.java
 * description: A reusable framework for control systems
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;


/**
 * <b>Controller Class</b> - The reusable framework for control systems.
 */
public class Controller implements Serializable, Threadable {
    // Internal variables
    protected final HashMap<UUID, Event> eventList = new HashMap<>();
    protected final Stack<State<Component, String>> states = new Stack<>();
    protected int errorCode = 0;

    protected volatile transient boolean running;
    protected transient Writer logWriter;
    private transient Callable<Void> onShutdown;

    // Create the log, error, and done streams
    private transient PipedOutputStream logOutputStream;
    private transient PipedInputStream logInputStream;
    protected transient DataOutputStream logDataOutputStream;
    private transient DataInputStream logDataInputStream;

    private transient PipedOutputStream errorOutputStream;
    private transient PipedInputStream errorInputStream;
    protected transient ObjectOutputStream errorObjectOutputStream;
    private transient ObjectInputStream errorObjectInputStream;

    private transient PipedOutputStream doneOutputStream;
    private transient PipedInputStream doneInputStream;
    protected transient ObjectOutputStream doneObjectOutputStream;
    private transient ObjectInputStream doneObjectInputStream;

    // Create the done, error, and log stream listener threads
    private transient Thread logStreamListenerThread;
    private transient Thread errorStreamListenerThread;
    private transient Thread doneStreamListenerThread;

    /**
     * <b>Controller Constructor</b>
     * Creates a Controller
     */
    public Controller(Writer logWriter) {
        this(logWriter, null);
    }

    /**
     * <b>Controller Constructor</b>
     * Creates a Controller
     */
    public Controller(Writer logWriter, Callable<Void> onShutdown) {
        this.logWriter = logWriter;
        this.onShutdown = onShutdown;
        run();
    }

    /**
     * <b>setLogWriter() Method</b>
     * Sets the log writer
     * @param logWriter The log writer
     */
    public void setLogWriter(Writer logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * <b>setShutdownHook() Method</b>
     * Sets the shutdown hook
     * @param onShutdown The shutdown hook
     */
    public void setShutdownHook(Callable<Void> onShutdown) {
        this.onShutdown = onShutdown;
    }

    /**
     * <b>addEvent() Method</b>
     * Adds an event to the event list
     * @param event The event to add
     */
    public void addEvent(Event event) {
        // Set the event's state
        event.setStates(states);

        // Set the event log stream
        event.setLogStream(logDataOutputStream);

        // Set the event error stream
        event.setErrorStream(errorObjectOutputStream);

        // Set the event done stream
        event.setDoneStream(doneObjectOutputStream);

        // Add the event to the list
        eventList.put(event.id, event);
    }

    /**
     * <b>getStatesString() Method</b>
     * Returns the states as a string
     * @return String
     */
    protected String getStatesString() {
        return states.toString();
    }

    /**
     * <b>isRunning() Method</b>
     * Checks if the controller is running
     * @return boolean
     */
    public boolean isRunning() {
        return logStreamListenerThread.isAlive() ||
                errorStreamListenerThread.isAlive() ||
                doneStreamListenerThread.isAlive() ||
                eventList.values().stream().anyMatch(Event::isRunning);
    }

    /**
     * <b>resume() Method</b>
     * Resumes the controller
     */
    public void resume() {
        for (Event event : eventList.values())
            event.resume();
    }

    /**
     * <b>resume() Method</b>
     * Resumes the controller
     */
    public void suspend() {
        for (Event event : eventList.values())
            event.suspend();
    }

    /**
     * <b>stop() Method</b>
     * Stops the controller
     */
    public void stop() {
        // Suspend all events
        for (Event event : eventList.values())
            event.suspend();

        // Wait until all events are suspended
        while (eventList.values().stream().anyMatch(Event::isRunning))
            Thread.onSpinWait();

        // Stop all events
        for (Event event : eventList.values())
            event.stop();

        // Close the input and output streams
        try {
            Closeable[] streams = new Closeable[] {
                    logDataInputStream, logDataOutputStream, logInputStream, logOutputStream,
                    errorObjectInputStream, errorObjectOutputStream, errorInputStream, errorOutputStream,
                    doneObjectInputStream, doneObjectOutputStream, doneInputStream, doneOutputStream
            };

            for (Closeable stream : streams)
                if (stream != null)
                    stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Stop the log, error, and done stream listener threads
        running = false;

        // Call the shutdown hook
        if (onShutdown != null) {
            try {
                onShutdown.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <b>shutdown() Method</b>
     * Shuts down the controller
     * @param e The exception that caused the shutdown
     */
    public void shutdown(ControllerException e) {
        // Warn the user that the program is shutting down
        try {
            logWriter.write("💤 Shutting down...\n");
            logWriter.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Stop the controller
        stop();

        // Set the error code
        errorCode = e.getCode();

        // Remove the erroneous event from the list
        eventList.remove(e.getEvent().id);

        // Compose the message and print to console
        String message = "Error: "+e.getMessage()+". "
                + getStatesString()
                + " {events=" + eventList.size()
                + ", timestamp=" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                + "}";

        // Handle outputs
        try {
            // Print the message to the console
            logWriter.write("❗️ " + message + "\n");
            logWriter.flush();

            // Add the message to error.log file
            write(message, "error.log");

            // Serialize and save the entire GreenhouseControls object in a file dump.out
            write(this, "dump.out");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * <b>start() Method</b>
     * Starts the controller
     */
    @Override
    public void run() {
        try {
            this.running = true;
            runLogStreamListener();
            runErrorStreamListener();
            runDoneStreamListener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <b>runLogStreamListener() Method</b>
     * Runs the log stream listener
     * @throws IOException If an I/O error occurs
     */
    protected void runLogStreamListener() throws IOException {
        // If the thread is alive, do nothing
        if (logStreamListenerThread != null && logStreamListenerThread.isAlive())
            return;

        // Create the streams
        logOutputStream = new PipedOutputStream();
        logInputStream = new PipedInputStream(logOutputStream);
        logDataOutputStream = new DataOutputStream(logOutputStream);
        logDataInputStream = new DataInputStream(logInputStream);

        // Create the log stream listener
        logStreamListenerThread = new Thread(() -> {
            while (!Thread.interrupted() && running) {
                try {
                    logWriter.write(logDataInputStream.readUTF() + "\n");
                    logWriter.flush();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }, "LogStreamListener");

        // Start the log stream listener thread
        logStreamListenerThread.start();
    }

    /**
     * <b>runErrorStreamListener() Method</b>
     * Runs the error stream listener
     * @throws IOException If an I/O error occurs
     */
    protected void runErrorStreamListener() throws IOException {
        // If the thread is alive, do nothing
        if (errorStreamListenerThread != null && errorStreamListenerThread.isAlive())
            return;

        // Create the streams
        errorOutputStream = new PipedOutputStream();
        errorInputStream = new PipedInputStream(errorOutputStream);
        errorObjectOutputStream = new ObjectOutputStream(errorOutputStream);
        errorObjectInputStream = new ObjectInputStream(errorInputStream);

        // Create the error stream listener
        errorStreamListenerThread = new Thread(() -> {
            while (!Thread.interrupted() && running) {
                try {
                    // Read the error from the stream
                    ControllerException error = (ControllerException) errorObjectInputStream.readObject();

                    // Write the Event string to the log
                    logWriter.write(error.getEvent().toString() + "\n");
                    logWriter.flush();

                    // Shutdown the controller
                    shutdown(error);
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }, "ErrorStreamListener");

        // Start the error stream listener thread
        errorStreamListenerThread.start();
    }

    /**
     * <b>runDoneStreamListener() Method</b>
     * Runs the done stream listener
     * @throws IOException If an I/O error occurs
     */
    protected void runDoneStreamListener() throws IOException {
        // If the thread is alive, do nothing
        if (doneStreamListenerThread != null && doneStreamListenerThread.isAlive())
            return;

        // Create the streams
        doneOutputStream = new PipedOutputStream();
        doneInputStream = new PipedInputStream(doneOutputStream);
        doneObjectOutputStream = new ObjectOutputStream(doneOutputStream);
        doneObjectInputStream = new ObjectInputStream(doneInputStream);

        // Create the done stream listener
        doneStreamListenerThread = new Thread(() -> {
            while (!Thread.interrupted() && running) {
                try {
                    // Read the event from the stream
                    Event event = (Event) doneObjectInputStream.readObject();

                    // Wait until the event is no longer running
                    while (event.isRunning())
                        Thread.onSpinWait();

                    // Remove the event from the list
                    eventList.remove(event.id);

                    // Print a message to the console
                    if (eventList.isEmpty()) {
                        // Print a message to the console
                        logWriter.write("🏁 All events have been processed!\n");
                        logWriter.flush();

                        // Stop the controller
                        stop();
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }, "DoneStreamListener");

        // Start the done stream listener thread
        doneStreamListenerThread.start();
    }

    /**
     * <b>write() Method</b>
     * Writes a message to a file
     *
     * @param message  The message to write
     * @param filename The filename to write to
     */
    protected void write(String message, String filename) throws IOException {
        java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename, true));
        out.println(message);
        out.close();
    }

    /**
     * <b>write() Method</b>
     * Writes an object to a file
     * @param object The object to write
     * @param filename The filename to write to
     */
    protected void write(Serializable object, String filename) throws IOException {
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filename));
        out.writeObject(object);
        out.close();
    }
}
