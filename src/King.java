import java.util.ArrayList;
import java.util.List;

public class King extends Piece implements Moved {
    boolean moved = false;

    //castling!!!!!

    public King(boolean color,  Square s) {
        super(color, 'K', s);
    }

    public King(boolean color,  Square s, boolean moved) {
        super(color, 'K', s);
        this.moved = moved;
    }

    @Override
    public Piece copy() {
        return new King(this.color, this.s, this.moved);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> l = new ArrayList<>();
        Piece temp;
        Move m;
        Square s;
        int newRow, newColumn;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newRow = this.s.getRow() - 1 + i;
                newColumn = this.s.getColumn() - 1 + i;
                temp = b.getArray()[newRow][newColumn];
                s = Square.create(newRow, newColumn);
                if (s != null && (!s.equal(this.s)) && (temp == null || this.sameColor(temp))) {
                    m = new Move(this.s, s, b, this.color);
                    if (m.isLegalMove() && (!checkForChecks || !m.isCheck())) {
                        l.add(m);
                    }
                }
            }
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

    public void castling() {
    if (this.moved) {
        System.out.println("king moved already");
        return;
    }
    }
}