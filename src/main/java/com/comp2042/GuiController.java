package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Pane gameRoot;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private BorderPane pauseMenuPanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private Text pauseScoreText;

    @FXML
    public Text finalScore;

    @FXML
    private Text scoreValue;

    @FXML
    public Text highScore;

    @FXML
    private BorderPane gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private int[][] gridData;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getResource("/fonts/VT323.ttf").toExternalForm(), 38);
        Font.loadFont(getClass().getResource("/fonts/PressStart2P.ttf").toExternalForm(), 38);
        Font.loadFont(getClass().getResource("/fonts/RussoOne.ttf").toExternalForm(), 38);

        if (gameRoot != null) {
            String theme = ThemeManage.getCurrentBackground();

            gameRoot.setStyle("-fx-background-image: url('" + theme + "');" +
                    "-fx-background-size: cover;");
        }

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        groupNotification.setVisible(false);

        updateHighScore();

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        this.gridData = boardMatrix;

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }


        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        double hGap = gamePanel.getHgap();
        double vGap = gamePanel.getVgap();
        double startX = gameBoard.getLayoutX() + gamePanel.getLayoutX();
        double startY = gameBoard.getLayoutY() + gamePanel.getLayoutY();

        brickPanel.setLayoutX(startX + brick.getxPosition() * (BRICK_SIZE + hGap));
        brickPanel.setLayoutY(-42 + startY + brick.getyPosition() * (BRICK_SIZE + vGap));


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    public void refreshFallingBrick(ViewData brick) {
        int[][] shape = brick.getBrickData();
        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 0) continue;
                int x = xPos + j;
                int y = yPos + i;

                if (y >= 2 && y < displayMatrix.length && x >= 0 && x < displayMatrix[0].length) {

                    displayMatrix[y][x].setFill(getFillColor(brick.getBrickData()[i][j]));
                }
            }
        }
    }

    public void refreshNextBrick(ViewData brick) {
        brickPanel.getChildren().clear();
        int[][] nextBrick = brick.getNextBrickData();

        for (int i = 0; i < nextBrick.length; i++) {
            for (int j = 0; j < nextBrick[i].length; j++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setFill(getFillColor(nextBrick[i][j]));
                brickPanel.add(rect, j, i);
            }
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {

            double hGap = gamePanel.getHgap();
            double vGap = gamePanel.getVgap();

            double startX = gameBoard.getLayoutX() + gamePanel.getLayoutX();
            double startY = gameBoard.getLayoutY() + gamePanel.getLayoutY();

            brickPanel.setLayoutX(startX + brick.getxPosition() * (BRICK_SIZE + hGap));
            brickPanel.setLayoutY(-42 + startY + brick.getyPosition() * (BRICK_SIZE + vGap));

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] boardMatrix) {
        this.gridData = boardMatrix;

        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                setRectangleData(boardMatrix[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);

            refreshGameBackground(this.gridData);
            refreshFallingBrick(downData.getViewData());
            refreshNextBrick(downData.getViewData());

            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreValue.textProperty().bind(integerProperty.asString());
    }

    public void updateHighScore() {
        int topScore = ScoreManage.getTopScore();
        highScore.setText(String.valueOf(topScore));
    }

    public void gameOver() {
        timeLine.stop();
        groupNotification.setVisible(true);
        gameOverPanel.setVisible(true);
        groupNotification.toFront();
        pauseMenuPanel.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.TRUE);
        finalScore.setText(scoreValue.getText());
        // Display score
        String cleanText = scoreValue.getText().replaceAll("[^0-9]", "");
        if (!cleanText.isEmpty()) {
            int actualScore = Integer.parseInt(cleanText);
            ScoreManage.saveScore(actualScore);
        }
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        pauseMenuPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        pauseMenuPanel.setVisible(true);
        groupNotification.setVisible(false);
        isPause.setValue(Boolean.TRUE);
        isGameOver.setValue(Boolean.FALSE);
        eventListener.pauseGame();
        if (pauseScoreText != null && scoreValue != null) {
            pauseScoreText.setText(scoreValue.getText());
        }
    }

    public void continueGame(ActionEvent actionEvent) {
        timeLine.play();
        pauseMenuPanel.setVisible(false);
        gameOverPanel.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        eventListener.continueGame();
    }

    public void restartGame(ActionEvent actionEvent) {
        timeLine.stop();
        pauseMenuPanel.setVisible(false);
        groupNotification.setVisible(false);
        gameOverPanel.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        updateHighScore();
        eventListener.initGame(); //Reset board
        timeLine.play(); //Restart loop
        gamePanel.requestFocus();
    }

    public void backToMain(ActionEvent event) throws IOException {
        timeLine.stop();
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        Node source = (Node) event.getSource();
        Scene currentScene = source.getScene();
        Stage currentStage = (Stage) currentScene.getWindow();

        Scene gameScene = new Scene(root);
        gameScene.getStylesheets().add(getClass().getResource("/window_style.css").toExternalForm());
        currentStage.setScene(gameScene);
        currentStage.show();
    }

    public void quitGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        eventListener.quitGame();
    }

}
