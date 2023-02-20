package common.nimbus.tournament;

public interface Logger {
    /**
     * Logs an info message
     * @param message
     */
    void info(String message);

    /**
     * Logs an error message
     * @param message
     */
    void error(String message);
}
