package discpative.controller;

import discpative.view.ViewInterface;

public interface ControllerInterface {
    void handleMove(ViewInterface view, Direction direction);
    void handleComplete (ViewInterface view);
}
