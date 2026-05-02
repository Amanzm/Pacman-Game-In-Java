package Pacman;

import java.awt.event.KeyEvent;
import edu.princeton.cs.introcs.StdDraw;

public class Player extends Character {
    private static final int INITIAL_LIVES = 3;

    private int score;
    private int lives;
    private Direction bufferedDirection;

    public Player(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.bufferedDirection = direction;
        this.lives = INITIAL_LIVES;
        this.score = 0;
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int points) { this.score += points; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public void loseLife() { this.lives--; }

    public Direction getBufferedDirection() { return bufferedDirection; }

    public void handleInput() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
            bufferedDirection = Direction.LEFT;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
            bufferedDirection = Direction.UP;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
            bufferedDirection = Direction.RIGHT;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
            bufferedDirection = Direction.DOWN;
        }
    }

    public void updateDirection(GameBoard board) {
        handleInput();
        int nextX = x + bufferedDirection.getDx();
        int nextY = y + bufferedDirection.getDy();

        if (nextX < GameBoard.TUNNEL_LEFT || nextX > GameBoard.TUNNEL_RIGHT) {
            direction = bufferedDirection;
        } else if (board.isWalkableExcludingDoor(nextX, nextY)) {
            direction = bufferedDirection;
        }
    }

    public void resetPosition() {
        x = 14;
        y = 23;
        direction = Direction.LEFT;
        bufferedDirection = Direction.LEFT;
    }
}
