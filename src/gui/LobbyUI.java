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
import java.io.OutputStream;
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

    @FXML
    Button closeRoomButton;

    MainUI parent;

    private GameConnection gameConnection;

    private ResourceBundle resources;

    private Stage lobbyStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
    }

    public void setLobbyStage(Stage lobbyStage) {
        this.lobbyStage = lobbyStage;

        lobbyStage.setOnCloseRequest(e -> {
            if (gameConnection != null) {
                gameConnection.close();
            }
        });
    }

    @FXML
    void createRoomAction() {
        try {
            String localHostAddress = textField.getText();
            gameConnection = new GameConnection(localHostAddress, 1);
            gameConnection.createServer();

            messageLabel.setText(resources.getString("create_success"));
            closeRoomButton.setDisable(false);

            System.out.println("Listening on ip " + localHostAddress);

            refreshListOwner();

            Thread listen = new Thread(() -> {
                try {
                    while (!gameConnection.isFull()) {
                        gameConnection.acceptOne();
                        Platform.runLater(this::refreshListOwner);
                    }
                    Platform.runLater(() -> {
                        startGameButton.setDisable(false);
                        messageLabel.setText(resources.getString("ready"));
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            listen.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    private void joinRoomAction() {
        try {
            String ip = textField.getText();
            Socket client = new Socket();
            client.connect(new InetSocketAddress(ip, PORT));

            messageLabel.setText(resources.getString("connect_success"));
            System.out.println("Connected to " + ip);


            Thread listening = new Thread(
                    () -> GameConnection.clientListenToStart(client, () -> startGame(client, false), playerList));
            listening.start();

        } catch (IOException e) {
            messageLabel.setText(resources.getString("connect_failed"));
            e.printStackTrace();
        }
    }

    @FXML
    void closeRoomAction() {
        gameConnection.close();
        closeRoomButton.setDisable(true);

        refreshListOwner();
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

    private void refreshListGuest() {

    }

    private void refreshListOwner() {
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
                stage.setOnCloseRequest(e -> {
                    gameConnection.broadcastClose();
                    gameConnection.close();
                });
            } else {
                title = loader.getResources().getString("black_side");
                stage.setOnCloseRequest(e -> {
                    try {
                        OutputStream os = client.getOutputStream();
                        os.write(new byte[]{GameConnection.CLOSE});
                        os.flush();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                });
            }
            stage.setTitle(title);

            XiangQiUI gameUI = loader.getController();

            gameUI.setConnection(client.getInputStream(), client.getOutputStream(), isServer, false);
            gameUI.listen();

            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
