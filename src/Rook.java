import java.util.List;

/**
 * rook class
 */
public class Rook extends Piece {

    /**
     * creates a rook piece
     *
     * @param color of the piece
     * @param s     the square of the piece
     */
    Rook(boolean color, Square s) {
        super(color, 'R', s, 500);
    }

    @Override
    public Piece copy() {
        return new Rook(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        return this.allStraightMoves(b, checkForChecks);
    }
}
