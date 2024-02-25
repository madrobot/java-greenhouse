/**
 * Description: Event abstract class
 * <p>
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Event.java
 * description: Turn on the light
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

import java.io.*;
import java.util.Stack;
import java.util.UUID;

/**
 * <b>Event Class</b> - Abstract class for events
 */
public abstract class Event implements Serializable, Threadable {
    // Constants
    private final static long recurranceDelay = 2000;

    // Internal variables
    public final UUID id = UUID.randomUUID();
    private volatile EventStatus status = EventStatus.READY;
    private volatile transient Thread thread;
    private long eventTime;
    private long suspendTime = 0;
    private int recurrences = 0;

    // Variables from the main thread
    private transient DataOutputStream log;
    private transient ObjectOutputStream err;
    private transient ObjectOutputStream done;
    private transient Stack<State<Component, String>> states;

    /**
     * <b>Event Constructor</b>
     * Creates an Event with a delay time of 0
     */
    public Event() {
        this(0);
    }

    /**
     * <b>Event Constructor</b>
     * Creates an Event with a delay time
     * @param delayTime The delay time for the event
     */
    public Event(long delayTime) {
        // Set the event time
        this.eventTime = System.currentTimeMillis() + delayTime;

        // Start the main thread
        resume();
    }

    /**
     * <b>getStateComponent</b>
     * Gets the state component
     * @return String
     */
    public abstract Component getStateComponent();

    /**
     * <b>action</b>
     * Performs the action
     * @return State
     * @throws ControllerException If an error occurs
     */
    public abstract State<Component, String> action() throws ControllerException;

    /**
     * <b>setStates</b>
     * Sets the states of the event
     * @param states The states of the event
     */
    public final void setStates(Stack<State<Component, String>> states) {
        this.states = states;
    }

    /**
     * <b>setLogStream</b>
     * Sets the log stream
     * @param logStream The log stream
     */
    public final void setLogStream(DataOutputStream logStream) {
        this.log = logStream;
    }

    /**
     * <b>setErrorStream</b>
     * Sets the error stream
     * @param errorStream The error stream
     */
    public final void setErrorStream(ObjectOutputStream errorStream) {
        this.err = errorStream;
    }

    /**
     * <b>setDoneStream</b>
     * Sets the done stream
     * @param doneStream The done stream
     */
    public final void setDoneStream(ObjectOutputStream doneStream) {
        this.done = doneStream;
    }

    /**
     * <b>setRecurrences</b>
     * Sets the number of recurrences
     * @param recurrences The number of recurrences
     */
    public void setRecurrences(int recurrences) {
        this.recurrences = recurrences;
    }

    /**
     * <b>ready</b>
     * Checks if the event is ready
     * @return boolean
     */
    public boolean ready() {
        return System.currentTimeMillis() >= eventTime;
    }

    /**
     * <b>isRunning</b>
     * Checks if the event is running
     * @return boolean
     */
    public boolean isRunning() {
        return status == EventStatus.RUNNING || status == EventStatus.SUSPENDING;
    }

    /**
     * <b>resume</b>
     * Resumes the event and corrects the time
     */
    public final void resume() {
        // If thread is null, create a new thread
        if (thread == null)
            thread = new Thread(this, getStateComponent().getComponent()+"Event-"+id);

        // Only ready, suspended and stopped events can be resumed
        if (status != EventStatus.READY && status != EventStatus.SUSPENDED && status != EventStatus.STOPPED)
            return;

        // Correct the event time, if necessary
        if (suspendTime > 0)
            eventTime += System.currentTimeMillis() - suspendTime;

        // Reset the suspend time
        suspendTime = 0;

        // Set the status to RUNNING
        status = EventStatus.RUNNING;

        // Start the thread
        if (thread.isAlive()) return;
        thread.start();
    }

    /**
     * <b>suspend</b>
     * Suspends the event and stores the time
     */
    public final void suspend() {
        if (status == EventStatus.RUNNING)
            status = EventStatus.SUSPENDING;
    }

    /**
     * <b>stop</b>
     * Stops the event
     */
    public final void stop() {
        status = EventStatus.STOPPED;
    }

    /**
     * <b>shutdown</b>
     * Shuts down the event and writes the reason to the main thread pipe
     */
    public final void shutdown(ControllerException reason) {
        // Suspend the event
        suspend();

        // Push an error state
        states.push(new State<>(getStateComponent(), "ERROR"));

        // Send the error to the main thread
        try {
            err.writeObject(reason);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <b>log</b>
     * Logs a message to the main thread pipe
     * @param message The message to log
     */
    protected final void log(String message) {
        try {
            log.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <b>complete</b>
     * Completes the event and writes to the done pipe
     */
    private void complete() {
        // Set the status to DONE
        status = EventStatus.COMPLETED;

        // Write to the done pipe
        try {
            done.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <b>run</b>
     * Runs the event and performs the action
     */
    @Override
    public final void run() {
        // Run the event
        while (!Thread.interrupted() && (
                status == EventStatus.RUNNING ||
                status == EventStatus.SUSPENDING ||
                status == EventStatus.SUSPENDED)) {
            Thread.onSpinWait();

            // If event is suspended, do nothing
            if (status == EventStatus.SUSPENDED)
                continue;

            // If suspension is requested, suspend the event
            if (status == EventStatus.SUSPENDING) {
                status = EventStatus.SUSPENDED;
                suspendTime = System.currentTimeMillis();
                continue;
            }

            // Validate the states, error stream, and log stream are set
            if (states == null || log == null || err == null || done == null)
                continue;

            if (status == EventStatus.RUNNING && ready()) {
                try {
                    // Perform the action and pass the last state
                    synchronized (states) {
                        // Perform the action and pass the last state
                        State<Component, String> newState = action();

                        // Add the new state to the top of the list
                        states.push(newState);
                    }

                    // Check if the event has recurrences, otherwise complete event
                    if (recurrences > 0) {
                        recurrences--;
                        eventTime = System.currentTimeMillis() + recurranceDelay;
                    } else {
                        complete();
                    }
                } catch (ControllerException e) {
                    shutdown(e);
                } catch (Exception e) {
                    shutdown(new ControllerException(e.getMessage(), this));
                }
            }
        }
    }
}
