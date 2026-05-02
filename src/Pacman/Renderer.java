package Pacman;

import edu.princeton.cs.introcs.StdDraw;

public class Renderer {
    private static final double SPACE = 20.0;
    private static final double BORDER = 20.0;
    private static final double SCORE_AREA = 40.0;

    private final double winWidth;
    private final double winHeight;

    public Renderer() {
        winWidth = BORDER * 2 + GameBoard.COLS * SPACE;
        winHeight = SCORE_AREA + GameBoard.ROWS * SPACE + BORDER * 2;
    }

    public double getWinWidth() { return winWidth; }
    public double getWinHeight() { return winHeight; }

    public void initCanvas() {
        StdDraw.setXscale(-50, winWidth + 50);
        StdDraw.setYscale(0, winHeight);
    }

    public void clear() {
        StdDraw.clear(StdDraw.BLACK);
    }

    public void drawMaze() {
        StdDraw.picture(BORDER + SPACE * 14, SCORE_AREA + SPACE * 16,
                "images/maze.jpg", SPACE * 29, SPACE * 32);
    }

    public void drawPellets(GameBoard board) {
        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLS; col++) {
                int tile = board.getTile(col, row);
                if (tile == GameBoard.TILE_PELLET) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledCircle(
                            col * SPACE + SPACE / 2 + BORDER,
                            SCORE_AREA + (GameBoard.ROWS - row) * SPACE, 2);
                } else if (tile == GameBoard.TILE_POWER_PELLET) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledCircle(
                            col * SPACE + SPACE / 2 + BORDER,
                            SCORE_AREA + (GameBoard.ROWS - row) * SPACE, 7.5);
                }
            }
        }
    }

    public void drawPlayer(Player player) {
        int x = player.getX();
        int y = player.getY();
        int degree = 0;
        switch (player.getDirection()) {
            case LEFT: degree = 180; break;
            case UP: degree = 90; break;
            case RIGHT: degree = 0; break;
            case DOWN: degree = 270; break;
        }
        StdDraw.picture(
                x * SPACE + SPACE / 2 + BORDER,
                SCORE_AREA + SCORE_AREA + (GameBoard.ROWS - y - 1) * SPACE - SPACE / 2 - 10,
                "images/player.png", SPACE * 1.5, SPACE * 1.5, degree);
    }

    public void drawGhost(Ghost ghost, boolean frightened) {
        int x = ghost.getX();
        int y = ghost.getY();
        String image;
        if (frightened && !ghost.isInPrison()) {
            image = "images/vunerableFantome.png";
        } else {
            switch (ghost.getName()) {
                case "Blinky": image = "images/blinky.png"; break;
                case "Pinky": image = "images/pinky.png"; break;
                case "Inky": image = "images/inky.png"; break;
                default: image = "images/clyde.png"; break;
            }
        }
        StdDraw.picture(
                x * SPACE + SPACE / 2 + BORDER,
                SCORE_AREA + SCORE_AREA + (GameBoard.ROWS - y - 1) * SPACE - SPACE / 2 - 10,
                image, SPACE * 1.5, SPACE * 1.5, 0);
    }

    public void drawScore(int score) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(80, 20, "Score ");
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.text(140, 20, "" + score);
    }

    public void drawLives(int lives) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(300, 20, "Lives ");
        for (int i = 0; i < lives && i < 3; i++) {
            StdDraw.picture(350 + i * 40, 20, "images/player.png", 30, 30, 180);
        }
    }

    public void drawCenterText(String text) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(winWidth / 2, winHeight / 2, text);
    }

    public void drawStartScreen() {
        clear();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.picture(winWidth / 2, winHeight / 2 + 80, "images/logo.png");
        StdDraw.text(winWidth / 2, winHeight / 2 - 40, "Press SPACE to start !");
        StdDraw.show(20);
    }

    public void drawWinScreen(int score) {
        clear();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.picture(winWidth / 2, winHeight / 2 + 80, "images/youWin.jpeg");
        StdDraw.text(winWidth / 2, winHeight / 2 - 20, "Your score : " + score);
        StdDraw.text(winWidth / 2, winHeight / 2 - 60, "Press SPACE to try again !");
        StdDraw.show(20);
    }

    public void drawGameOverScreen(int score) {
        clear();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.picture(winWidth / 2, winHeight / 2 + 80, "images/gameOver.jpeg");
        StdDraw.text(winWidth / 2, winHeight / 2 - 50, "Your score : " + score);
        StdDraw.text(winWidth / 2, winHeight / 2 - 90, "Press SPACE to try again !");
        StdDraw.show(20);
    }

    public void show(int delay) {
        StdDraw.show(delay);
    }

    public void pause(int ms) {
        StdDraw.pause(ms);
    }
}
