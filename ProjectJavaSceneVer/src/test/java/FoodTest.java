import SnakeGame2P.model.entity.Food;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Food
 * ทดสอบการทำงานของอาหาร รวมถึงการสุ่มตำแหน่งและการถูกกิน
 *
 *  เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบการสุ่มตำแหน่งอาหารอยู่ในขอบเขตที่กำหนด
 * 2. ตรวจสอบการตรวจจับการถูกงูกิน
 * 3. ตรวจสอบการซ้อนทับกับงู
 */
public class FoodTest {

    private Food food;
    private Snake snake1;
    private Snake snake2;

    @BeforeEach
    void setUp() {
        food = new Food();
        snake1 = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
        snake2 = new Snake(600, 200, Direction.LEFT,
                Color.ORANGE, Color.RED, Color.PINK);
    }

    @Test
    void testFoodCreation() {
        // ทดสอบ: การสร้างอาหารควรอยู่ในกริดที่ถูกต้อง
        // เหตุผล: อาหารต้องวางในตำแหน่งที่ตรงกับ grid เสมอ (เป็นทวีคูณของ UNIT_SIZE)
        assertTrue(food.getX() % Constants.UNIT_SIZE == 0,
                "Food X should be multiple of UNIT_SIZE");
        assertTrue(food.getY() % Constants.UNIT_SIZE == 0,
                "Food Y should be multiple of UNIT_SIZE");

        // ตรวจสอบว่าอยู่ในขอบเขต
        assertTrue(food.getX() >= 0 && food.getX() < Constants.BOARD_WIDTH,
                "Food X should be within board width");
        assertTrue(food.getY() >= 0 && food.getY() < Constants.BOARD_HEIGHT,
                "Food Y should be within board height");
    }

    @RepeatedTest(20)
    void testGenerateNewPosition() {
        // ทดสอบ: การสุ่มตำแหน่งใหม่ควรได้ค่าที่แตกต่าง
        // เหตุผล: ตรวจสอบว่าการสุ่มทำงานและได้ค่าที่อยู่ในกริด
        Food testFood = new Food();
        int firstX = testFood.getX();
        int firstY = testFood.getY();

        testFood.generateNewPosition();

        // อาจมีโอกาสสุ่มได้ค่าเดิม แต่ความน่าจะเป็นต่ำมาก
        // ถ้าสุ่มได้ค่าเดิม 20 ครั้งติด แสดงว่า random ทำงานผิดปกติ
        boolean positionChanged = (firstX != testFood.getX()) || (firstY != testFood.getY());
        assertTrue(positionChanged, "Generate new position should usually change position");
    }

    @Test
    void testIsEatenBy_HeadCollision() {
        // ทดสอบ: ตรวจสอบการถูกกินเมื่อหัวงูชนอาหาร
        // เหตุผล: ต้องตรวจจับเมื่องูกินอาหารได้ถูกต้อง

        // ย้ายอาหารไปที่หัวงู
        Point headPos = snake1.getHead();
        food.setPosition(headPos.x, headPos.y);

        assertTrue(food.isEatenBy(snake1),
                "Food should be eaten when snake head is at food position");
        assertFalse(food.isEatenBy(snake2),
                "Food should not be eaten by other snake");
    }

    @Test
    void testIsEatenBy_BodyCollision() {
        // ทดสอบ: อาหารไม่ควรถูกกินเมื่อชนกับลำตัว
        // เหตุผล: งูต้องกินด้วยหัวเท่านั้น

        // ย้ายอาหารไปที่ตำแหน่งลำตัว (ส่วนที่ 1)
        Point bodyPos = new Point(
                snake1.getXPositions().get(1),
                snake1.getYPositions().get(1)
        );
        food.setPosition(bodyPos.x, bodyPos.y);

        assertFalse(food.isEatenBy(snake1),
                "Food should not be eaten when colliding with body");
    }

    @Test
    void testOnConsumed_Effect() {
        // ทดสอบ: เมื่อถูกกินควรทำให้งูยาวขึ้น
        // เหตุผล: ตรวจสอบผลลัพธ์ของการ implement Consumable interface

        int oldLength = snake1.getLength();
        int oldFoodEaten = snake1.getFoodEaten();

        food.onConsumed(snake1);

        assertEquals(oldLength + 1, snake1.getLength(),
                "Snake should grow after consuming food");
        assertEquals(oldFoodEaten + 1, snake1.getFoodEaten(),
                "Food eaten count should increase");
    }

    @Test
    void testIsOccupiedBy_WithBothSnakes() {
        // ทดสอบ: ตรวจสอบว่าอาหารซ้อนทับกับงูหรือไม่
        // เหตุผล: ป้องกันการเกิดอาหารบนตัวงู

        // กรณีไม่ซ้อนทับ
        food.setPosition(0, 0);
        assertFalse(food.isOccupiedBy(snake1, snake2),
                "Food should not be occupied when at empty position");

        // กรณีซ้อนทับกับงูตัวที่ 1
        Point headPos = snake1.getHead();
        food.setPosition(headPos.x, headPos.y);
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake1 head");

        // กรณีซ้อนทับกับงูตัวที่ 2
        headPos = snake2.getHead();
        food.setPosition(headPos.x, headPos.y);
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake2 head");

        // กรณีซ้อนทับกับลำตัว
        Point bodyPos = new Point(
                snake1.getXPositions().get(1),
                snake1.getYPositions().get(1)
        );
        food.setPosition(bodyPos.x, bodyPos.y);
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake body");
    }
}
