package Pacman;

public class InkyStrategy implements TargetingStrategy {
    private static final int LOOK_AHEAD = 2;

    @Override
    public int[] getTarget(Ghost ghost, Player player, Ghost blinky) {
        int px = player.getX();
        int py = player.getY();
        Direction facing = player.getDirection();

        int pivotX = px + facing.getDx() * LOOK_AHEAD;
        int pivotY = py + facing.getDy() * LOOK_AHEAD;

        int tx = pivotX + (pivotX - blinky.getX());
        int ty = pivotY + (pivotY - blinky.getY());

        tx = Math.max(0, Math.min(GameBoard.COLS - 1, tx));
        ty = Math.max(0, Math.min(GameBoard.ROWS - 1, ty));
        return new int[]{tx, ty};
    }
}
