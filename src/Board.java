import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Board {
    Piece[][] a;
    Move lastMove = null;
    boolean color = true;
    double turn = 0;
    boolean gameOver = false;
    ArrayList<Piece[][]> l;

    public Board(Piece[][] a) {
        this.a = a;
    }

    public Board(Piece[][] a, Move lastMove, boolean color, double turn, ArrayList<Piece[][]> l) {
        this.a = a;
        this.lastMove = lastMove;
        this.color = color;
        this.turn = turn;
        this.l = l;
    }

    public Piece[][] getArray() {
        return this.a;
    }

    public Piece getPiece(int i, int j) {
        return this.a[i][j];
    }

    public Piece getPiece(Square s) {
        return this.a[s.getRow()][s.getColumn()];
    }

    public boolean getColor() {
        return color;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Board copy() {
        Piece[][] a = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                a[i][j] = this.a[i][j].copy();
            }
        }
        return new Board(a, this.lastMove, this.color, this.turn, this.l);
    }

    public boolean equal(Board b) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!b.getArray()[i][j].equal(this.a[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Board newGame() {
        Piece[][] a = new Piece[8][8];
        boolean color = true;
        for (int i = 0; i < 8; i++) {
            if (i > 3) {
                color = false;
            }
            for (int j = 0; j < 8; j++) {
                if (i == 1 || i == 6) {
                    a[i][j] = new Pawn(color, new Square(i, j));
                } else if (i == 7 || i == 0) {
                    if (j == 0 || j == 7) {
                        a[i][j] = new Rook(color, new Square(i, j));
                    } else if (j == 1 || j == 6) {
                        a[i][j] = new Knight(color, new Square(i, j));
                    } else if (j == 2 || j == 5) {
                        a[i][j] = new Bishop(color, new Square(i, j));
                    } else if (j == 4) {
                        a[i][j] = new King(color, new Square(i, j));
                    } else {
                        a[i][j] = new Queen(color, new Square(i, j));
                    }
                }
            }
        }
        return new Board(a);
    }

    public void makeMove(Move m) {
        if (m.isCastling()) {
            this.castle(m);
            return;
        }
        Square start = m.getStart();
        Square end = m.getEnd();
        Piece p = this.a[start.getRow()][start.getColumn()];
        this.a[end.getRow()][end.getColumn()] = p;
        this.a[start.getRow()][start.getColumn()] = null;
        p.move(start);
        turn += 0.5;
        this.color = !this.color;
        this.lastMove = m;
    }

    public void castle(Move m) {

    }

    public List<Move> allMoves(boolean color, boolean checkForChecks) {
        List<Move> l = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (this.a[i][j].getColor() == color) {
                    l.addAll(this.a[i][j].allLegalMoves(this, checkForChecks));
                }
            }
        }
        return l;
    }
}