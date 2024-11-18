import java.util.List;

/**
 * a knook class
 */
public class Knook extends Piece {

    /**
     * creates a knook piece
     * @param color of the piece
     */
    public Knook(boolean color) {
        super(color, 'N', 3);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        List<Move> l = this.allStraightMoves(b, s);
        l.addAll(this.knightMoves(b, s));
        return l;
    }

    @Override
    public Piece copy() {
        return null;
    }
}
