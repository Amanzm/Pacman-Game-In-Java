package Pacman;

public enum Direction {
    LEFT(1, -1, 0),
    UP(2, 0, -1),
    RIGHT(3, 1, 0),
    DOWN(4, 0, 1);

    private final int code;
    private final int dx;
    private final int dy;

    Direction(int code, int dx, int dy) {
        this.code = code;
        this.dx = dx;
        this.dy = dy;
    }

    public int getCode() { return code; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public Direction opposite() {
        switch (this) {
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            case UP: return DOWN;
            case DOWN: return UP;
            default: return this;
        }
    }

    public static Direction fromCode(int code) {
        for (Direction d : values()) {
            if (d.code == code) return d;
        }
        return LEFT;
    }

    public static final Direction[] PRIORITY_ORDER = {UP, LEFT, DOWN, RIGHT};
}
