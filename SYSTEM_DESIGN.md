# Pac-Man Game — System Design Document

## 1. Project Overview

A Java implementation of the classic Pac-Man arcade game featuring authentic ghost AI behavior, proper game state management, and clean object-oriented architecture. Built using Princeton's StdDraw library for rendering.

### Key Features
- Original arcade ghost AI with unique personalities (Blinky, Pinky, Inky, Clyde)
- Chase / Scatter / Frightened mode cycling
- Power pellet mechanics with ghost vulnerability
- Tunnel wrapping, ghost prison with staggered release
- Score tracking with combo multipliers for eating ghosts

---

## 2. Architecture Overview

The project follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                  Game (Orchestrator)             │
├─────────────────────────────────────────────────┤
│  Renderer  │  CollisionDetector  │  GhostMode   │
│            │                     │  Manager     │
├─────────────────────────────────────────────────┤
│  Player  │  Ghost  │  GameBoard  │  Direction   │
├─────────────────────────────────────────────────┤
│  Character (Abstract Base)  │  MathUtil         │
├─────────────────────────────────────────────────┤
│  TargetingStrategy (Interface)                  │
│  BlinkyStrategy │ PinkyStrategy │ InkyStrategy  │
│  ClydeStrategy                                  │
├─────────────────────────────────────────────────┤
│  Maze (Static Data)  │  GhostMode (Enum)       │
│  Direction (Enum)                               │
└─────────────────────────────────────────────────┘
```

---

## 3. Class Diagram (UML)

```
┌──────────────────────────────────┐
│         <<abstract>>             │
│          Character               │
├──────────────────────────────────┤
│ # x: int                         │
│ # y: int                         │
│ # direction: Direction           │
├──────────────────────────────────┤
│ + getX(): int                    │
│ + getY(): int                    │
│ + getDirection(): Direction      │
│ + move(board: GameBoard): void   │
└──────────┬───────────┬───────────┘
           │           │
    ┌──────┴──┐   ┌────┴─────────────────────────────┐
    │ Player  │   │              Ghost                │
    ├─────────┤   ├───────────────────────────────────┤
    │ - score │   │ - name: String                    │
    │ - lives │   │ - strategy: TargetingStrategy     │
    │ - buff  │   │ - scatterTargetX/Y: int           │
    │   Dir   │   │ - inPrison: boolean               │
    ├─────────┤   │ - prisonTimer: int                │
    │ +handle │   │ - prisonDelay: int                │
    │  Input()│   ├───────────────────────────────────┤
    │ +update │   │ + updateDirection(player, blinky, │
    │  Dir()  │   │     modeManager, board): void     │
    │ +addScr │   │ + sendToPrison(): void            │
    │ +loseL  │   │ + resetPosition(): void           │
    └─────────┘   └──────────────┬────────────────────┘
                                 │ uses
                                 ▼
                  ┌──────────────────────────────┐
                  │   <<interface>>               │
                  │   TargetingStrategy           │
                  ├──────────────────────────────┤
                  │ + getTarget(ghost, player,    │
                  │     blinky): int[]            │
                  └──────┬───┬───┬───┬───────────┘
                         │   │   │   │
         ┌───────────────┘   │   │   └────────────────┐
         ▼                   ▼   ▼                    ▼
┌──────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│BlinkyStrategy│ │PinkyStrategy│ │InkyStrategy│ │ClydeStrategy│
├──────────────┤ ├────────────┤ ├────────────┤ ├────────────┤
│ Targets      │ │ Targets 4  │ │ Flanking   │ │ Chases if  │
│ player       │ │ tiles ahead│ │ vector via │ │ far, else  │
│ directly     │ │ of player  │ │ Blinky     │ │ retreats   │
└──────────────┘ └────────────┘ └────────────┘ └────────────┘


┌────────────────────────┐       ┌─────────────────────────┐
│      GhostModeManager  │       │     CollisionDetector   │
├────────────────────────┤       ├─────────────────────────┤
│ - currentMode          │       │ - ghostsEatenCombo: int │
│ - modeTimer            │       ├─────────────────────────┤
│ - modePhase            │       │ + check(player, ghosts, │
│ - frightened           │       │     modeManager):       │
│ - frightenedTimer      │       │     CollisionResult     │
├────────────────────────┤       │ + resetCombo(): void    │
│ + update(): void       │       └─────────────────────────┘
│ + startFrightened()    │
│ + getActiveMode()      │
│ + reset(): void        │       ┌─────────────────────────┐
└────────────────────────┘       │        Renderer         │
                                 ├─────────────────────────┤
┌────────────────────────┐       │ + initCanvas()          │
│       GameBoard        │       │ + clear()               │
├────────────────────────┤       │ + drawMaze()            │
│ - tiles: int[][]       │       │ + drawPellets(board)    │
│ - pelletsRemaining     │       │ + drawPlayer(player)    │
├────────────────────────┤       │ + drawGhost(ghost, fri) │
│ + getTile(col, row)    │       │ + drawScore(score)      │
│ + isWalkable(col, row) │       │ + drawLives(lives)      │
│ + consumeTile(col,row) │       │ + drawStartScreen()     │
│ + allPelletsEaten()    │       │ + drawWinScreen(score)  │
│ + reset(): void        │       │ + drawGameOverScreen()  │
└────────────────────────┘       └─────────────────────────┘


┌────────────────────────┐       ┌─────────────────────────┐
│    <<enum>> Direction  │       │   <<enum>> GhostMode    │
├────────────────────────┤       ├─────────────────────────┤
│ LEFT(1, -1, 0)         │       │ CHASE                   │
│ UP(2, 0, -1)           │       │ SCATTER                 │
│ RIGHT(3, 1, 0)         │       │ FRIGHTENED              │
│ DOWN(4, 0, 1)          │       └─────────────────────────┘
├────────────────────────┤
│ + opposite(): Direction│
│ + fromCode(int): Dir   │
│ + PRIORITY_ORDER       │
└────────────────────────┘


┌────────────────────────────────────────────────────────┐
│                        Game                            │
├────────────────────────────────────────────────────────┤
│ - renderer: Renderer                                   │
│ - board: GameBoard                                     │
│ - modeManager: GhostModeManager                       │
│ - collisionDetector: CollisionDetector                │
│ - player: Player                                       │
│ - ghosts: Ghost[]                                      │
├────────────────────────────────────────────────────────┤
│ + run(): void                                          │
│ - tick(): void                                         │
│ - handlePelletConsumption(): void                     │
│ - handleCollisions(): boolean                         │
│ - resetAfterDeath(): void                             │
│ - resetForNewGame(): void                             │
│ + main(args): void                                     │
└────────────────────────────────────────────────────────┘
```

---

## 4. Design Patterns Used

### 4.1 Strategy Pattern
**Where:** Ghost targeting behavior  
**Why:** Each ghost has a unique AI personality. Instead of a giant if/else chain checking the ghost's name, each ghost is injected with a `TargetingStrategy` implementation at construction time.

```java
// Adding a new ghost type requires ZERO changes to existing code:
Ghost newGhost = new Ghost(x, y, "Shadow", new ShadowStrategy(), ...);
```

**Benefit:** Open/Closed Principle — extend behavior without modifying existing classes.

### 4.2 Template Method Pattern (Implicit)
**Where:** `Character.move()` provides the base movement algorithm. Subclasses (`Player`, `Ghost`) override direction-selection logic but share the same movement mechanics.

### 4.3 Composition over Inheritance
**Where:** `Ghost` has-a `TargetingStrategy` rather than using subclasses like `BlinkyGhost extends Ghost`.  
**Why:** Avoids class explosion. Strategy can theoretically be swapped at runtime (e.g., for testing or difficulty modes).

### 4.4 State Pattern (Simplified)
**Where:** `GhostModeManager` manages transitions between CHASE → SCATTER → FRIGHTENED.  
**Why:** Centralizes complex timer-based state transitions. Ghosts query the manager rather than tracking their own mode.

### 4.5 Facade Pattern
**Where:** `Renderer` class  
**Why:** Hides all StdDraw complexity behind a clean interface. If you swap rendering libraries, only `Renderer` changes.

---

## 5. SOLID Principles

| Principle | How It's Applied |
|-----------|-----------------|
| **S** — Single Responsibility | Each class has one reason to change. `Renderer` only renders. `CollisionDetector` only detects collisions. `GameBoard` only manages tile state. |
| **O** — Open/Closed | New ghost behaviors via new `TargetingStrategy` implementations. No modification to `Ghost` or `Game`. |
| **L** — Liskov Substitution | Any `TargetingStrategy` implementation can replace another without breaking `Ghost`. `Player` and `Ghost` both work as `Character`. |
| **I** — Interface Segregation | `TargetingStrategy` has exactly one method. Clients aren't forced to implement unused methods. |
| **D** — Dependency Inversion | `Ghost` depends on the `TargetingStrategy` abstraction, not concrete strategy classes. High-level `Game` depends on abstractions (`GameBoard`, `GhostModeManager`). |

---

## 6. Key Design Decisions

### 6.1 Direction as Enum vs Magic Integers
**Problem:** Original code used `1, 2, 3, 4` for directions with no type safety.  
**Solution:** `Direction` enum with `dx`, `dy`, `opposite()`, and `fromCode()`. Eliminates invalid states and makes code self-documenting.

### 6.2 GameBoard Encapsulation
**Problem:** Raw `int[][]` accessed directly everywhere with no bounds checking.  
**Solution:** `GameBoard` wraps the array with named constants (`TILE_WALL`, `TILE_PELLET`), bounds-safe access, and pellet tracking. Single source of truth for maze state.

### 6.3 Collision as Separate Concern
**Problem:** Collision logic was duplicated twice in the game loop.  
**Solution:** `CollisionDetector` with a `CollisionResult` value object. Called once, returns whether player died or earned points. Combo tracking is encapsulated.

### 6.4 Renderer Isolation
**Problem:** StdDraw calls scattered across 200+ lines in Game.  
**Solution:** All rendering in `Renderer`. If you wanted to port to JavaFX, Swing, or a web canvas, you'd only rewrite this one class.

---

## 7. Ghost AI — Original Arcade Behavior

### Mode Cycling (Timer-Based)
```
SCATTER (7s) → CHASE (20s) → SCATTER (7s) → CHASE (20s) → 
SCATTER (5s) → CHASE (20s) → CHASE (permanent)
```

### Ghost Personalities

| Ghost | Strategy | Behavior |
|-------|----------|----------|
| Blinky (Red) | `BlinkyStrategy` | Directly targets Pac-Man's current tile. Most aggressive. |
| Pinky (Pink) | `PinkyStrategy` | Targets 4 tiles ahead of Pac-Man's facing direction. Ambusher. |
| Inky (Cyan) | `InkyStrategy` | Doubles vector from Blinky through 2 tiles ahead of Pac-Man. Unpredictable flanker. |
| Clyde (Orange) | `ClydeStrategy` | Chases when >8 tiles away, retreats to corner when close. Shy. |

### Pathfinding Algorithm
At each intersection, ghosts evaluate all non-reverse directions. They pick the direction whose next tile minimizes Euclidean distance to the target. Tie-breaking follows arcade priority: UP > LEFT > DOWN > RIGHT.

### Frightened Mode
When Pac-Man eats a power pellet, all ghosts switch to random movement for a fixed duration. They can be eaten for escalating points (200 → 400 → 800 → 1600).

---

## 8. Game Loop Architecture

```
┌─────────────────────────────────────────┐
│              Game.tick()                 │
├─────────────────────────────────────────┤
│ 1. Clear screen                         │
│ 2. Draw maze + pellets + HUD            │
│ 3. Update ghost mode timer              │
│ 4. Player: read input → update dir →    │
│    move → consume pellets               │
│ 5. Check collisions (pre-ghost-move)    │
│ 6. Ghosts: update dir → move → draw     │
│ 7. Check collisions (post-ghost-move)   │
│ 8. Render frame                         │
│ 9. Check win/lose conditions            │
└─────────────────────────────────────────┘
```

Collisions are checked twice per frame (before and after ghost movement) to prevent "pass-through" at high speeds — matching the original arcade behavior.

---

## 9. File Structure

```
src/Pacman/
├── Game.java               Orchestrator — game loop, initialization
├── Character.java          Abstract base — position, movement
├── Player.java             Input handling, lives, score
├── Ghost.java              Prison logic, delegates to strategy
├── TargetingStrategy.java  Interface for ghost AI
├── BlinkyStrategy.java     Direct chase
├── PinkyStrategy.java      Ambush (4 tiles ahead)
├── InkyStrategy.java       Flanking vector
├── ClydeStrategy.java      Shy (chase/retreat threshold)
├── GameBoard.java          Maze state, tile access, pellet tracking
├── GhostModeManager.java   Chase/Scatter/Frightened state machine
├── GhostMode.java          Enum: CHASE, SCATTER, FRIGHTENED
├── Direction.java          Enum: LEFT, UP, RIGHT, DOWN
├── CollisionDetector.java  Collision logic + combo scoring
├── Renderer.java           All StdDraw rendering calls
├── MathUtil.java           Distance calculation utility
└── Maze.java               Static maze data
```

---

## 10. How to Explain This in an Interview

### Opening (30 seconds)
"I built a Pac-Man clone in Java that implements the original arcade ghost AI. The interesting part isn't the game itself — it's the architecture. I used the Strategy pattern for ghost personalities, a state machine for mode management, and clean separation between rendering, game logic, and AI."

### Design Discussion Points

1. **Why Strategy over inheritance?**  
   "Four ghost subclasses would mean class explosion. Strategy lets me inject behavior at construction time. Adding a fifth ghost is one new class — zero changes to existing code."

2. **Why separate Renderer?**  
   "If I wanted to port this to JavaFX or write unit tests for game logic, I shouldn't need to mock a graphics library. Renderer is the only class that knows about StdDraw."

3. **Why CollisionDetector as its own class?**  
   "Collision logic was duplicated and mixed with game state mutation. Extracting it made the game loop readable and the collision rules testable in isolation."

4. **How does the ghost AI work?**  
   "Each ghost has a target tile that changes based on mode. In Chase, each ghost calculates its target differently — Blinky targets Pac-Man directly, Pinky targets 4 ahead, Inky uses a vector from Blinky, Clyde retreats when close. At intersections, they pick the direction that minimizes distance to their target. In Scatter, they retreat to assigned corners. In Frightened, they move randomly."

5. **What would you improve?**  
   "I'd add proper event-driven input instead of polling, implement a game state machine (MENU → PLAYING → PAUSED → GAME_OVER), and add unit tests for the strategies and collision detector. The Renderer could also use double-buffering for smoother animation."

---

## 11. Potential Extensions

| Extension | Design Impact |
|-----------|--------------|
| New ghost type | Add one `XxxStrategy.java` — zero changes elsewhere |
| Difficulty levels | Adjust `GhostModeManager` timer constants |
| Different mazes | Swap `Maze.java` data, `GameBoard` handles the rest |
| Multiplayer | `Player` is already decoupled from input source |
| Unit tests | All logic classes are testable without rendering |
| Different renderer | Replace `Renderer` implementation only |

---

## 12. Technologies

- **Language:** Java
- **Graphics:** Princeton StdDraw (edu.princeton.cs.introcs)
- **Build:** javac with classpath to stdlib-package.jar
- **Patterns:** Strategy, State (simplified), Facade, Composition
- **Principles:** SOLID, DRY, Separation of Concerns
