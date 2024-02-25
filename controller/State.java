/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: State.java
 * description: A generic class to hold a component and status tuple
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

import java.io.Serializable;

/**
 * <b>State Class</b> - A generic class to hold a component and status tuple
 */
public record State<Component, Status>(Component component, Status status) implements Serializable {
}
