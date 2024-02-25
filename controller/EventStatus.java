/**
 * Description: Event abstract class
 * <p>
 * COMP308 Java for Programmers
 * Athabasca University
 * program: EventStatus.java
 * description: Turn on the light
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

// An enum to represent the status of the event
public enum EventStatus {
    READY, RUNNING, SUSPENDING, SUSPENDED, COMPLETED, STOPPED
}
