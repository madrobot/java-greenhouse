/**
 * Description: Event abstract class
 * <p>
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Threadable.java
 * description: Turn on the light
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

/**
 * <b>Threadable Interface</b> - An interface for threadable objects
 */
public interface Threadable extends Runnable {
    boolean isRunning();
    void resume();
    void suspend();
    void stop();
    void shutdown(ControllerException e);
}
