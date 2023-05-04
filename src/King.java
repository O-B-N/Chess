import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * king class
 */
public class King extends Piece {
    /**
     * creates a queen piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public King(boolean color, Square s) {
        super(color, 'K', s, 10000);
    }

    /**
     * creates a queen piece
     * @param color of the piece
     * @param s the square of the piece
     * @param moved ture if the king had moved
     */
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
      //  System.out.println("starting king moves");
        List<Move> moves = new ArrayList<>();
        Piece capture;
        Move m;
        Square end;
        int newRow, newColumn;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                newRow = this.s.getRank() + i;
                newColumn = this.s.getFile() + j;
                end = Square.create(newRow, newColumn);
                if (end != null && !end.equal(this.s)) {
                    capture = b.getPiece(end);
                    m = new Move(this.s, end, b, this.color);
                    if ((capture == null || !this.sameColor(capture)) && (!checkForChecks || !m.isInCheck(this.color))) {
                       // System.out.println("king move added: " + m);
                        moves.add(m);
                    }
                }
            }
        }
     //   System.out.println("finished regular king moves");
        if (checkForChecks) {
            for (char c : b.getCastlingString().toCharArray()) {
                if ((c == 'k' || c == 'K') && Character.isUpperCase(c) == this.color) {
                   // System.out.println("king side");
                    moves.add(this.castling(b, true));
                }
                if ((c == 'q' || c == 'Q') && Character.isUpperCase(c) == this.color) {
                   // System.out.println("queen side");
                    moves.add(this.castling(b, true));
                }
                //           System.out.println("done");
            }
        }
  //      System.out.println("finished king moves");
        return moves;
    }

    @Override
    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.moved == ((King) p).wasMoved()
                && this.s.equal(p.getSquare()));
    }

    @Override
    public int move(Square s) {
        this.s = s;
        this.moved = true;
        return this.color ? 3 : -3;
    }

    /*
    //add not while in check
    public Move castling(Board b, boolean kingSide) {
        Piece[][] a = b.getArray();
        int i = this.s.getRank(), j = 7, first = 6, second = 5;
        if (!kingSide) {
            j = 0;
            first -= 3;
            second -= 3;
            if (a[i][1] != null) {
      //          System.out.println(new Square(i, 1)  + " is not clear");
                return null;
            }
        }
        Piece rook = a[i][j];
        List<Move> moves = b.allMoves(!this.color, false);
        //if the rook hasn't moved and in the corner and the squers between are clear
        if (rook instanceof Rook && !((Rook)rook).wasMoved() && a[i][first] == null && a[i][second] == null) {
     //       System.out.println(Arrays.toString(b.getArray()[rook.getSquare().getRank()]));
            //check if the squares are attacted
            for (Move m : moves) {
                if (m != null) {
                    Square end = m.getEnd();
                    if (end.getRank() == i && (end.getFile() == first || end.getFile() == second)) {
        //                System.out.println(new Square(i, first) + " or "  + new Square(i, second) + " is not clear " + m);
                        return null;
                    }
                }
            }
                return Move.createCastlingMove(kingSide, b, color, this.s, rook.getSquare());
        }
    //     System.out.println("rook on " + new Square(i, j) + " moved, or there are stuff in the way or just not");
        return null;
    }
    */
    private Move castling(Board b, boolean kingSide) {
        int[] files = b.getCastlingFiles();
        Square current;
        Piece temp;
        int rank = this.s.getRank(), kingFinal = 7, file = this.s.getFile(), rookFinal = 5;
        if (!kingSide) {
            kingFinal = 2;
            rookFinal = 3;
        }
        int direction = file < kingFinal ? 1 : -1;
        if (!b.isEmpty(new Square(rank, rookFinal)) && !b.getPiece(new Square(rank, rookFinal)).equal(this)) {
          //  System.out.println("rookFinal not empty" + rookFinal);
            return null;
        }
        file += direction;
        do {
            current = Square.create(rank, file);
            if (current == null) {
               // System.out.println("no rook");
                return null;
            }
            temp = b.getPiece(current);
            if (temp instanceof Rook && temp.color == this.color && file == files[0] || file == files[1]) {
                break;
            } else if (temp != null || b.Controlled(current, !this.color, true) != 0) {
              //  System.out.println("controlled or not empty");
                return null;
            }
            file += direction;
        } while (!(temp instanceof Rook) && !(file > 7 || file < 0));
        if (temp instanceof Rook && temp.getColor() == this.color) {
            return Move.createCastlingMove(kingSide, b, this.color, this.s, temp.getSquare());
        }
    return null;
    }
}