package Pacman;

public class BlinkyStrategy implements TargetingStrategy {
    @Override
    public int[] getTarget(Ghost ghost, Player player, Ghost blinky) {
        return new int[]{player.getX(), player.getY()};
    }
}
