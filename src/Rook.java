import java.util.List;

/**
 * rook class
 */
public class Rook extends Piece {
    private boolean kingSide = false;

    /**
     * creates a rook piece
     * @param color of the piece
     * @param s the square of the piece
     */
    Rook(boolean color, Square s) {
        super(color, 'R', s, 500);
    }

    /**
     * creates a queen piece
     * @param color of the piece
     * @param s the square of the piece
     * @param moved ture if the rook had moved
     * @param kingSide if it's the king-side's rook
     */
    Rook(boolean color, Square s, boolean moved, boolean kingSide) {
        super(color, 'R', s, 500);
        this.moved = moved;
        this.kingSide = kingSide;
    }

    @Override
    public Piece copy() {
        return new Rook(this.color, this.s, this.moved, this.kingSide);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        return this.allStraightMoves(b, checkForChecks);
    }

    @Override
    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.moved == ((Rook) p).wasMoved()
                && this.s.equal(p.getSquare()));
    }

    @Override
    public int move(Square s) {
        this.s = s;
        if (!this.moved) {
            this.moved = true;
            return kingSide ? 1 : 2 + (this.color ? 0 : 2);
        }
        return 0;
    }
}
