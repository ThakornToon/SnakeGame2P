package SnakeGame2P.audio;

import SnakeGame2P.utils.Constants;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * คลาสจัดการเสียงทั้งหมดในเกม
 * โหลดไฟล์เสียงและควบคุมการเล่น
 *
 *   [ไฟล์เสียง .wav]
 *        ↓
 * 1. AudioInputStream (ข้อมูลเสียงดิบ + format)
 *        ↓
 * 2. Clip (จัดการในหน่วยความจำ)
 *        ↓
 * 3. Mixer (รวมสัญญาณ)
 *        ↓
 * 4. SourceDataLine (ส่งข้อมูล)
 *        ↓
 * 5. Sound Card (แปลงดิจิตอลเป็นอนาล็อก)
 *        ↓
 *      [ลำโพง]
 */
public class AudioManager {

    /** Map เก็บ Clip เสียงต่างๆ โดยใช้ชื่อไฟล์เป็น key */
    private final Map<String, Clip> sounds = new HashMap<>();

    /** สถานะปิดเสียง (true = ปิดเสียง) */
    private boolean muted = false;

    /**
     * Constructor: โหลดเสียงทั้งหมดเมื่อสร้าง object
     */
    public AudioManager() {
        loadSounds();
    }

    /**
     * โหลดไฟล์เสียงทั้งหมดจาก Constants
     * ใช้ protect เพื่อ Override ใน Class ใน Test ที่ extend ได้
     */
    protected void loadSounds() {
        loadSound(Constants.SOUND_MENU);
        loadSound(Constants.SOUND_BACKGROUND);
        loadSound(Constants.SOUND_GAME_OVER);
        loadSound(Constants.SOUND_EAT);
    }

    /**
     * โหลดไฟล์เสียงทีละไฟล์
     * path ที่อยู่ของไฟล์เสียง (ใน resources)
     */
    private void loadSound(String path) {
        try {
            URL soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.out.println("⚠️ ไม่พบไฟล์เสียง: " + path);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            sounds.put(path, clip);
            System.out.println("✅ โหลดเสียงสำเร็จ: " + path);

        } catch (UnsupportedAudioFileException e) {
            System.out.println("❌ ไม่รองรับไฟล์เสียง: " + path + " - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("❌ ไม่สามารถอ่านไฟล์: " + path + " - " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("❌ ไม่สามารถใช้งานเสียง: " + path + " - " + e.getMessage());
        }
    }

    /**
     * เล่นเสียงแบบสั้น (เช่น เสียงกินอาหาร)
     * path ที่อยู่ของไฟล์เสียง
     */
    public void playSound(String path) {
        if (muted) return;

        Clip clip = sounds.get(path);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * เล่นเพลงพื้นหลังแบบ loop
     * path ที่อยู่ของไฟล์เสียง
     */
    public void playBackgroundMusic(String path) {
        if (muted) return;

        stopAllMusic();

        Clip clip = sounds.get(path);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * หยุดเพลงทั้งหมดที่กำลังเล่นอยู่
     */
    public void stopAllMusic() {
        for (Clip clip : sounds.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    /**
     * สลับสถานะปิดเสียง
     */
    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopAllMusic();
        }
    }

    /**
     * ตรวจสอบสถานะปิดเสียง
     * return true ถ้าปิดเสียงอยู่
     */
    public boolean isMuted() {
        return muted;
    }
}