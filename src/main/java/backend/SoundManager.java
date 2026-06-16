package backend;

import javafx.scene.media.AudioClip;
import lombok.Getter;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    @Getter
    public static SoundManager instance;

    // Перелік усіх звуків у грі (camelCase за запитом)
    public enum SoundType { arShot, gunChange, noBullet, pistolShot,
        footstep1, footstep2, footstep3, footstep4, footstep5,
        death1, healing1,
        bigAgro, bigDeath, leapAgro, leapDeath, simpAgro, simpDeath,
    }

    private Map<SoundType, AudioClip> sounds = new HashMap<>();

    public SoundManager() {
        instance = this;

        loadSound(SoundType.arShot, "sounds/mainCharacter/weapon/ar_shot.mp3");
        loadSound(SoundType.gunChange, "sounds/mainCharacter/weapon/gun_change.wav");
        loadSound(SoundType.noBullet, "sounds/mainCharacter/weapon/no_bullet.wav");
        loadSound(SoundType.pistolShot, "sounds/mainCharacter/weapon/pistol_shot.mp3");

        loadSound(SoundType.footstep1, "sounds/mainCharacter/footsteps/footstep1.wav");
        loadSound(SoundType.footstep2, "sounds/mainCharacter/footsteps/footstep2.wav");
        loadSound(SoundType.footstep3, "sounds/mainCharacter/footsteps/footstep3.wav");
        loadSound(SoundType.footstep4, "sounds/mainCharacter/footsteps/footstep4.wav");
        loadSound(SoundType.footstep5, "sounds/mainCharacter/footsteps/footstep5.wav");

        loadSound(SoundType.death1, "sounds/mainCharacter/death1.mp3");
        loadSound(SoundType.healing1, "sounds/mainCharacter/healing1.wav");

        loadSound(SoundType.bigAgro, "sounds/monsters/big_agro.wav");
        loadSound(SoundType.bigDeath, "sounds/monsters/big_death.wav");
        loadSound(SoundType.leapAgro, "sounds/monsters/leap_agro.wav");
        loadSound(SoundType.leapDeath, "sounds/monsters/leap_death.wav");
        loadSound(SoundType.simpAgro, "sounds/monsters/simp_agro.wav");
        loadSound(SoundType.simpDeath, "sounds/monsters/simp_death.wav");
    }

    private void loadSound(SoundType type, String relativePath) {
        try {
            String uri = Paths.get(relativePath).toUri().toString();
            sounds.put(type, new AudioClip(uri));
        } catch (Exception e) {
            System.err.println("КРИТИЧНА ПОМИЛКА: Не вдалося завантажити звук: " + relativePath);
        }
    }

    // метод для відтворення
    public void playSound(SoundType type) {
        AudioClip clip = sounds.get(type);
        if (clip != null) {
            clip.play();
        } else {
            System.err.println("Спроба відтворити відсутній звук: " + type);
        }
    }
}