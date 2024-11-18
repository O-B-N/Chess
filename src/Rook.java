import java.util.List;

/**
 * a rook class
 */
public class Rook extends Piece {

    /**
     * creates a rook piece
     *
     * @param color of the piece
     */
    Rook(boolean color) {
        super(color, 'R', 500);
    }

    @Override
    public Piece copy() {
        return new Rook(this.color);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        return this.allStraightMoves(b, s);
    }
}
