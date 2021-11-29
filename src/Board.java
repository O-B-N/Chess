import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    Piece[][] a;
    Move lastMove = null;
    boolean color = true;
    double turn = 0;
    boolean gameOver = false;
    ArrayList<Piece[][]> l = new ArrayList<>();

    public Board(Piece[][] a) {
        this.a = a;
        l.add(this.a);
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

    public boolean isThreefoldRepetition() {
        return false;
    }

    public boolean isStalemate() {
        List<Move> l = this.allMoves(this.color, true);
        for (Move m : l) {
            if (m != null) {
                return false;
            }
        }
        return true;
    }

    public boolean inCheck() {
        List<Move> l = this.allMoves(!this.color, false);
        for (Move m : l) {
            if (m.isCapture() && m.getCapture() instanceof King) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckmate() {
        if (!this.inCheck()) {
            return false;
        }
        List<Move> l = this.allMoves(this.color, true);
        for (Move m : l) {
            if (m != null) {
                return false;
            }
        }
        return true;
    }

    public static Board chess960() {
        Random r = new Random();
        int king = r.nextInt(6) + 1;
        int rook1 = r.nextInt(king);
        int rook2 = r.nextInt(7 - king) + king;
        int[] c = new int[8]; //king, rook, rook, bishop, bishop
        return null;
    }

    public static Board newGame() {
        Piece[][] a = new Piece[8][8];
        boolean color = true;
        int king = 4, bishop1 = 2, bishop2 = 5, knight1 = 1, knight2 = 6, rook1 = 0, rook2 = 7;
        for (int i = 0; i < 8; i++) {
            if (i > 3) {
                color = false;
            }
            for (int j = 0; j < 8; j++) {
                if (i == 1 || i == 6) {
                    a[i][j] = new Pawn(color, new Square(i, j));
                } else if (i == 7 || i == 0) {
                    if (j == rook1 || j == rook2) {
                        a[i][j] = new Rook(color, new Square(i, j));
                    } else if (j == knight1 || j == knight2) {
                        a[i][j] = new Knight(color, new Square(i, j));
                    } else if (j == bishop1 || j == bishop2) {
                        a[i][j] = new Bishop(color, new Square(i, j));
                    } else if (j == king) {
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
        this.turn += 0.5;
        this.color = !this.color;
        this.lastMove = m;
        l.add(this.a);
    }

    public void castle(Move m) {
    this.makeMove(m.getCastleKing());
    this.makeMove(m.getCastleRook());
    this.turn -= 0.5;
    this.color = !this.color;
    this.lastMove = m;
    }

    public List<Move> allMoves(boolean color, boolean checkForChecks) {
        List<Move> l = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.a[i][j].getColor() == color) {
                    l.addAll(this.a[i][j].allLegalMoves(this, checkForChecks));
                }
            }
        }
        return l;
    }
}