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
    // труба або інше джерело вогня
    private Image sourceOfFire = new Image("file:assets/detectors/torch8.png");

    // спрати для початкової частини вогня, коли він розгортається
    private Image spreadimgFireImgs[] = { new Image("file:assets/detectors/torch1.png"),
            new Image("file:assets/detectors/torch2.png"),
            new Image("file:assets/detectors/torch3.png"),
    };

    // спрайти для основної частини анімації, які будуть прокручуватись кілька разів
    private Image mainFireImgs[] = { new Image("file:assets/detectors/torch4.png"),
            new Image("file:assets/detectors/torch5.png"),
            new Image("file:assets/detectors/torch6.png"),   //?
    };
    // -----------------

    private int currentSpriteIndex;

    private double currentSpriteTime;
    private double sumTime; // для перемикання частин

    private double spreadingTime = 1; // час початкової частини анімації
    private double spreadingPeriod;

    private double mainTime; // час основної частини анімації + spreadingTime
    private double mainPeriod;

    private double breakTime; // час перерви (вогонь не б'є) + mainAnimTime + spreadingTime

    private boolean isMainPhase = false;

    private Image stubImage; // зберігач заглушки

    public Trap2 (int x, int y, double fireTime, double breakTime) {
        super(x, y);
        zIndex = 6;

        width = 30;
        height = 200;

        havePeriodicActoin = true;
        timePeriod = 1;

        mainTime = fireTime;
        this.breakTime = fireTime + breakTime;

        spreadingPeriod = spreadingTime / spreadimgFireImgs.length;
        mainPeriod = mainTime / mainFireImgs.length;


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
        this.image = canvas.snapshot(params, null);
        stubImage = image;
        // ----------------
    }

    @Override
    protected void doPeriodicAction() {
        if(sumTime < mainTime){
            for (GameEntity obj : entitiesInside){
                if(obj instanceof Player){
                    Player.getInstance().takeDamage(10);
                }
                if(obj instanceof Monster){
                    ((Monster) obj).takeDamage(10);
                }
            }
        }
    }

    @Override
    public void update(double deltaTime) {

        super.update(deltaTime);

        currentSpriteTime += deltaTime;
        sumTime += deltaTime;

        if (sumTime <= spreadingTime) {
            // ЗАГЛУШКА
            image = stubImage;

            isMainPhase = false;
            if (currentSpriteTime >= spreadingPeriod) {
                currentSpriteTime -= spreadingPeriod;
                currentImage = spreadimgFireImgs[currentSpriteIndex];
                currentSpriteIndex = (currentSpriteIndex + 1) % spreadimgFireImgs.length;
            }
        } else if (sumTime <= mainTime) {
            if (!isMainPhase) {
                isMainPhase = true;
                currentSpriteIndex = 0;
            }
            if (currentSpriteTime >= mainPeriod) {
                currentSpriteTime -= mainPeriod;
                currentImage = mainFireImgs[currentSpriteIndex];
                currentSpriteIndex = (currentSpriteIndex + 1) % mainFireImgs.length;
            }
        } else if (sumTime <= breakTime) {
            if(isMainPhase) {
                currentImage = null;
                isMainPhase = false;
                // ЗАГЛУШКА
                image = null;

            }
        } else {
            sumTime = 0;
            currentSpriteIndex = 0;
        }
    }

    @Override
    public void initialTargetList() {
        targetObjects = Level.getCurrentLevel().getLivingEntitties();
    }
}