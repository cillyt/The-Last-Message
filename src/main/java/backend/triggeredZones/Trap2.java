/*
  Труба, з якої виходить вогонь площею 30х200
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Level;
import backend.Player;
import backend.monsters.Monster;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Trap2 extends Detector {

    // ----- АСЕТИ -----
    // спрайт без вогню для стану паузи
    private Image breakImg = new Image(getClass().getResourceAsStream("/assets/detectors/torch8.png"));

    // спрайти для анімації, які будуть прокручуватись кілька разів
    private Image fireImgs[] = {
            new Image(getClass().getResourceAsStream("/assets/detectors/torch1.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch2.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch3.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch4.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch5.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch6.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch7.png")),
            new Image(getClass().getResourceAsStream("/assets/detectors/torch8.png")),
    };
    // -----------------

    private int currentSpriteIndex;

    private double currentSpriteTime;
    private double sumTime; // для перемикання частин

    private double fireTime; // час анімації
    private double firePeriod = 0.05;

    private double breakTime; // час перерви (вогонь не б'є) + fireTime

    private Image stubImage; // зберігач заглушки
    private boolean firePhase;

    public Trap2 (int x, int y, double fireTime, double breakTime) {
        super(x, y);
        zIndex = 6;

        width = 60;
        height = 180;

        calcImgMarg(0.6, breakImg);

        havePeriodicActoin = true;
        timePeriod = 0.5;

        this.fireTime = fireTime;
        this.breakTime = fireTime + breakTime;

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#8B0000"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.BLACK);
        tempGc.setFont(new Font("Arial", 10));
        tempGc.fillText("TRAP2", 2, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        //this.image = canvas.snapshot(params, null);
        stubImage = image;
        // ----------------
    }

    @Override
    protected void doPeriodicAction() {
        if(sumTime < fireTime){
            for (GameEntity obj : entitiesInside){
                if(obj instanceof Player){
                    Player.getInstance().takeDamage(8);
                }
                if(obj instanceof Monster){
                    ((Monster) obj).takeDamage(8);
                }
            }
        }
    }

    @Override
    public void update(double deltaTime) {

        super.update(deltaTime);

        currentSpriteTime += deltaTime;
        sumTime += deltaTime;

        if (sumTime <= fireTime) {
            if(!firePhase) firePhase = true;
            // ЗАГЛУШКА
            //image = stubImage;
            if (currentSpriteTime >= firePeriod) {
                currentSpriteTime -= firePeriod;
                currentImage = fireImgs[currentSpriteIndex];
                currentSpriteIndex = (currentSpriteIndex + 1) % fireImgs.length;
            }
        } else if (sumTime <= breakTime) {
            if(firePhase) {
                currentImage = breakImg;
                firePhase = false;
                // ЗАГЛУШКА
                //image = null;
            }
        } else {
            sumTime = 0;
            currentSpriteTime = 0;
            currentSpriteIndex = 0;
        }
    }

    @Override
    public void initialTargetList() {
        targetObjects = Level.getCurrentLevel().getLivingEntitties();
    }
}