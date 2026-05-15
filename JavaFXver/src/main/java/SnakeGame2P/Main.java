package SnakeGame2P;

import SnakeGame2P.controller.GameController;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * main class
 * @author Thakorn and Unnawas
 */
public class Main extends Application {

    private AudioManager audioManager;

    // ฟอนต์ Angsana New ที่จะใช้ทั่วทั้งโปรแกรม
    public static Font thaiFont;
    public static Font thaiFontBold;
    public static Font thaiFontLarge;
    public static Font thaiFontMedium;
    public static Font thaiFontSmall;

    /**
     * JavaFX Start
     * @param primaryStage JavaFX primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        // โหลดฟอนต์ Angsana New
        loadFonts();

        // Initialize AudioManager
        audioManager = new AudioManager();

        // Create GamePanelFX (JavaFX version of your panel)
        GamePanel gamePanel = new GamePanel(audioManager);

        // Create scene with the preferred size
        Scene scene = new Scene(gamePanel, gamePanel.getPrefWidth(), gamePanel.getPrefHeight());

        // สร้าง GameController และส่ง panel กับ scene ให้ควบคุม
        GameController controller = new GameController(gamePanel, scene, audioManager);

        // Configure the stage
        primaryStage.setTitle(Constants.GAME_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // ขอ focus ให้ panel รับ keyboard input
        gamePanel.requestFocus();
    }

    /**
     * โหลดฟอนต์ Angsana New (ใช้ฟอนต์ในระบบ Windows/Mac).
     * if font cannot be loaded, try the next font in fontNames
     */
    private static void loadFonts() {
        try {
            // ลองใช้ Angsana New หลายรูปแบบ
            String[] fontNames = {
                    "Angsana New",
                    "AngsanaUPC",
                    "Tahoma",
                    "Microsoft Sans Serif"
            };

            Font loadedFont = null;
            for (String fontName : fontNames) {
                try {
                    Font font = Font.font(fontName);
                    // ตรวจสอบว่าโหลดได้จริงและไม่ใช่ฟอนต์ System default
                    if (font != null && font.getFamily() != null && !font.getFamily().equals("System")) {
                        loadedFont = font;
                        System.out.println("✅ โหลดฟอนต์สำเร็จ: " + font.getFamily());
                        break;
                    }
                } catch (Exception e) {
                    // ข้ามฟอนต์ที่ใช้ไม่ได้
                }
            }

            if (loadedFont != null) {
                String fontFamily = loadedFont.getFamily();
                // สร้างฟอนต์ขนาดต่างๆ ด้วย FontWeight
                thaiFont = Font.font(fontFamily, FontWeight.NORMAL, 20);
                thaiFontBold = Font.font(fontFamily, FontWeight.BOLD, 20);
                thaiFontLarge = Font.font(fontFamily, FontWeight.BOLD, 60);
                thaiFontMedium = Font.font(fontFamily, FontWeight.BOLD, 30);
                thaiFontSmall = Font.font(fontFamily, FontWeight.BOLD, 12);

                System.out.println("✅ ตั้งค่าฟอนต์สำเร็จ: " + fontFamily);
            }
        } catch (Exception e) {
            // Fallback สุดท้าย - ใช้ Arial
            System.out.println("❌ โหลดฟอนต์ไม่สำเร็จ ใช้ Arial แทน: " + e.getMessage());
            thaiFont = Font.font("Arial", FontWeight.NORMAL, 20);
            thaiFontBold = Font.font("Arial", FontWeight.BOLD, 20);
            thaiFontLarge = Font.font("Arial", FontWeight.BOLD, 60);
            thaiFontMedium = Font.font("Arial", FontWeight.BOLD, 30);
            thaiFontSmall = Font.font("Arial", FontWeight.BOLD, 12);
        }
    }

    /**
     * Pseudo ENUM for handling font weight
     */
    private static class FontWeight {
        static javafx.scene.text.FontWeight BOLD = javafx.scene.text.FontWeight.BOLD;
        static javafx.scene.text.FontWeight NORMAL = javafx.scene.text.FontWeight.NORMAL;
    }

    /**
     * Launch JavaFX
     * @param args default
     */
    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}