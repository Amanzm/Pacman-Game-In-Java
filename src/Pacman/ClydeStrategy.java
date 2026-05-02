package Pacman;

public class ClydeStrategy implements TargetingStrategy {
    private static final double SHY_DISTANCE = 8.0;

    @Override
    public int[] getTarget(Ghost ghost, Player player, Ghost blinky) {
        double dist = MathUtil.distance(ghost.getX(), ghost.getY(), player.getX(), player.getY());
        if (dist > SHY_DISTANCE) {
            return new int[]{player.getX(), player.getY()};
        }
        return new int[]{ghost.getScatterTargetX(), ghost.getScatterTargetY()};
    }
}
