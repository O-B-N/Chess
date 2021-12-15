import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Board {
    private Piece[][] a;
    private Move lastMove = null;
    private boolean color = true;
    private double pey = 0;
    private boolean gameOver = false;
    private ArrayList<Piece[][]> arrays = new ArrayList<>();
    private ArrayList<String> history = new ArrayList<>();
    private String currentFen = null;
    private double fiftyMovesTrack;
    private ArrayList<Move> movesHistory = new ArrayList<>();

    public Board(Piece[][] a) {
        this.a = a;
        this.arrays.add(this.a);
    }

    public Board(Piece[][] a, Move lastMove, boolean color, double turn, ArrayList<Piece[][]> arrays) {
        this.a = a;
        this.lastMove = lastMove;
        this.color = color;
        this.pey = turn;
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
        if (isCheckmate()) {
            System.out.println("checkmate!");
            return true;
        } else if (isStalemate()) {
            System.out.println("stalemate!");
            return true;
        }
        return false;
    }

    public Board copy() {
        Piece[][] array = new Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file] != null) {
                    array[rank][file] = this.a[rank][file].copy();
                }
            }
        }
        return new Board(array, this.lastMove, this.color, this.pey, this.arrays);
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
        return noMoves();
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
        return this.inCheck() && noMoves();
    }


    public boolean noMoves() {
        List<Move> moves = this.allMoves(this.color, true);
        for (Move m : moves) {
            if (m != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isFiftyMoves() {
        return fiftyMovesTrack == 50;
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
        while (bishop1 % 2 == 1 || !integers.contains(bishop1)) {
            bishop1 = r.nextInt(7);
        }
        integers.remove(bishop1);
        while (bishop2 % 2 == 1 || !integers.contains(bishop2)) {
            bishop2 = r.nextInt(7);
        }
        integers.remove(bishop2);
        while (!integers.contains(knight1)) {
            knight1 = r.nextInt(7);
        }
        integers.remove(knight1);
        ;
        while (!integers.contains(knight2)) {
            knight2 = r.nextInt(7);
        }
        integers.remove(knight2);
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    public static Board newGame() {
        int king = 4, bishop1 = 2, bishop2 = 5, knight1 = 1, knight2 = 6, rook1 = 0, rook2 = 7;
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    public static Board newGame(int king, int rook1, int rook2, int bishop1,
                                int bishop2, int knight1, int knight2) {
        Piece[][] a = new Piece[8][8];
        boolean color = true;
        for (int rank = 0; rank < 8; rank++) {
            if (rank > 3) {
                color = false;
            }
            for (int file = 0; file < 8; file++) {
                if (rank > 1 && rank < 6) {
                    a[rank][file] = null;
                }
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
     //  System.out.println("making move: " + m);
        if (m.isCastling()) {
            this.castle(m);
        } else {
            Square start = m.getStart();
            Square end = m.getEnd();
       //     System.out.println(start + " " + end);
            Piece p = this.a[start.getRank()][start.getFile()];
            if (p == null) {
                System.out.println("no piece " + m);
                return;
            }
            this.a[end.getRank()][end.getFile()] = p;
            if (m.isPromotion()) {
                this.a[end.getRank()][end.getFile()] = m.getPromoted();
            }
            this.a[start.getRank()][start.getFile()] = null;
            if (m.isEnPassant()) {
                System.out.println("En croissant!!");
                this.a[end.getRank() + (p.getColor() ? -1 : 1)][end.getFile()] = null;
            }
            p.move(end);
            this.pey += 1;
            this.color = !this.color;
            this.movesHistory.add(m);
            this.lastMove = m;
        }
    }

    public void castle(Move m) {
        this.makeMove(m.getCastleKing());
        this.makeMove(m.getCastleRook());
        this.pey -= 1;
        this.color = !this.color;
        this.lastMove = m;
    }

    public List<Move> allMoves(boolean c, boolean checkForChecks) {
   //     System.out.println("all moves, checking for checks: " + checkForChecks);
        if (this.lastMove != null) {
     //       System.out.println("last move: " + this.lastMove);
        }
        List<Move> moves = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file] != null && this.a[rank][file].getColor() == c) {
    //                System.out.println("piece in " + new Square(rank, file) + " is a " + this.a[rank][file]);
                    List<Move> temp = this.a[rank][file].allLegalMoves(this, checkForChecks);
    //                System.out.println("added: " + temp);
                    moves.addAll(temp);
    //                System.out.println("finished with " + new Square(rank, file) + " is a " + this.a[rank][file]);
                }
            }
        }
        //if move leave in check remove
       List<Move> l = new ArrayList<>(moves);
       for (int i = l.size() - 1; i >= 0; i--) {
           if (l.get(i) == null || (checkForChecks && l.get(i).isInCheck())) {
               moves.remove(i);
           }
       }
    //    System.out.println("all moves: " + moves);
        return moves;
    }

    public Move getLastMove() {
        return this.lastMove;
    }
                //int x = (this.color ? 0 : 1) * 7;
    public void runTest() {
        boolean pvAi = false;
        Random r = new Random();
        System.out.println("test started");
        if (false) {
            for (int i = 0; i < 8; i++) {
                Piece p = this.getPiece(0, i);
                if (!(p instanceof Rook || p instanceof King)) {
                    this.getArray()[0][i] = null;
                }
            }
            this.getArray()[1][4] = null;
            this.getArray()[1][3] = null;
            this.getArray()[6][4] = null;
            this.getArray()[6][3] = null;
        }
        this.printBoard();
        List<Move> moves;
        Scanner scan = new Scanner(System.in);
        int index;
        while (!this.isGameOver()) {
            moves = this.allMoves(this.getColor(), true);
            for (Move move : moves) {
                if (move != null) {
                    System.out.println("move " + (moves.indexOf(move) + 1) + ": " + move);
                }
            }
         //   if (this.color && (pvAi || (int)(pey) % 10 == 0)) {
            if (this.color) {
                index = 0;
                System.out.println("choose an index: ");
                while (index < 1 || index > moves.size()) {
                    if (scan.hasNextInt()) {
                        index = scan.nextInt();
                    }
                //        if (index == -1) {
                //            System.out.println(this.movesHistory);
                //        }
                    }
            } else {
                index = r.nextInt(moves.size()) + 1;
            }
            Move m = moves.get(index - 1);
            this.makeMove(m);
            this.printBoard();
            System.out.println("move made: " + m + '\n' + "pey: " + this.pey);
            if (this.getColor()) {
                System.out.println("white turn");
            } else {
                System.out.println("black turn");
            }
        }
    }

    public void putPiece(Piece p, int rank, int file) {
        this.a[rank][file] = p;
    }

    public void printBoard() {
        System.out.println("printing:");
        for (int i = 7; i >= 0; i--) {
              for (int j = 0; j < 8; j++) {
                  if (a[i][j] == null) {
                      System.out.printf("0 ");
                  } else {
                      System.out.printf(a[i][j] + " ");
                  }
              }
            System.out.println(" ");
        }
        /*
        for (int i = 0; i < 8; i++) {
            System.out.println(a[i][0] + " " + a[i][1] + " " + a[i][2] + " " + a[i][3] + " " + a[i][4] + " " + a[i][5] + " " + a[i][6] + " " + a[i][7]);
          //  for (int j = 0; j < 8; j++) {
          //      System.out.println("s: " + new Square(i, j));
          //      if (this.a[i][j] != null) {
          //          System.out.println("p: " + a[i][j]);
          //      }
          //  }
        }

         */
    }


    //remove pawn after en passant and notation

    public static void simpleTest() {
        Piece[][] a = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                a[i][j] = null;
            }
        }
        a[4][4] = new Pawn(false, new Square(4, 4));
        a[1][7] = new Queen(true, new Square(1, 7));
        a[2][1] = new King(true, new Square(2, 1));
        a[0][0] = new King(false, new Square(0, 0));
        Board test = new Board(a, null, true, 0, new ArrayList<>());
        test.runTest();
        /*
        System.out.println("test started");
        test.printBoard();
        List<Move> moves;
        Scanner scan = new Scanner(System.in);
        int index = 0;
        while (!test.isGameOver()) {
            moves = test.allMoves(test.getColor(), true);
            for (Move m : moves) {
                if (m != null) {
                    System.out.println("move " + (moves.indexOf(m) + 1) + ": " + m);
                }
            }
            Move m;
            if (test.getColor()) {
                System.out.println("choose an index:");

                while (!scan.hasNextInt() || scan.nextInt() < 0 || scan.nextInt() > moves.size()) {
                    scan.nextLine();
                }
                index = scan.nextInt();
                System.out.println("index: " + index);
                m = moves.get(index - 1);
            } else {
                Random r = new Random();
                m = moves.get(r.nextInt(moves.size() + 1));
            }
            System.out.println("move chosen: " + m);
            test.printBoard();
            test.makeMove(m);
            test.printBoard();
            System.out.println("move made: " + m + '\n' + "pey: " + test.pey);
            if (test.getColor()) {
                System.out.println("white turn");
            } else {
                System.out.println("black turn");
            }
        }

         */
    }
}