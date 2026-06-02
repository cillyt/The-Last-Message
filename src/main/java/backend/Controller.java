package backend;

import javafx.scene.input.KeyEvent;

public class Controller {

    private Player player;

    public Controller(Player player) {
        this.player = player;
    }


    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                player.commandJump();
                break;
            case A:
                player.commandMoveLeft();
                break;
            case S:
                player.commandCrouch();
                break;
            case D:
                player.commandMoveRight();
                break;
            case ENTER:
                player.commandStartShooting();
                break;

            case DIGIT1:
                player.commandEquipPistol();
                break;
            case DIGIT2:
                player.commandEquipAR();
                break;
        }
    }

    public void handleKeyReleased (KeyEvent event) {
        switch (event.getCode()){
            case A:
                player.commandStopMoveLeft();
                break;
            case D:
                player.commandStopMoveRight();
                break;
            case S:
                player.commandStandUp();
                break;
            case ENTER:
                player.commandStopShooting();
                break;
        }

    }


}