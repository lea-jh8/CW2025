package com.comp2042;


public class GameController implements InputEventListener {

    private final Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
        board.createNewBrick();
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }


            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            viewGuiController.refreshNextBrick(board.getViewData());

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

        }

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public  ViewData getCurrentViewData() {
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public void pauseGame() {
        board.pauseGame();
    }

    @Override
    public void continueGame(){
        board.continueGame();
    }

    @Override
    public void initGame() {
        board.initGame();
    }

    @Override
    public void quitGame() {
        board.quitGame();
    }

}
