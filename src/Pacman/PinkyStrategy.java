package Pacman;

public class PinkyStrategy implements TargetingStrategy {
    private static final int LOOK_AHEAD = 4;

    @Override
    public int[] getTarget(Ghost ghost, Player player, Ghost blinky) {
        int tx = player.getX();
        int ty = player.getY();
        Direction facing = player.getDirection();

        tx += facing.getDx() * LOOK_AHEAD;
        ty += facing.getDy() * LOOK_AHEAD;

        if (facing == Direction.UP) {
            tx -= LOOK_AHEAD;
        }

        tx = Math.max(0, Math.min(GameBoard.COLS - 1, tx));
        ty = Math.max(0, Math.min(GameBoard.ROWS - 1, ty));
        return new int[]{tx, ty};
    }
}
