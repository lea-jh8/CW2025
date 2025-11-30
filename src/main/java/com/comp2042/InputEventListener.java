package com.comp2042;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData getCurrentViewData();

    void createNewGame();

    void pauseGame();

    void quitGame();

    void continueGame();

    void initGame();
}
