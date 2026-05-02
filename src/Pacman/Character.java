package Pacman;

public abstract class Character {
    protected int x;
    protected int y;
    protected Direction direction = Direction.LEFT;

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public int getState() { return direction.getCode(); }
    public void setState(int state) { this.direction = Direction.fromCode(state); }

    public void move(GameBoard board) {
        int nextX = x + direction.getDx();
        int nextY = y + direction.getDy();

        if (nextX < GameBoard.TUNNEL_LEFT) {
            x = GameBoard.TUNNEL_RIGHT;
        } else if (nextX > GameBoard.TUNNEL_RIGHT) {
            x = GameBoard.TUNNEL_LEFT;
        } else if (board.isWalkable(nextX, nextY)) {
            x = nextX;
            y = nextY;
        }
    }
}
