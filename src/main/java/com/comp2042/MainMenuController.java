package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class MainMenuController {

    @FXML
    private VBox leaderboardRoot;

    @FXML
    private VBox menuBox;

    @FXML
    private VBox scoreList;

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
    public void loadLeaderboard(ActionEvent actionEvent) {
        scoreList.getChildren().clear();
        List<Integer> scores = ScoreManage.getTopScores();

        for (int i = 0; i < scores.size() && i < 3; i++) {
            int score = scores.get(i);
            String text = String.format("#%d: %d", i + 1, score);
            Label scoreLabel = new Label(text);
            scoreLabel.getStyleClass().add("LeaderBoardEntry");
            scoreList.getChildren().add(scoreLabel);
        }

        menuBox.setVisible(false);
        leaderboardRoot.setVisible(true);
    }

    public void backToMenu(ActionEvent actionEvent) {
        leaderboardRoot.setVisible(false);
        menuBox.setVisible(true);
    }

    @FXML
    public void quitGame(ActionEvent actionEvent) {
        System.exit(0);
    }

}
