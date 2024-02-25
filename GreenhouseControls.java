/**
 * The Greenhouse Controls program simulates a greenhouse with various sensors
 * and controls. The program is designed to be flexible and can be configured
 * to run different scenarios by providing a configuration file.
 * <p>
 * Usage Example:
 * ```bash
 * java GreenhouseControls -f examples1.txt
 * ```
 * <p>
 * COMP308 Java for Programmers
 * Athabasca University
 * @author William Salemé (ID: 3556297)
 * @version 1.0
 * @date 2023-12-11
 */

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Callable;

import controller.*;
import event.PowerFix;
import event.WindowFix;

/**
 * <b>GreenhouseControls Class</b> - Creates a GreenhouseControls object
 */
public class GreenhouseControls extends Controller {
    /**
     * <b>Controller Constructor</b>
     * Creates a Greenhouse Controller
     */
    public GreenhouseControls(Writer logWriter) {
        super(logWriter);
    }

    /**
     * <b>main() Method</b>
     * Main method to run the program
     * @param args The command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            // Check if the number of arguments is correct
            String option = args[0];
            String filename = args[1];

            // Check if the option is valid
            if (!(option.equals("-f")) && !(option.equals("-d"))) {
                System.err.println("Invalid option");
                printUsage();
            }

            // Create a log writer
            Writer logWriter = new OutputStreamWriter(System.out);

            // Load the program or restore the system
            if (option.equals("-f")) {
                // Create a GreenhouseControls object
                GreenhouseControls gc = new GreenhouseControls(logWriter);
                gc.loadProgram(filename, logWriter);
            } else if (option.equals("-d")) {
                GreenhouseControls.restore(filename, logWriter, null);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid number of parameters");
            printUsage();
        }
    }

    /**
     * <b>loadProgram() Method</b>
     * Loads the program from a file
     * @param eventsFile The filename to load from
     */
    public void loadProgram(String eventsFile, Writer logWriter) throws IOException {
        // Create a file object
        File file = new File(eventsFile);

        // Create an input processor object
        Processor processor = new Processor(
                this.states,
                this.logDataOutputStream,
                this.errorObjectOutputStream,
                this.doneObjectOutputStream
            );

        // Read the file
        try {
            // Create a scanner object
            Scanner scanner = new Scanner(file);

            // Process each line
            while (scanner.hasNextLine())
                addEvent(processor.processLine(scanner.nextLine()));

            // Close the scanner
            scanner.close();
        } catch (Exception e) {
            logWriter.write("Error reading file: " + e.getMessage() + "\n");
            logWriter.flush();
            System.exit(1);
        }
    }

    /**
     * <b>restore() Method</b>
     * Restores the system from a file
     * @param filename The filename to restore from
     * @return GreenhouseControls
     */
    public static GreenhouseControls restore(String filename, Writer logWriter, Callable<Void> shutdownHook) throws IOException {
        logWriter.write("🔄 Restoring system from file " + filename + "\n");
        logWriter.flush();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            // Read the object from the file
            GreenhouseControls gc = (GreenhouseControls) ois.readObject();

            // Check if the object is null
            if (gc == null) {
                logWriter.write("❗️ Error restoring from file " + filename + "\n");
                logWriter.flush();
                System.exit(1);
            }

            // Set the log writer and shutdown hook
            gc.setLogWriter(logWriter);
            gc.setShutdownHook(shutdownHook);

            // Print the restored object
            logWriter.write("✅ Restored GreenhouseControls with the following state:\n" + gc.getStatesString() + " {events=" + gc.eventList.size() + "}\n\n");
            logWriter.flush();

            // Resume the controller
            gc.run();

            // Fix the error
            Fixable fixer = gc.getFixable(gc.getError());
            gc.states.push(fixer.fix());

            // Send message to logs
            gc.logDataOutputStream.writeUTF(fixer.toString());

            // Log to fix.log file
            gc.write(fixer.log(), "fix.log");

            // Display a log
            logWriter.write("🔄 Resuming system\n");
            logWriter.flush();

            // Set the states and streams on each event and start them
            for (Event event : gc.eventList.values()) {
                event.setStates(gc.states);
                event.setLogStream(gc.logDataOutputStream);
                event.setErrorStream(gc.errorObjectOutputStream);
                event.setDoneStream(gc.doneObjectOutputStream);
                event.resume();
            }

            // Return the restored object
            return gc;
        } catch (Exception e) {
            logWriter.write("Error reading file: " + e.getMessage() + "\n");
            logWriter.flush();
            System.exit(1);
        }
        return null;
    }

    /**
     * <b>getError() Method</b>
     * Returns the error code
     * @return int
     */
    public int getError() {
        return errorCode;
    }

    /**
     * <b>getFixable() Method</b>
     * Returns the fixable object
     * @param errorCode The error code
     * @return Fixable
     */
    public Fixable getFixable(int errorCode) throws Exception {
        return switch (errorCode) {
            case 2 -> new WindowFix();
            case 3 -> new PowerFix();
            default -> throw new Exception("No such fixable for error code: " + errorCode);
        };
    }

    /**
     * <b>GreenhouseControls Constructor</b>
     * Creates a GreenhouseControls object
     */
    public static void printUsage() {
        System.out.println("Correct format: ");
        System.out.println("  java GreenhouseControls -f <filename>, or");
        System.out.println("  java GreenhouseControls -d dump.out");
    }
}
