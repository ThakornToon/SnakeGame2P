package SnakeGame2P.model.entity;

import java.util.Random;

/**
 * Factory that creates a random food item each time it is called.
 * Centralises food-type probabilities so GameController never needs to know subclass names.
 *
 * Spawn probabilities:
 *   NormalFood  60% — grow by 1
 *   GhostFood   15% — grow + ghost mode 5 s
 *   ShieldFood  15% — grow + shield (absorbs 1 collision)
 *   FreezeFood  10% — grow + freeze opponent 3 s
 */
public class FoodFactory {
    private static final Random random = new Random();

    /** Returns a new food instance chosen randomly by the probabilities above. */
    public static Food createRandomFood() {
        int chance = random.nextInt(100);

        if (chance < 60) {
            return new NormalFood();
        } else if (chance < 75) {
            return new GhostFood();
        } else if (chance < 90) {
            return new ShieldFood();
        } else {
            return new FreezeFood();
        }
    }
}
