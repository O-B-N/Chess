import java.util.List;

public class Queen extends Piece {

    public Queen(boolean color, Square s) {
        super(color, 'Q', s);
    }

    @Override
    public Queen copy() {
        return new Queen(this.getColor(), this.s);
    }


    //rook + bishop
    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> l = this.allStraightMoves(b, checkForChecks);
        l.addAll(this.allDiagonalMoves(b, checkForChecks));
        return l;
    }
}
