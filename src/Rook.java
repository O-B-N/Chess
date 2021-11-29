import java.util.List;

public class Rook extends Piece implements Moved {
    boolean moved = false;

    Rook(boolean color, Square s) {
        super(color, 'R', s, 5);
    }

    Rook(boolean color, Square s, boolean moved) {
        super(color, 'R', s, 5);
        this.moved = moved;
    }

    @Override
    public Piece copy() {
        return new Rook(this.color, this.s, this.moved);
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
    public void move(Square s) {
        this.s = s;
        this.moved = true;
    }

    public boolean wasMoved() {
        return this.moved;
    }
}
