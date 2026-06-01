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
                player.jump();
                break;
            case A:
                player.facingRight = false;
                player.go();
                break;
            case S:
                player.crouch();
                break;
            case D:
                player.facingRight = true;
                player.go();
                break;
            case ENTER:
                // запуск стрільби

            case DIGIT1:
                player.takePistol();
                break;
            case DIGIT2:
                player.takeAR();
                break;
        }
    }

    public void handleKeyReleased (KeyEvent event) {
        switch (event.getCode()){
            case A:
                player.stop();
                break;
            case D:
                player.stop();
                break;
            case S:
                player.stop();
                break;
        }

    }


}