package connection;

import javafx.application.Platform;
import javafx.scene.control.ListView;

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

    public static final byte ASK_USERS = 2;

    public static final byte CLOSE = -1;

    private String serverAddress;

    private ServerSocket serverSocket;

    private Socket[] clientSockets;

    private int currentNum;

    private boolean listeningUsers = true;

    public GameConnection(String serverAddress, int clientNum) {
        clientSockets = new Socket[clientNum];
        this.serverAddress = serverAddress;
    }

    public void createServer() throws IOException {
        serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(serverAddress));
    }

    public void acceptOne() throws IOException {
//        int num = currentNum;
        Socket socket = serverSocket.accept();
        clientSockets[currentNum++] = socket;

        broadcast(getUsers());
    }

    private byte[] getUsers() {
        byte[] data = new byte[(currentNum + 1) * 4];
        byte[] addr = serverSocket.getInetAddress().getAddress();
        System.arraycopy(addr, 0, data, 0, addr.length);
        int index = 4;
        for (int i = 0; i < currentNum; i++) {
            Socket s = clientSockets[i];
            addr = s.getInetAddress().getAddress();
            System.out.println(addr.length);
            System.arraycopy(addr, 0, data, index, addr.length);
            index += addr.length;
        }
        return data;
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

    private void broadcast(byte[] data) throws IOException {
        for (int i = 0; i < currentNum; i++) {
            Socket socket = clientSockets[i];
            OutputStream os = socket.getOutputStream();
            os.write(data);
            os.flush();
        }
    }

    public static void clientListenToStart(Socket client, Runnable runInStart, ListView<String> listView) {
        try {
            boolean start = false;
            byte[] buf = new byte[1024];
            InputStream is = client.getInputStream();

            int read;
            while (!start && (read = is.read(buf)) != -1) {
                if (read == 1) {
                    if (buf[0] == GameConnection.START) {
                        OutputStream os = client.getOutputStream();
                        os.write(new byte[]{GameConnection.CONFIRM});
                        os.flush();

                        start = true;
                        Platform.runLater(runInStart);
                    }
                } else {
                    if (listView != null) {
                        String[] userReps = new String[read / 4];
                        int index = 0;
                        for (int i = 0; i < read; i += 4) {
                            byte[] b = new byte[4];
                            System.arraycopy(buf, i, b, 0, 4);
                            String s = InetAddress.getByAddress(b).toString();
                            userReps[index++] = s;
                        }
                        Platform.runLater(() -> {
                            listView.getItems().clear();
                            for (String s : userReps) {
                                listView.getItems().add(s);
                            }
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastClose() {
        try {
            broadcast(new byte[]{CLOSE});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            serverSocket.close();
            for (Socket s : clientSockets) {
                if (s != null)
                    s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
