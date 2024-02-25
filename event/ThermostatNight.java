/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: ThermostatNight.java
 * description: Set thermostat to night setting
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>ThermostatNight Class</b> - Creates ThermostatNight event
 */
public class ThermostatNight extends Event implements Serializable {
    /**
     * <b>ThermostatNight() Constructor</b>
     * Constructs a ThermostatNight event
     * @param delayTime The delay time
     */
    public ThermostatNight(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.THERMOSTAT;
    }

    /**
     * <b>action() Method</b>
     * Sets the thermostat to night setting
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "NIGHT");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "⏲️ Thermostat on night setting";
    }
}

