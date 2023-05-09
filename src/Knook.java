import java.util.List;

/**
 * a knook class
 */
public class Knook extends Piece {

    /**
     * creates a knook piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public Knook(boolean color,  Square s) {
        super(color, 'N', s, 3);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> l = this.allStraightMoves(b, checkForChecks);
        l.addAll(this.knightMoves(b, checkForChecks));
        return l;
    }

    @Override
    public Piece copy() {
        return null;
    }
}
