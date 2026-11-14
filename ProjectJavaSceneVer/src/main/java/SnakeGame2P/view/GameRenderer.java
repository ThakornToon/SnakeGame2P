package SnakeGame2P.view;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.view.renderer.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * คลาสสำหรับวาดกราฟิกทั้งหมดของเกม
 * ใช้ Strategy Pattern เพื่อเลือกรูปแบบการวาดตามสถานะ
 */
public class GameRenderer {
    /** Map เก็บ Renderer ตามสถานะเกม */
    private final Map<GameState, GameRendererStrategy> renderers = new EnumMap<>(GameState.class);

    /**
     * Constructor: สร้าง Renderer สำหรับแต่ละสถานะ
     */
    public GameRenderer(GamePanel panel) {
        renderers.put(GameState.MENU, new MenuRenderer(panel));
        renderers.put(GameState.PLAYING, new PlayingRenderer(panel));
        renderers.put(GameState.PAUSED, new PausedRenderer(panel));
        renderers.put(GameState.GAME_OVER, new GameOverRenderer(panel));
    }

    /**
     * วาดหน้าจอตามสถานะ
     * g Graphics object
     * state สถานะปัจจุบัน
     * snake1 งูผู้เล่น 1
     * snake2 งูผู้เล่น 2
     * food อาหาร
     * winnerText ข้อความผู้ชนะ (ใช้เฉพาะ GAME_OVER)
     */
    public void render(Graphics g, GameState state, Snake snake1, Snake snake2, Food food, String winnerText) {
        // จะเลือกทำ GameRendererStrategy ตาม state จาก map
        GameRendererStrategy renderer = renderers.get(state);
        if (renderer != null) {
            renderer.render(g, snake1, snake2, food, winnerText);
        } else {
            // กลับหน้าเมนูถ้า null
            renderers.get(GameState.MENU).render(g, null, null, null, null);
        }
    }
}