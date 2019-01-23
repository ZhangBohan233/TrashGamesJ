package gui;

import connection.GameConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class LobbyUI implements Initializable {

    public final static int PORT = 3456;

    @FXML
    TextField textField;

    @FXML
    ListView<String> playerList;

    @FXML
    Button startGameButton;

    @FXML
    Label messageLabel;

    MainUI parent;

    private GameConnection gameConnection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void createRoomAction() {
        try {
//            ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getLocalHost());
            String localHostAddress = textField.getText();
            gameConnection = new GameConnection(localHostAddress, 1);
            gameConnection.createServer();

            System.out.println("Listening on ip " + localHostAddress);

            refreshList();

            Thread thread = new Thread(() -> {
                try {
                    while (!gameConnection.isFull()) {
                        gameConnection.acceptOne();
                        Platform.runLater(this::refreshList);
                    }
                    Platform.runLater(() -> startGameButton.setDisable(false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    void joinRoomAction() {
        try {
            String ip = textField.getText();
            Socket client = new Socket();
            client.connect(new InetSocketAddress(ip, PORT));

            System.out.println("Connected to " + ip);

            Thread listening = new Thread(
                    () -> GameConnection.clientListenToStart(client, () -> startGame(client, false)));
            listening.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void getLocalHostAction() throws IOException {
        String localHost = InetAddress.getLocalHost().getHostAddress();
        textField.setText(localHost);
    }

    @FXML
    void startGameAction() throws IOException {
        gameConnection.broadcastStart();

        startGame(gameConnection.getClientSockets()[0], true);
    }

    private void refreshList() {
        playerList.getItems().clear();
        if (gameConnection != null) {
            playerList.getItems().add(gameConnection.getServerSocket().getInetAddress().toString());
            for (Socket socket : gameConnection.getClientSockets()) {
                if (socket != null)
                    playerList.getItems().add(socket.getInetAddress().toString());
            }
        }
    }

    private void startGame(Socket client, boolean isServer) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = parent.makeView(stage, "xiangQiUI.fxml");

            String title;
            if (isServer) {
                title = loader.getResources().getString("red_side");
            } else {
                title = loader.getResources().getString("black_side");
            }
            stage.setTitle(title);

            XiangQiUI gameUI = loader.getController();

            gameUI.setResources(loader.getResources());

            gameUI.inputStream = client.getInputStream();
            gameUI.outputStream = client.getOutputStream();

            gameUI.isServer = isServer;
            gameUI.listen();

            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
