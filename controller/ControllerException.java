/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: ControllerException.java
 * description: A reusable framework for control systems
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

import java.io.Serializable;

/**
 * <b>ControllerException Class</b> - Creates a ControllerException
 */
public class ControllerException extends Exception implements Serializable {
    private int code = 1;
    private final Event event;

    /**
     * <b>ControllerException Constructor</b>
     * Creates a ControllerException
     *
     * @param message The message for the exception.
     */
    public ControllerException(String message, Event event) {
        super(message);
        this.event = event;
    }

    /**
     * <b>ControllerException Constructor</b>
     * Creates a ControllerException
     *
     * @param message The message for the exception.
     * @param code The code for the exception.
     */
    public ControllerException(String message, int code, Event event) {
        super(message);
        this.code = code;
        this.event = event;
    }

    /**
     * <b>getCode() Method</b>
     * Returns the code for the exception
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * <b>getEvent() Method</b>
     * Returns the event for the exception
     * @return Event
     */
    public Event getEvent() {
        return event;
    }
}
