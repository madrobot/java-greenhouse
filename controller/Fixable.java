/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Fixable.java
 * description: Interface for Fixable objects
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

/**
 * <b>Fixable Interface</b> - Interface for Fixable objects
 */
public interface Fixable {
    /**
     * <b>fix() Method</b>
     * Fixes the state
     * @return String
     */
    State<Component, String> fix ();

    /**
     * <b>log() Method</b>
     * Returns the string representation of the event
     * @return String
     */
    String log();
}
