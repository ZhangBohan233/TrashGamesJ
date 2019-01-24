package dock;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A JavaFX GUI game with socket network connection.
 */
public interface NetGame {

    /**
     * Start listening to connection changes.
     */
    void listen();

    /**
     * Sets up the connection channel.
     *
     * @param inputStream  the input stream, which the client read data from
     * @param outputStream the output stream, which the client send data to
     * @param isServer     whether the new game instance is the server side
     */
    void setConnection(InputStream inputStream, OutputStream outputStream, boolean isServer);

    /**
     * Starts the GUI game.
     * <p>
     * By calling this method, a new game window will be created.
     */
    void startGame();
}
