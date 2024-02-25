/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Processor.java
 * description: An enum to represent the components of the system
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

import controller.Component;
import controller.ControllerException;
import controller.Event;
import controller.State;
import event.*;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Processor Class</b> - Processes the input line and returns the corresponding event
 */
public class Processor {
    private final transient DataOutputStream log;
    private final transient ObjectOutputStream err;
    private final transient ObjectOutputStream done;
    private final transient Stack<State<Component, String>> states;

    /**
     * <b>Processor Constructor</b>
     * Creates a Processor
     * @param states The stack of states
     * @param log The log stream
     * @param err The error stream
     * @param done The done stream
     */
    public Processor(Stack<State<Component, String>> states, DataOutputStream log, ObjectOutputStream err, ObjectOutputStream done) {
        this.states = states;
        this.log = log;
        this.err = err;
        this.done = done;
    }

    /**
     * <b>processLine() Method</b>
     * Processes the input line and returns the corresponding event
     *
     * @param line The input line
     * @return Event
     * @throws ControllerException If the line format is invalid
     */
    public Event processLine(String line) throws ControllerException {
        // Create the regex pattern
        Pattern regex = Pattern.compile("(?:(\\w+)=(\\w+))+");
        Matcher matcher = regex.matcher(line);

        // Create the parameters map
        Map<String, String> params = new HashMap<>();
        while (matcher.find()) {
            for (int i = 1; i < matcher.groupCount(); i += 2) {
                String key = matcher.group(i);
                String value = matcher.group(i + 1);
                if (key != null && value != null)
                    params.put(key, value);
            }
        }

        // Check for required parameters
        if (params.get("Event") == null || params.get("time") == null)
            throw new ControllerException("Invalid line format: " + line, null);

        // Get the delay time
        long delayTime = Long.parseLong(params.get("time"));

        // Create the event
        Event event = switch (params.get("Event")) {
            case "Bell" -> {
                int rings = params.get("rings") == null ? 1 : Integer.parseInt(params.get("rings"));
                yield new Bell(delayTime, rings);
            }
            case "FansOn" -> new FansOn(delayTime);
            case "FansOff" -> new FansOff(delayTime);
            case "LightOn" -> new LightsOn(delayTime);
            case "LightOff" -> new LightsOff(delayTime);
            case "PowerOut" -> new PowerOut(delayTime);
            case "Terminate" -> new Terminate(delayTime);
            case "ThermostatNight" -> new ThermostatNight(delayTime);
            case "ThermostatDay" -> new ThermostatDay(delayTime);
            case "WaterOn" -> new WaterOn(delayTime);
            case "WaterOff" -> new WaterOff(delayTime);
            case "WindowMalfunction" -> new WindowMalfunction(delayTime);
            default -> throw new ControllerException("Unsupported event type: " + params.get("Event"), null);
        };

        // Set the states and streams
        event.setStates(states);
        event.setLogStream(log);
        event.setErrorStream(err);
        event.setDoneStream(done);

        // Return the event
        return event;
    }
}
