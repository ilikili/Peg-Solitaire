package pegsolitaire;

import java.util.ArrayList;
import java.util.List;

public class MoveHistory {

    private final List<MoveRecord> moves = new ArrayList<>();

    public void add(MoveRecord m) {
        moves.add(m);
    }

    public void clear() {
        moves.clear();
        System.out.print("clear");
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }

    public int size() {
        return moves.size();
    }

    public MoveRecord get(int index) {
        return moves.get(index);
    }

    public List<MoveRecord> getAll() {
        return moves;
    }
}
