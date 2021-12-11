import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private Piece[][] a;
    private Move lastMove = null;
    private boolean color = true;
    private double turn = 0;
    private boolean gameOver = false;
    private ArrayList<Piece[][]> arrays = new ArrayList<>();
    private ArrayList<String> history = new ArrayList<>();
    private String currentFen = null;
    private double fiftyMovesTrack;

    public Board(Piece[][] a) {
        this.a = a;
        this.arrays.add(this.a);
    }

    public Board(Piece[][] a, Move lastMove, boolean color, double turn, ArrayList<Piece[][]> arrays) {
        this.a = a;
        this.lastMove = lastMove;
        this.color = color;
        this.turn = turn;
        this.arrays = arrays;
    }

    public Piece[][] getArray() {
        return this.a;
    }

    public Piece getPiece(int rank, int file) {
        return this.a[rank][file];
    }

    public Piece getPiece(Square s) {
        return this.a[s.getRank()][s.getFile()];
    }

    public boolean getColor() {
        return color;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Board copy() {
        Piece[][] a = new Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                a[rank][file] = this.a[rank][file].copy();
            }
        }
        return new Board(a, this.lastMove, this.color, this.turn, this.arrays);
    }

    public boolean equal(Board b) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (!b.getArray()[rank][file].equal(this.a[rank][file])) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isThreefoldRepetition() {
        int repetitions = 0;
        for (String f : this.history) {
            if (f.equals(this.currentFen)) {
                repetitions++;
            }
        }
        this.history.add(this.currentFen);
        return repetitions == 3;
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
        List<Move> moves = this.allMoves(this.color, true);
        for (Move m : moves) {
            if (m != null) {
                return false;
            }
        }
        return true;
    }

    public static Board chess960() {
        Random r = new Random();
        List<Integer> integers = new ArrayList<>();
        for (int file = 0; file < 8; file++) {
            integers.add(file);
        }
        int king = r.nextInt(6) + 1, bishop1 = 1, bishop2 = 0, knight1 = 10, knight2 = 10;
        integers.remove(king);
        int rook1 = r.nextInt(king);
        integers.remove(rook1);
        int rook2 = r.nextInt(7 - king) + king;
        integers.remove(rook2);
        while (bishop1 % 2 == 1 || !integers.contains(bishop1) ) {
            bishop1 = r.nextInt(7);
        }
        integers.remove(bishop1);
        while (bishop2 % 2 == 1 || !integers.contains(bishop2) ) {
            bishop2 = r.nextInt(7);
        }
        integers.remove(bishop2);
        while (!integers.contains(knight1) ) {
            knight1 = r.nextInt(7);
        }
        integers.remove(knight1);;
        while (!integers.contains(knight2) ) {
            knight2 = r.nextInt(7);
        }
        integers.remove(knight2);
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    public static Board newGame() {
        int king = 4, bishop1 = 2, bishop2 = 5, knight1 = 1, knight2 = 6, rook1 = 0, rook2 = 7;
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    public static Board newGame(int king,  int rook1,  int rook2, int bishop1,
                               int bishop2,  int knight1,  int knight2)  {
        Piece[][] a = new Piece[8][8];
        boolean color = true;
        for (int rank = 0; rank < 8; rank++) {
            if (rank > 3) {
                color = false;
            }
            for (int file = 0; file < 8; file++) {
                if (rank == 1 || rank == 6) {
                    a[rank][file] = new Pawn(color, new Square(rank, file));
                } else if (rank == 7 || rank == 0) {
                    if (file == rook1 || file == rook2) {
                        a[rank][file] = new Rook(color, new Square(rank, file));
                    } else if (file == knight1 || file == knight2) {
                        a[rank][file] = new Knight(color, new Square(rank, file));
                    } else if (file == bishop1 || file == bishop2) {
                        a[rank][file] = new Bishop(color, new Square(rank, file));
                    } else if (file == king) {
                        a[rank][file] = new King(color, new Square(rank, file));
                    } else {
                        a[rank][file] = new Queen(color, new Square(rank, file));
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
        } else if (m.isPromotion()) {

        }
        Square start = m.getStart();
        Square end = m.getEnd();
        Piece p = this.a[start.getRank()][start.getFile()];
        this.a[end.getRank()][end.getFile()] = p;
        if (m.isPromotion()) {
            this.a[end.getRank()][end.getFile()] = m.getPromoted();
        }
        this.a[start.getRank()][start.getFile()] = null;
        if (m.isEnPassant()) {
            this.a[end.getRank() + (this.color ? 1 : -1)][end.getFile()] = null;
        }
        //update p.s timePassed(b)
        p.move(start);
        this.turn += 0.5;
        this.color = !this.color;
        this.lastMove = m;
        this.arrays.add(this.a);
    }

    public void castle(Move m) {
    this.makeMove(m.getCastleKing());
    this.makeMove(m.getCastleRook());
    this.turn -= 0.5;
    this.color = !this.color;
    this.lastMove = m;
    }

    public List<Move> allMoves(boolean color, boolean checkForChecks) {
        List<Move> moves = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file].getColor() == color) {
                    moves.addAll(this.a[rank][file].allLegalMoves(this, checkForChecks));
                }
            }
        }
        return moves;
    }

    public Move getLastMove() {
        return this.lastMove;
    }
}