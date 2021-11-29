import java.util.ArrayList;
import java.util.List;

public class King extends Piece implements Moved {
    boolean moved = false;

    public King(boolean color, Square s) {
        super(color, 'K', s, 0);
    }

    public King(boolean color, Square s, boolean moved) {
        super(color, 'K', s, 0);
        this.moved = moved;
    }

    @Override
    public Piece copy() {
        return new King(this.color, this.s, this.moved);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> l = new ArrayList<>();
        Piece capture;
        Move m;
        Square end;
        int newRow, newColumn;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newRow = this.s.getRow() - 1 + i;
                newColumn = this.s.getColumn() - 1 + i;
                end = Square.create(newRow, newColumn);
                if (end != null && !end.equal(this.s)) {
                    capture = b.getPiece(end);
                    m = new Move(this.s, end, b, this.color);
                    if ((capture == null || !this.sameColor(capture)) && (!checkForChecks || !m.isCheck())) {
                        l.add(m);
                    }
                }
            }
        }
        if (!this.moved) {
            l.add(this.castling(b, true));
            l.add(this.castling(b, false));
        }
        return l;
    }

    @Override
    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.moved == ((King) p).wasMoved()
                && this.s.equal(p.getSquare()));
    }

    public boolean wasMoved() {
        return this.moved;
    }
/*
  //  public boolean inCheck(Board b, Move m) {
  //      b = b.copy();
  //      b.makeMove(m);
  //      List<Move> l = b.allMoves(!this.color, false);
  //      int index = 0;
  //      Move temp = l.get(index);
  //      while (temp != null && temp.isCapture() && temp.getCapture().getType() != 'K') {
  //          index++;
  //          temp = l.get(index);
  //      }
  //      if (temp != null && temp.isCapture() && temp.getCapture().getType() == 'K') {
  //          return true;
  //      }
  //  return true;
  //  }

 */

    @Override
    public void move(Square s) {
        this.s = s;
        this.moved = true;
    }

    public Move castling(Board b, boolean kingSide) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        int i = s.getRow(), j = 0, first = 3, second = 2;
        if (kingSide) {
            j = 7;
            first = 5;
            second = 6;
        }
        Piece rook = a[i][j];
        List<Move> l = b.allMoves(!this.color, false);
        boolean controlled = false;
        if (rook instanceof Rook && !((Rook) rook).wasMoved() && a[i][first] == null && a[i][second] == null) {
            for (Move m : l) {
                if (m != null) {
                    Square end = m.getEnd();
                    if (end.getRow() == i && (end.getColumn() == first || end.getColumn() == second)) {
                        controlled = true;
                    }
                }
            }
            if (!controlled) {
                return new Move(kingSide, b, color, this.s, rook.getSquare());
            }
        }
        return null;
    }
}