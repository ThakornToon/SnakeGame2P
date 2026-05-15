package SnakeGame2P.view;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.view.renderer.*;

import javafx.scene.canvas.GraphicsContext;
import java.util.EnumMap;
import java.util.Map;

/**
 * คลาสสำหรับวาดกราฟิกทั้งหมดของเกม (JavaFX version)
 * ใช้ Strategy Pattern เพื่อเลือกรูปแบบการวาดตามสถานะ
 * @author Thakorn
 */
public class GameRenderer {

    /** Map เก็บ Renderer ตามสถานะเกม */
    private final Map<GameState, GameRendererStrategy> renderers = new EnumMap<>(GameState.class);

    /**
     * Constructor: สร้าง Renderer สำหรับแต่ละสถานะ
     */
    public GameRenderer() {
        renderers.put(GameState.MENU, new MenuRenderer());
        renderers.put(GameState.PLAYING, new PlayingRenderer());
        renderers.put(GameState.PAUSED, new PausedRenderer());
        renderers.put(GameState.GAME_OVER, new GameOverRenderer());
    }

    /**
     * วาดหน้าจอตามสถานะ
     *
     * @param gc GraphicsContext ของ Canvas
     * @param state สถานะปัจจุบัน
     * @param snake1 งูผู้เล่น 1
     * @param snake2 งูผู้เล่น 2
     * @param food อาหาร
     * @param winnerText ข้อความผู้ชนะ (ใช้เฉพาะ GAME_OVER)
     */
    public void render(GraphicsContext gc,
                       GameState state,
                       Snake snake1,
                       Snake snake2,
                       Food food,
                       String winnerText) {

        GameRendererStrategy renderer = renderers.get(state);

        if (renderer != null) {
            renderer.render(gc, snake1, snake2, food, winnerText);
        } else {
            // กลับหน้าเมนูถ้า null
            renderers.get(GameState.MENU).render(gc, null, null, null, null);
        }
    }
}