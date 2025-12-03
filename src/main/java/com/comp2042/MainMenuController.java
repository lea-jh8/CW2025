package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController {

    @FXML
    public void startGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
        Parent gameRoot = loader.load();

        GuiController c = loader.getController();
        GameController game = new GameController(c);
        c.setEventListener(game);

        game.initGame();

        Node source = (Node) event.getSource();
        Scene currentScene = source.getScene();
        Stage currentStage = (Stage) currentScene.getWindow();

        Scene gameScene = new Scene(gameRoot);
        gameScene.getStylesheets().add(getClass().getResource("/window_style.css").toExternalForm());
        currentStage.setScene(gameScene);
        currentStage.show();

        gameRoot.requestFocus();
    }

    @FXML
    public void quitGame(ActionEvent actionEvent) {
        System.exit(0);
    }

}
