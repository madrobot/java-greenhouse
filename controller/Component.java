/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Component.java
 * description: An enum to represent the components of the system
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package controller;

/**
 * <b>Component Enum</b> - An enum to represent the components of the system
 */
public enum Component {
    TERMINAL("Terminal"),
    BELL("Bell"),
    LIGHT("Light"),
    THERMOSTAT("Thermostat"),
    WINDOW("Window"),
    WATER("Water"),
    FAN("Fan"),
    POWER("Power");

    // Internal variables
    private final String component;

    /**
     * <b>Component Constructor</b>
     * Creates a component
     * @param component The component name
     */
    Component(String component) {
        this.component = component;
    }

    /**
     * <b>getComponent() Method</b>
     * Gets the component
     * @return The component
     */
    public String getComponent() {
        return component;
    }
}
