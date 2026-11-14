# Snake Game 2P — เกมงู 2 ผู้เล่น

A feature-packed, local two-player Snake game built with **Java Swing**, featuring special food items, speed-boost mechanics, bullet combat, combo scoring, a countdown match timer with fast-phase sudden death, and immersive audio.

---

## Features / ฟีเจอร์

| Feature | Description |
|---------|-------------|
| **2-Player Local Play** | Both players battle on the same keyboard in real-time |
| **Speed Boost** | Hold key to double your speed (snake shrinks 1 segment every 0.5 s; min length 3) |
| **Bullet System** | Fire high-speed projectiles from head (damages opponent score & length, destroys food) |
| **Special Food Items** | Normal (🔴), Ghost (🟣), Shield (🟡), and Freeze (🔵) power-ups |
| **Combo Scoring** | Eat again within 3 s to chain combos up to ×5 for exponential bonus points |
| **Food Expiry** | Uneaten food pulses warning visuals and auto-respawns after 8 s |
| **Fast Phase** | In the final 60 s, game tick rate doubles for a high-intensity showdown |
| **Match Timer & Sudden Death**| 180-second timed match with comprehensive tiebreaker rules |
| **Clean Toast Notifications**| Toggle audio with `P` anytime (menu/pause); temporary bottom-right toast appears for 1.5 s without persistent screen clutter |
| **Animated Graphics & HUD**| Rounded snakes with directional eyes, orbiting/pulsing items, and state badges |
| **Dynamic Audio Engine** | WAV sound effects and looping background music with mute controls |

---

## Controls / การควบคุม

### Player 1 — Green Snake (งูเขียว)

| Key | Action |
|-----|--------|
| `W` `A` `S` `D` | Move up / left / down / right |
| Hold `Shift` | Speed boost (2× speed; snake shrinks 1 segment every 0.5 s, requires length > 3) |
| `Space` | Shoot bullet (costs 3 length units; requires length ≥ 6; 0.5 s cooldown) |

### Player 2 — Orange Snake (งูส้ม)

| Key | Action |
|-----|--------|
| `↑` `←` `↓` `→` | Move up / left / down / right |
| Hold `M` | Speed boost (2× speed; snake shrinks 1 segment every 0.5 s, requires length > 3) |
| `/` (Slash) | Shoot bullet (costs 3 length units; requires length ≥ 6; 0.5 s cooldown) |

### System & Menu / ทั่วไป

| Key | Action |
|-----|--------|
| `Esc` | Pause / Resume game |
| `R` | Return to main menu (while paused) |
| `P` | Toggle mute (Menu / Paused / Game Over screen) with toast notification |

---

## Food Items & Power-ups / ประเภทอาหาร

Each food type is animated (pulsing glow, inner shine, unique effect symbols) and blinks rapidly when under 25% lifetime remaining.

| Color | Type | Spawn % | Effect |
|-------|------|---------|--------|
| 🔴 **Red** | Normal | 60% | Grow +1 segment, +1 score |
| 🟣 **Purple** | Ghost | 15% | Grow +1 · Phase through opponent for 5 s (45% opacity) |
| 🟡 **Gold** | Shield | 15% | Grow +1 · Absorb next lethal collision (pulsing golden aura) |
| 🔵 **Ice Blue**| Freeze | 10% | Grow +1 · Freeze opponent in place for 3 s (ice-blue tint) |

> ⏳ **Expiry:** Food uneaten within **8 seconds** auto-respawns at a new random grid coordinate.

---

## Bullets & Combat / ระบบการต่อสู้

- **Requirements:** Snake length must be $\ge 6$ (length after firing stays $\ge 3$) and cooldown (0.5 s) has elapsed.
- **Cost:** Firing consumes **3 length units**.
- **On hit opponent:** Opponent suffers **−2 score points** and shrinks safely by **1 segment** (never below starting length 3).
- **On hit food:** Destroys food immediately, causing it to respawn elsewhere.
- **Friendly Fire:** Cannot hit the snake that fired the bullet.

---

## Scoring & Combos / ระบบคะแนน

- **Base Score:** Eating any food awards **+1 point**.
- **Combo Chain:** Eating consecutive food items within **3 seconds** builds up combo multiplier:
  $$\text{Multiplier} = \min(5, \text{combo} + 1)$$
  Awards $\text{combo} - 1$ extra bonus points on top of base point.
- **Bullet Penalties:** Hitting opponent reduces their score by 2 (can drop below zero).

---

## Collision & Win Conditions / การชนและเงื่อนไขชนะ

### Collision Rules:
- **Head-on-head:** Longer snake wins. Equal length = draw.
  - *Shield interaction:* If one player has a shield, the shielded player wins. If both have shields, both shields break and both receive 1.5 s ghost mode.
- **Head hits body:** Head loses immediately (unless shielded, which activates 1.5 s ghost mode to escape).
- **Wall hit:** Lose immediately (Shield = warps head to opposite edge).
- **Self-collision:** Lose immediately (Shield = activates 1.5 s ghost mode).

### Timeout Rules (180 s):
1. **Higher score wins.**
2. **Score tie:** Longer snake wins.
3. **Length tie:** Match ends in a **Draw**.

---

## Architecture & OOP Design / สถาปัตยกรรมซอฟต์แวร์

The project strictly follows **MVC (Model-View-Controller)** and standard OOP design patterns:

```
src/main/java/SnakeGame2P/
├── Main.java                          Entry point & window setup
├── controller/
│   ├── GameController.java            Game loop, collision detection, game state
│   └── PowerBoostManager.java         Speed-boost timer & shrink management
├── model/
│   ├── behavior/                      Interfaces: Movable, Collidable, Consumable, PowerBoostable
│   └── entity/
│       ├── GameObject.java            Abstract entity base (position & Collidable)
│       ├── Snake.java                 Player snake entity (Movable, PowerBoostable)
│       ├── Bullet.java                Combat projectile entity (Movable, Collidable)
│       ├── Food.java                  Abstract food base (Consumable)
│       ├── NormalFood.java            Normal red food
│       ├── GhostFood.java             Phase-through power-up
│       ├── ShieldFood.java            Collision absorption shield
│       ├── FreezeFood.java            Freeze opponent power-up
│       └── FoodFactory.java           Factory Pattern for random food generation
├── view/
│   ├── GamePanel.java                 Main JPanel, toast notifications, UI controls
│   ├── GameRenderer.java              Strategy pattern dispatcher
│   └── renderer/
│       ├── GameRendererStrategy.java  Strategy interface for state screens
│       ├── MenuRenderer.java          Main menu screen renderer
│       ├── PlayingRenderer.java       In-game HUD, status badges, objects
│       ├── PausedRenderer.java        Pause overlay screen
│       └── GameOverRenderer.java      Match result screen
├── audio/
│   └── AudioManager.java              Audio mixer & WAV clip playback
└── utils/
    ├── Constants.java                 Game configuration constants & timings
    ├── Direction.java                 Enum: UP, DOWN, LEFT, RIGHT, NONE
    └── GameState.java                 Enum: MENU, PLAYING, PAUSED, GAME_OVER
```

### Design Patterns Used
- **MVC (Model-View-Controller):** Separates core logic (`GameController`), entities (`Snake`, `Food`), and presentation (`GamePanel`, `GameRenderer`).
- **Strategy Pattern:** `GameRendererStrategy` decouples screen drawing per `GameState` (`MenuRenderer`, `PlayingRenderer`, `PausedRenderer`, `GameOverRenderer`).
- **Factory Pattern:** `FoodFactory` encapsulates the weighted random spawning of special food subclasses.
- **Template Method:** `Food` and `GameObject` define shared life-cycle and coordinate logic while subclasses define specific behaviors (`onConsumed`, `draw`).

---

## Unit Testing / การทดสอบ

Unit tests are written with **JUnit 5** covering all core gameplay mechanics and edge cases:

| Test Class | Focus |
|------------|-------|
| `SnakeTest.java` | Movement, turning, growth, speed-boost shrink, bullets, combos, shield, ghost, freeze |
| `BulletTest.java` | Trajectory, boundary detection, collision with snakes and game objects |
| `FoodTest.java` | Grid positioning, occupancy prevention, consumption effects of all 4 food types, factory |
| `CollisionTest.java` | Self-collision, head-to-head, head-to-body edge cases |
| `GameControllerLogicTest.java` | State transitions, tiebreaker timeout rules, mute toggle & toast, audio triggers |
| `GameObjectTest.java` | Coordinate handling, collision base logic, encapsulation |
| `PowerBoostManagerTest.java` | Speed-boost interval timers, automatic deactivation at min length |
| `DirectionTest.java` | Direction opposites and reversal prevention |

```bash
# Run all unit tests
./gradlew test
```

---

## How to Build & Run / วิธีรันโปรเจกต์

**Requirements:** JDK 17+ (JDK 21/24 supported), Gradle (wrapper included).

```bash
# Run tests
./gradlew test

# Launch the game
./gradlew run
```

Or open the project in **IntelliJ IDEA** and run `SnakeGame2P.Main`.

---

## Sound Credits / เครดิตเสียง

| Track | Usage |
|-------|-------|
| `Start-Menu.wav` | Main menu background theme |
| `Spider-Dance.wav` | Gameplay background battle theme |
| `Home-_Music-Box_.wav` | Game-over screen theme |
| `apple-bite.wav` | SFX for eating food, firing bullets, and collision effects |
