import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(boolean color,  Square s) {
        super(color, 'N', s);
    }

    @Override
    public Piece copy() {
        return new Knight(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> l = new ArrayList<>();
        Square s = this.getSquare();
        Move m;
        Square end;
        Piece capture;
        for (int i = -2; i < 2; i++) {
            for (int j = -2; j < 2; j++) {
                if (Math.abs(i) == 2 && Math.abs(j) == 1 || Math.abs(j) == 2 && Math.abs(i) == 1) {
                    end = new Square(s.getRow() + i, s.getColumn() + j);
                    capture = b.getPiece(end);
                    if (capture != null && this.sameColor(capture)) {
                        m = new Move(this.s, end, b, this.color);
                        if (m.isLegalMove() && (!checkForChecks || !m.isCheck())) {
                            l.add(m);
                        }
                    }
                }

            }
        }
        return l;
    }
}
