package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private VBox leaderboardRoot;

    @FXML
    private VBox menuBox;

    @FXML
    private VBox scoreList;

    @FXML
    private VBox themeRoot;

    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (rootPane != null) {
            String theme = ThemeManage.getCurrentBackground();

            if (theme == null) theme = "/backgrounds/default.png";

            rootPane.setStyle("-fx-background-image: url('" + theme + "');" +
                    "-fx-background-size: cover;");
        }
    }

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

    // Show Theme Menu
    @FXML
    public void changeTheme(ActionEvent actionEvent) {
        menuBox.setVisible(false);
        leaderboardRoot.setVisible(false);
        themeRoot.setVisible(true);
    }

    //Theme Button Actions
    @FXML
    public void setThemeDefault(ActionEvent actionEvent) {
        applyBackground("/backgrounds/default.png");
    }

    @FXML
    public void setThemeRetro(ActionEvent actionEvent) {
        applyBackground("/backgrounds/retro.png");
    }

    @FXML
    public void setThemeForest(ActionEvent actionEvent) {
        applyBackground("/backgrounds/forest.png");
    }

    @FXML
    public void setThemeCute(ActionEvent actionEvent) {
        applyBackground("/backgrounds/cute.png");
    }

    private void applyBackground (String imagePath) {
        ThemeManage.setBackground(imagePath);
        if (rootPane.getScene() != null && rootPane.getScene().getRoot() != null) {
            rootPane.getScene().getRoot().setStyle(
                    "-fx-background-image: url('" + imagePath + "');" +
                    "-fx-background-size: cover;"
            );
        }
    }

    //Show Leaderboard
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
        themeRoot.setVisible(false);
        menuBox.setVisible(true);
    }

    @FXML
    public void quitGame(ActionEvent actionEvent) {
        System.exit(0);
    }
}
