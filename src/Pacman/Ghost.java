package Pacman;

public class Ghost extends Character {
    private final String name;
    private final TargetingStrategy strategy;
    private final int scatterTargetX;
    private final int scatterTargetY;
    private final int prisonDelay;
    private final int startX;
    private final int startY;

    private boolean inPrison;
    private int prisonTimer;

    public Ghost(int x, int y, String name, TargetingStrategy strategy,
                 int scatterX, int scatterY, int prisonDelay) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.name = name;
        this.strategy = strategy;
        this.scatterTargetX = scatterX;
        this.scatterTargetY = scatterY;
        this.prisonDelay = prisonDelay;
        this.inPrison = (prisonDelay > 0);
        this.prisonTimer = 0;
        this.direction = Direction.LEFT;
    }

    public String getName() { return name; }
    public int getScatterTargetX() { return scatterTargetX; }
    public int getScatterTargetY() { return scatterTargetY; }
    public boolean isInPrison() { return inPrison; }

    public void sendToPrison() {
        x = 13;
        y = 14;
        inPrison = true;
        prisonTimer = 0;
    }

    public void resetPosition() {
        x = startX;
        y = startY;
        inPrison = (prisonDelay > 0);
        prisonTimer = 0;
        direction = Direction.LEFT;
    }

    public void updateDirection(Player player, Ghost blinky, GhostModeManager modeManager, GameBoard board) {
        prisonTimer++;

        if (inPrison) {
            handlePrisonMovement();
            return;
        }

        if (x == GameBoard.TUNNEL_LEFT && direction == Direction.LEFT) return;
        if (x == GameBoard.TUNNEL_RIGHT && direction == Direction.RIGHT) return;

        GhostMode mode = modeManager.getActiveMode();

        if (mode == GhostMode.FRIGHTENED) {
            chooseFrightenedDirection(board);
        } else {
            int targetX, targetY;
            if (mode == GhostMode.SCATTER) {
                targetX = scatterTargetX;
                targetY = scatterTargetY;
            } else {
                int[] target = strategy.getTarget(this, player, blinky);
                targetX = target[0];
                targetY = target[1];
            }
            chooseTargetDirection(targetX, targetY, board);
        }
    }

    private void handlePrisonMovement() {
        if ((x == 13 || x == 14) && y == 11) {
            inPrison = false;
            direction = Direction.LEFT;
            return;
        }

        if (prisonTimer < prisonDelay) {
            navigateInsidePrison();
            return;
        }

        navigateInsidePrison();
    }

    private void navigateInsidePrison() {
        if ((x == 11 || x == 12) && y >= 12 && y <= 14) {
            direction = Direction.RIGHT;
        } else if ((x == 13 || x == 14) && y >= 12 && y <= 14) {
            direction = Direction.UP;
        } else if ((x == 15 || x == 16) && y >= 12 && y <= 14) {
            direction = Direction.LEFT;
        }
    }

    private void chooseFrightenedDirection(GameBoard board) {
        Direction opposite = direction.opposite();
        int count = 0;
        Direction[] available = new Direction[4];

        for (Direction d : Direction.PRIORITY_ORDER) {
            if (d == opposite) continue;
            int nx = x + d.getDx();
            int ny = y + d.getDy();
            if (board.isWalkable(nx, ny)) {
                available[count++] = d;
            }
        }

        if (count > 0) {
            direction = available[(int)(Math.random() * count)];
        }
    }

    private void chooseTargetDirection(int targetX, int targetY, GameBoard board) {
        Direction opposite = direction.opposite();
        double bestDist = Double.MAX_VALUE;
        Direction bestDir = direction;

        for (Direction d : Direction.PRIORITY_ORDER) {
            if (d == opposite) continue;
            int nx = x + d.getDx();
            int ny = y + d.getDy();
            if (board.isWalkable(nx, ny)) {
                double dist = MathUtil.distance(nx, ny, targetX, targetY);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestDir = d;
                }
            }
        }

        direction = bestDir;
    }
}
