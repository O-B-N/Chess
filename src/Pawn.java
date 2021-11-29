import java.util.List;

public class Pawn extends Piece {

    public Pawn(boolean color, Square s) {
        super(color, 'P', s, 1);
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        if (this.color && this.getSquare().getRow() == 1) {
            //allow double
        }
     //   if (b.lastMove.isDoublePawnPush())
        return null;
    }
}
