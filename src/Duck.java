import java.util.ArrayList;
import java.util.List;

/**
 * a duck class
 */
public class Duck extends Piece {

    /**
     * creates a new duck
     */
    public Duck() {
        super(true, 'D', 0);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        Square end;
        List<Move> l = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                end = new Square(i, j);
                if (b.isEmpty(s)) {
                    l.add(new Move(s, end, b));
                }
            }
        }
        return l;
    }

    @Override
    public Piece copy() {
        return new Duck();
    }
}