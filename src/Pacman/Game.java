package Pacman;

import java.awt.event.KeyEvent;
import edu.princeton.cs.introcs.StdDraw;

public class Game {
    private static final int GAME_TICK_MS = 120;
    private static final int READY_PAUSE_MS = 3000;

    private final Renderer renderer;
    private final GameBoard board;
    private final GhostModeManager modeManager;
    private final CollisionDetector collisionDetector;

    private Player player;
    private Ghost[] ghosts;
    private boolean showReady;

    public Game() {
        renderer = new Renderer();
        board = new GameBoard();
        modeManager = new GhostModeManager();
        collisionDetector = new CollisionDetector();
        showReady = true;
        initCharacters();
    }

    private void initCharacters() {
        player = new Player(14, 23, Direction.LEFT);
        ghosts = new Ghost[]{
            new Ghost(13, 11, "Blinky", new BlinkyStrategy(), 25, 0, 0),
            new Ghost(13, 14, "Pinky", new PinkyStrategy(), 2, 0, 10),
            new Ghost(11, 14, "Inky", new InkyStrategy(), 27, 30, 30),
            new Ghost(15, 14, "Clyde", new ClydeStrategy(), 0, 30, 50)
        };
    }

    private void resetAfterDeath() {
        player.resetPosition();
        for (Ghost ghost : ghosts) {
            ghost.resetPosition();
        }
        modeManager.reset();
        collisionDetector.resetCombo();
        showReady = true;
    }

    private void resetForNewGame() {
        board.reset();
        modeManager.reset();
        collisionDetector.resetCombo();
        initCharacters();
        showReady = true;
    }

    private void handlePelletConsumption() {
        int tile = board.getTile(player.getX(), player.getY());
        if (tile == GameBoard.TILE_PELLET) {
            board.consumeTile(player.getX(), player.getY());
            player.addScore(GameBoard.PELLET_SCORE);
        } else if (tile == GameBoard.TILE_POWER_PELLET) {
            board.consumeTile(player.getX(), player.getY());
            player.addScore(GameBoard.POWER_PELLET_SCORE);
            modeManager.startFrightened();
            collisionDetector.resetCombo();
        }
    }

    private boolean handleCollisions() {
        CollisionDetector.CollisionResult result =
                collisionDetector.check(player, ghosts, modeManager);
        if (result.playerDied) {
            player.loseLife();
            resetAfterDeath();
            return true;
        }
        player.addScore(result.pointsEarned);
        return false;
    }

    private void tick() {
        renderer.clear();
        renderer.drawMaze();
        renderer.drawPellets(board);
        renderer.drawScore(player.getScore());
        renderer.drawLives(player.getLives());

        if (showReady) {
            renderer.drawCenterText("GET READY!");
            showReady = false;
        }

        modeManager.update();

        player.updateDirection(board);
        player.move(board);
        handlePelletConsumption();
        renderer.drawPlayer(player);

        if (handleCollisions()) {
            renderer.show(GAME_TICK_MS);
            return;
        }

        for (Ghost ghost : ghosts) {
            ghost.updateDirection(player, ghosts[0], modeManager, board);
            ghost.move(board);
            renderer.drawGhost(ghost, modeManager.isFrightened());
        }

        if (handleCollisions()) {
            renderer.show(GAME_TICK_MS);
            return;
        }

        renderer.show(GAME_TICK_MS);

        if (board.allPelletsEaten()) {
            handleWin();
        }

        if (player.getLives() <= 0) {
            handleGameOver();
        }
    }

    private void handleWin() {
        do {
            renderer.drawWinScreen(player.getScore());
        } while (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE));
        resetForNewGame();
    }

    private void handleGameOver() {
        do {
            renderer.drawGameOverScreen(player.getScore());
        } while (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE));
        player = new Player(14, 23, Direction.LEFT);
        resetForNewGame();
    }

    private void waitForStart() {
        do {
            renderer.drawStartScreen();
        } while (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE));
    }

    private void drawInitialState() {
        renderer.clear();
        renderer.drawMaze();
        renderer.drawPellets(board);
        renderer.drawScore(player.getScore());
        renderer.drawLives(player.getLives());
        renderer.drawCenterText("GET READY!");
        for (Ghost ghost : ghosts) {
            renderer.drawGhost(ghost, false);
        }
        renderer.drawPlayer(player);
        renderer.show(20);
        renderer.pause(READY_PAUSE_MS);
        showReady = false;
    }

    public void run() {
        renderer.initCanvas();
        waitForStart();
        drawInitialState();
        while (true) {
            tick();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}
