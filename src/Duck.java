import java.util.ArrayList;
import java.util.List;

/**
 * duck class
 */
public class Duck extends Piece {

    /**
     * creates a new duck
     */
    public Duck() {
        super(true, 'P', null, 1);
    }

    /**
     * creates a new duck on a square
     * @param s the square
     */
    public Duck(Square s) {
        super(true, 'P', s, 1);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        Square end;
        List<Move> l = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                end = new Square(i, j);
                if (b.isEmpty(s)) {
                    l.add(new Move(this.s, end, b, b.getColor()));
                }
            }
        }
        return l;
    }

    @Override
    public Piece copy() {
        return new Duck(this.s);
    }
}
