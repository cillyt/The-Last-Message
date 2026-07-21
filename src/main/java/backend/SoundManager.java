package backend;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    @Getter
    public static SoundManager instance;

    // Перелік усіх звуків у грі (camelCase за запитом)
    public enum SoundType { arShot, gunChange, noBullet, pistolShot,
        footsteps, jumpSound,
        death1, healing1,
        bigAgro, bigDeath, bigAttack,
        leapAgro, leapDeath, leapAttack,
        simpAgro, simpDeath, simpAttack,
        nextLevel, deathScreen, buttonClick
    }

    public enum MusicType {
        mainMenu, gameplay
    }

    private Map<SoundType, AudioClip> sounds = new HashMap<>();

    private Map<MusicType, String> musicPaths = new HashMap<>();
    private MediaPlayer currentMusicPlayer;
    private MusicType currentPlayingMusic;


    public SoundManager() {
        instance = this;

        loadSound(SoundType.arShot, "assets/sounds/mainCharacter/weapon/ar_shot.mp3");
        loadSound(SoundType.gunChange, "assets/sounds/mainCharacter/weapon/gun_change.wav");
        loadSound(SoundType.noBullet, "assets/sounds/mainCharacter/weapon/no_bullet.wav");
        loadSound(SoundType.pistolShot, "assets/sounds/mainCharacter/weapon/pistol_shot.mp3");

        loadSound(SoundType.footsteps, "assets/sounds/mainCharacter/footsteps.mp3");
        loadSound(SoundType.jumpSound, "assets/sounds/mainCharacter/jump_sound.mp3");

        loadSound(SoundType.death1, "assets/sounds/mainCharacter/death1.mp3");
        loadSound(SoundType.healing1, "assets/sounds/mainCharacter/healing1.wav");

        loadSound(SoundType.bigAgro, "assets/sounds/monsters/big_agro.wav");
        loadSound(SoundType.bigDeath, "assets/sounds/monsters/big_death.wav");
        loadSound(SoundType.bigAttack, "assets/sounds/monsters/big_attack1.mp3");

        loadSound(SoundType.leapAgro, "assets/sounds/monsters/leap_agro.wav");
        loadSound(SoundType.leapDeath, "assets/sounds/monsters/leap_death.wav");
        loadSound(SoundType.leapAttack, "assets/sounds/monsters/leap_attack.mp3");

        loadSound(SoundType.simpAgro, "assets/sounds/monsters/simp_agro.wav");
        loadSound(SoundType.simpDeath, "assets/sounds/monsters/simp_death.wav");
        loadSound(SoundType.simpAttack, "assets/sounds/monsters/simp_attack.mp3");

        loadSound(SoundType.nextLevel, "assets/sounds/other/nextLevel1.mp3");
        loadSound(SoundType.deathScreen, "assets/sounds/other/deathScreen.wav");
        loadSound(SoundType.buttonClick, "assets/sounds/other/buttons/buttonClick1.wav");

        musicPaths.put(MusicType.mainMenu, "assets/sounds/other/menu/menuTheme2.mp3");
        musicPaths.put(MusicType.gameplay, "assets/sounds/other/menu/menuTheme4.mp3");
    }

    private void loadSound(SoundType type, String relativePath) {
        String resourcePath = "/" + relativePath;
        URL resourceUrl = SoundManager.class.getResource(resourcePath);
            if (resourceUrl == null) {
                System.out.println("КРИТИЧНА ПОМИЛКА: Не вдалося завантажити звук: " + resourcePath);
            }
            sounds.put(type, new AudioClip(resourceUrl.toExternalForm()));
    }

    /**
       Програвання звуку з накладанням.
    */
    public void play(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            clip.play();
        } else {
            System.err.println("Спроба відтворити відсутній звук: " + type);
        }
    }

    /**
     Програвання звуку, лише якщо він не відтворюється.
     */
    public void playOnce(SoundType type){
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            if (clip.isPlaying()) return;
            clip.play();
        } else {
            System.err.println("Спроба відтворити відсутній звук: " + type);
        }
    }

    /**
     Зациклене програвання.
     */
    public void playLoop(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            if (clip.isPlaying()) return;
            clip.setCycleCount(AudioClip.INDEFINITE);
            clip.play();
        }
    }

    /**
     Зупиняє і відтворює знову.
     */
    public void stopAndPlay(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            if(clip.isPlaying()) clip.stop();
            clip.play();
        }
    }

    /**
     Зупинка програвання.
     */
    public void stop(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null && clip.isPlaying()) {
            clip.stop();
        }
    }

    public void playMusic(MusicType type) {
        if (currentPlayingMusic == type && currentMusicPlayer != null && currentMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        stopMusic();

        try {
            String relativePath = musicPaths.get(type);
            if (relativePath != null) {
                String resourcePath = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
                java.net.URL resourceUrl = getClass().getResource(resourcePath);

                if (resourceUrl != null) {
                    Media media = new Media(resourceUrl.toExternalForm());
                    currentMusicPlayer = new MediaPlayer(media);
                    currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    currentMusicPlayer.play();
                    currentPlayingMusic = type;
                } else {
                    System.err.println("Файл музики не знайдено в ресурсах: " + resourcePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Помилка відтворення музики: " + type);
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer.dispose(); // Звільняємо ресурси
            currentMusicPlayer = null;
            currentPlayingMusic = null;
        }
    }

    /**
     Зупинка програвання всіх звуків.
     */
    public void stopAll() {
        for (AudioClip clip : sounds.values()) {
            if (clip != null && clip.isPlaying()) {
                clip.stop();
            }
        }
        stopMusic();
    }
}