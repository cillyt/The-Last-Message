package backend;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;

import java.nio.file.Paths;
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

        loadSound(SoundType.arShot, "sounds/mainCharacter/weapon/ar_shot.mp3");
        loadSound(SoundType.gunChange, "sounds/mainCharacter/weapon/gun_change.wav");
        loadSound(SoundType.noBullet, "sounds/mainCharacter/weapon/no_bullet.wav");
        loadSound(SoundType.pistolShot, "sounds/mainCharacter/weapon/pistol_shot.mp3");

        loadSound(SoundType.footsteps, "sounds/mainCharacter/footsteps.mp3");
        loadSound(SoundType.jumpSound, "sounds/mainCharacter/jump_sound.mp3");

        loadSound(SoundType.death1, "sounds/mainCharacter/death1.mp3");
        loadSound(SoundType.healing1, "sounds/mainCharacter/healing1.wav");

        loadSound(SoundType.bigAgro, "sounds/monsters/big_agro.wav");
        loadSound(SoundType.bigDeath, "sounds/monsters/big_death.wav");
        loadSound(SoundType.bigAttack, "sounds/monsters/big_attack1.mp3");

        loadSound(SoundType.leapAgro, "sounds/monsters/leap_agro.wav");
        loadSound(SoundType.leapDeath, "sounds/monsters/leap_death.wav");
        loadSound(SoundType.leapAttack, "sounds/monsters/leap_attack.mp3");

        loadSound(SoundType.simpAgro, "sounds/monsters/simp_agro.wav");
        loadSound(SoundType.simpDeath, "sounds/monsters/simp_death.wav");
        loadSound(SoundType.simpAttack, "sounds/monsters/simp_attack.mp3");

        loadSound(SoundType.nextLevel, "sounds/other/nextLevel1.mp3");
        loadSound(SoundType.deathScreen, "sounds/other/deathScreen.wav");
        loadSound(SoundType.buttonClick, "sounds/other/buttons/buttonClick1.wav");

        musicPaths.put(MusicType.mainMenu, "sounds/other/menu/menuTheme2.mp3");
        musicPaths.put(MusicType.gameplay, "sounds/other/menu/menuTheme4.mp3");
    }

    private void loadSound(SoundType type, String relativePath) {
        try {
            String uri = Paths.get(relativePath).toUri().toString();
            sounds.put(type, new AudioClip(uri));
        } catch (Exception e) {
            System.err.println("КРИТИЧНА ПОМИЛКА: Не вдалося завантажити звук: " + relativePath);
        }
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
                String uri = Paths.get(relativePath).toUri().toString();
                Media media = new Media(uri);
                currentMusicPlayer = new MediaPlayer(media);
                currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                currentMusicPlayer.play();
                currentPlayingMusic = type;
            }
        } catch (Exception e) {
            System.err.println("Помилка відтворення музики: " + type);
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