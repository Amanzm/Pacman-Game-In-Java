package Pacman;

public class GameBoard {
    public static final int ROWS = 31;
    public static final int COLS = 28;

    public static final int TILE_WALL = 0;
    public static final int TILE_PELLET = 1;
    public static final int TILE_EMPTY = 2;
    public static final int TILE_DOOR = 3;
    public static final int TILE_POWER_PELLET = 4;

    public static final int PELLET_SCORE = 10;
    public static final int POWER_PELLET_SCORE = 50;

    public static final int TUNNEL_LEFT = 0;
    public static final int TUNNEL_RIGHT = 27;

    private final int[][] tiles;
    private int pelletsRemaining;

    public GameBoard() {
        tiles = new int[ROWS][COLS];
        pelletsRemaining = 0;
        reset();
    }

    public void reset() {
        pelletsRemaining = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                tiles[row][col] = Maze.walls[row][col];
                if (tiles[row][col] == TILE_PELLET || tiles[row][col] == TILE_POWER_PELLET) {
                    pelletsRemaining++;
                }
            }
        }
    }

    public int getTile(int col, int row) {
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return TILE_WALL;
        return tiles[row][col];
    }

    public boolean isWalkable(int col, int row) {
        int tile = getTile(col, row);
        return tile != TILE_WALL;
    }

    public boolean isWalkableExcludingDoor(int col, int row) {
        int tile = getTile(col, row);
        return tile != TILE_WALL && tile != TILE_DOOR;
    }

    public int consumeTile(int col, int row) {
        int tile = tiles[row][col];
        if (tile == TILE_PELLET || tile == TILE_POWER_PELLET) {
            tiles[row][col] = TILE_EMPTY;
            pelletsRemaining--;
            return tile;
        }
        return TILE_EMPTY;
    }

    public int getPelletsRemaining() {
        return pelletsRemaining;
    }

    public boolean allPelletsEaten() {
        return pelletsRemaining <= 0;
    }
}
