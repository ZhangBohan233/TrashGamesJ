package connection;

import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GameConnection {

    public static final int PORT = 3456;

    public static final byte START = 0;

    public static final byte CONFIRM = 1;

    private ServerSocket serverSocket;

    private Socket[] clientSockets;

    private int currentNum;

    public GameConnection(int clientNum) {
        clientSockets = new Socket[clientNum];
    }

    public void createServer() throws IOException {
        serverSocket = new ServerSocket(PORT, 50, InetAddress.getLocalHost());
    }

    public int acceptOne() throws IOException {
        int num = currentNum;
        clientSockets[currentNum++] = serverSocket.accept();
        return num;
    }

    public boolean isFull() {
        return currentNum == clientSockets.length;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Socket[] getClientSockets() {
        return clientSockets;
    }

    public void broadcastStart() throws IOException {
        for (Socket socket : clientSockets) {
            OutputStream os = socket.getOutputStream();
            os.write(new byte[]{START});
            os.flush();
            InputStream is = socket.getInputStream();
            byte[] buf = new byte[1];
            if (is.read(buf) != 1) {
                throw new IOException("Client no response");
            }
            System.out.println(buf[0]);
            if (buf[0] != CONFIRM) {
                throw new IOException("Connection error");
            }
        }
    }

    public static void clientListenToStart(Socket client, Runnable runAfter) {
        try {
            byte[] buf = new byte[1];
            InputStream is = client.getInputStream();

            if (is.read(buf) != 1) {
                throw new IOException("No response");
            }

            if (buf[0] == GameConnection.START) {
                OutputStream os = client.getOutputStream();
                os.write(new byte[]{GameConnection.CONFIRM});
                os.flush();

                Platform.runLater(runAfter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
