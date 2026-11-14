import SnakeGame2P.model.entity.Food;
import SnakeGame2P.model.entity.NormalFood;
import SnakeGame2P.model.entity.GhostFood;
import SnakeGame2P.model.entity.ShieldFood;
import SnakeGame2P.model.entity.FreezeFood;
import SnakeGame2P.model.entity.FoodFactory;
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
 * ทดสอบการทำงานของอาหาร รวมถึงการสุ่มตำแหน่ง การถูกกิน และบัฟพิเศษของอาหารแต่ละชนิด
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบการสุ่มตำแหน่งอาหารอยู่ในขอบเขตที่กำหนด
 * 2. ตรวจสอบการตรวจจับการถูกงูกิน
 * 3. ตรวจสอบการซ้อนทับกับงู
 * 4. ตรวจสอบบัฟพิเศษของอาหารแต่ละชนิด (Ghost, Shield, Freeze)
 * 5. ตรวจสอบระบบนับเวลาหมดอายุของอาหาร และการสุ่มสร้างอาหารจาก FoodFactory
 */
public class FoodTest {

    private Food food;
    private Snake snake1;
    private Snake snake2;

    @BeforeEach
    void setUp() {
        food = new NormalFood();
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
        Food testFood = new NormalFood();
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

        food.onConsumed(snake1, snake2);

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

    @Test
    void testGhostFood_OnConsumed() {
        // ทดสอบ: เมื่อกิน GhostFood งูต้องยาวขึ้นและได้รับสถานะล่องหนทะลุตัวคู่ต่อสู้
        // เหตุผล: ตรวจสอบการทำงานของ GhostFood.onConsumed() ตามกติกาโหมดล่องหน
        GhostFood ghostFood = new GhostFood();
        int initialLength = snake1.getLength();

        assertFalse(snake1.isGhostModeActive(), "Snake should not be in ghost mode initially");
        ghostFood.onConsumed(snake1, snake2);

        assertEquals(initialLength + 1, snake1.getLength(), "Snake should grow by 1");
        assertTrue(snake1.isGhostModeActive(), "Snake should activate ghost mode after eating GhostFood");
        assertTrue(snake1.getGhostModeRemainingTime() > 0, "Remaining ghost mode time should be > 0");
    }

    @Test
    void testShieldFood_OnConsumed() {
        // ทดสอบ: เมื่อกิน ShieldFood งูต้องยาวขึ้นและได้รับเกราะป้องกันการชน 1 ครั้ง
        // เหตุผล: ตรวจสอบการทำงานของ ShieldFood.onConsumed() และการเปิดใช้ shieldActive
        ShieldFood shieldFood = new ShieldFood();
        int initialLength = snake1.getLength();

        assertFalse(snake1.isShieldActive(), "Snake should not have shield initially");
        shieldFood.onConsumed(snake1, snake2);

        assertEquals(initialLength + 1, snake1.getLength(), "Snake should grow by 1");
        assertTrue(snake1.isShieldActive(), "Snake should activate shield after eating ShieldFood");
    }

    @Test
    void testFreezeFood_OnConsumed() {
        // ทดสอบ: เมื่อกิน FreezeFood งูผู้กินยาวขึ้น และงูคู่แข่งต้องถูกแช่แข็งเป็นเวลา 3 วินาที
        // เหตุผล: ตรวจสอบว่าเอฟเฟกต์แช่แข็งส่งผลต่องูฝ่ายตรงข้ามเท่านั้น และไม่กระทบงูผู้กิน
        FreezeFood freezeFood = new FreezeFood();
        int initialLength = snake1.getLength();

        assertFalse(snake2.isFrozen(), "Opponent should not be frozen initially");
        assertFalse(snake1.isFrozen(), "Eater should not be frozen initially");

        freezeFood.onConsumed(snake1, snake2);

        assertEquals(initialLength + 1, snake1.getLength(), "Eater should grow by 1");
        assertFalse(snake1.isFrozen(), "Eater should NOT be frozen");
        assertTrue(snake2.isFrozen(), "Opponent should be frozen after eater eats FreezeFood");
        assertTrue(snake2.getFreezeRemainingTime() > 0, "Freeze remaining time should be > 0");
    }

    @Test
    void testFoodExpiry_InitialNotExpired() {
        // ทดสอบ: อาหารที่เพิ่งเกิดใหม่ต้องยังไม่หมดอายุ และมีอัตราส่วนอายุที่ถูกต้อง
        // เหตุผล: ตรวจสอบระบบนับเวลาหมดอายุของอาหาร (Food Expiry 8 วินาที)
        NormalFood newFood = new NormalFood();
        assertFalse(newFood.isExpired(), "Newly spawned food should not be expired immediately");
        assertTrue(newFood.getLifeRatio() > 0.0 && newFood.getLifeRatio() <= 1.0,
                "Life ratio of fresh food should be between 0 and 1");
    }

    @Test
    void testFoodFactory_CreateRandomFood() {
        // ทดสอบ: FoodFactory ต้องสุ่มสร้างอาหารที่ไม่เป็น null และอยู่ภายในขอบเขตบอร์ดเกม
        // เหตุผล: ตรวจสอบการทำงานของ Factory Pattern ในการสร้าง Food subclasses
        for (int i = 0; i < 30; i++) {
            Food randomFood = FoodFactory.createRandomFood();
            assertNotNull(randomFood, "FoodFactory should never return null");
            assertTrue(randomFood.getX() >= 0 && randomFood.getX() < Constants.BOARD_WIDTH,
                    "Generated food X should be within board bounds");
            assertTrue(randomFood.getY() >= 0 && randomFood.getY() < Constants.BOARD_HEIGHT,
                    "Generated food Y should be within board bounds");
            assertTrue(randomFood instanceof NormalFood ||
                       randomFood instanceof GhostFood ||
                       randomFood instanceof ShieldFood ||
                       randomFood instanceof FreezeFood,
                    "Generated food should be one of the known Food subclasses");
        }
    }
}
