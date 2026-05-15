package SnakeGame2P.audio;

import SnakeGame2P.utils.Constants;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX Audio Manager
 * จัดการเสียงทั้งหมดในเกม (SFX + BGM)
 * @author Thakorn-
 */
public class AudioManager {

    // ========== Sound Effects ==========
    private final Map<String, AudioClip> sounds = new HashMap<>();

    // ========== Background Music ==========
    private MediaPlayer bgmPlayer;
    private String currentMusicPath = null;

    // ========== State ==========
    private boolean muted = false;

    /**
     * Constructor: โหลดเสียงทั้งหมด
     */
    public AudioManager() {
        loadSounds();
    }

    /**
     * โหลดเสียงทั้งหมดจาก Constants
     * Polymorphism: Method Overloading
     */
    protected void loadSounds() {
        loadSound(Constants.SOUND_MENU);
        loadSound(Constants.SOUND_BACKGROUND);
        loadSound(Constants.SOUND_GAME_OVER);
        loadSound(Constants.SOUND_EAT);
    }

    /**
     * โหลด Sound Effect (สั้นๆ)
     * Polymorphism: Method Overloading
     * @param path placeholder ของชื่อไฟล์เสียง
     */
    public void loadSound(String path) {
        try {
            AudioClip clip = new AudioClip(
                    getClass().getResource(path).toExternalForm()
            );

            sounds.put(path, clip);
            System.out.println("✅ โหลดเสียงสำเร็จ: " + path);

        } catch (Exception e) {
            System.out.println("❌ โหลดเสียงไม่สำเร็จ: " + path);
            e.printStackTrace();
        }
    }

    /**
     * เล่น Sound Effect
     * @param path placeholder ของชื่อไฟล์เสียง
     */
    public void playSound(String path) {
        if (muted) return;

        AudioClip clip = sounds.get(path);
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * เล่น Background Music (Loop)
     * @param path placeholder ของชื่อไฟล์เสียง
     */
    public void playBackgroundMusic(String path) {
        currentMusicPath = path;

        if (muted) return;

        stopAllMusic();

        try {
            Media media = new Media(
                    getClass().getResource(path).toExternalForm()
            );

            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop
            bgmPlayer.setVolume(0.5); // optional

            bgmPlayer.play();

        } catch (Exception e) {
            System.out.println("❌ เล่นเพลงไม่สำเร็จ: " + path);
            e.printStackTrace();
        }
    }

    /**
     * หยุดเพลงทั้งหมด
     *
     */
    public void stopAllMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose(); // สำคัญ! ป้องกัน memory leak
            bgmPlayer = null;
        }
    }

    /**
     * Toggle mute
     */
    public void toggleMute() {
        muted = !muted;

        if (muted) {
            stopAllMusic();
        }
    }

    /**
     * ตรวจสอบสถานะ mute
     */
    public boolean isMuted() {
        return muted;
    }
}