package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainUI extends Application {

    public static Locale locale = Locale.CHINA;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = makeView(primaryStage, "mainUI.fxml");

        primaryStage.show();
    }

    @FXML
    void doubleGame() throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = makeView(stage, "xiangQiUI.fxml");

        XiangQiUI gameUI = loader.getController();
        gameUI.setConnection(null, null, false, true);
        stage.show();
    }

    @FXML
    void lanGame() throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = makeView(stage, "lobbyUI.fxml");

        LobbyUI lui = loader.getController();
        lui.parent = this;
        lui.setLobbyStage(stage);

        stage.show();
    }

    FXMLLoader makeView(Stage stage, String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));

        loader.setResources(ResourceBundle.getBundle("bundles.language", locale, new ResourceBundleControl()));
//        ResourceBundle.getBund
//        loader.setR

        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        return loader;
    }
}
