package pegsolitaire;

public abstract class Play {

    protected PegBoard board;

    public Play(PegBoard board) {
        this.board = board;
    }

    // Called when this mode becomes active
    public abstract void activate();

    // Called when this mode is no longer active
    public abstract void deactivate();
}