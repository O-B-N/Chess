import java.util.List;

public class Queen extends Piece {

    public Queen(boolean color, Square s) {
        super(color, 'Q', s, 9);
    }

    @Override
    public Queen copy() {
        return new Queen(this.getColor(), this.s);
    }


    //rook + bishop
    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.allStraightMoves(b, checkForChecks);
        moves.addAll(this.allDiagonalMoves(b, checkForChecks));
        return moves;
    }
}
