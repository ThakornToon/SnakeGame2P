package SnakeGame2P;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.controller.GameController;
import SnakeGame2P.audio.AudioManager;
import javax.swing.*;

/**
 * คลาสหลักสำหรับเริ่มเกม
 * สร้าง JFrame และเชื่อมต่อส่วนต่างๆ เข้าด้วยกัน
 */
public class Main {

    /**
     * จุดเริ่มต้นของโปรแกรม
     * args arguments จาก command line (ไม่ได้ใช้)
     */
    public static void main(String[] args) {
        // สร้างหน้าต่างหลัก
        JFrame frame = new JFrame(Constants.GAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); // ห้ามปรับขนาด
        frame.setLocationRelativeTo(null); // วางตรงกลางจอ

        // สร้าง components
        AudioManager audioManager = new AudioManager();
        GamePanel gamePanel = new GamePanel(audioManager);
        GameController controller = new GameController(gamePanel, audioManager); // // ส่ง panel ให้ controller ควบคุม

        // เพิ่ม panel ลงใน frame
        frame.add(gamePanel);
        frame.pack(); // ปรับขนาด frame ให้พอดีกับ panel

        // แสดงหน้าต่าง
        frame.setVisible(true);

        // ขอ focus ให้ panel รับ keyboard input
        gamePanel.requestFocusInWindow();
    }
}