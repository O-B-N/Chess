import java.util.*;

/**
 * a class to represent playable chessboard
 */
public class Board {
    private Piece[][] a;
    private Move lastMove = null;
    private boolean color = true;
    private int pey = 0;
    private int fiftyMovesTrack = 0;
    private Square enPassant = null;
    private Map<String, Integer> history = new HashMap<>();
    private ArrayList<Move> movesHistory = new ArrayList<>();
    private  String[] castleRights = {"","","",""};
    private int whiteChecks = 0;
    private int blackChecks = 0;
    private int[] rookFiles;
    boolean threeChecks = false;
    boolean duckChess = false;
    boolean horde = false;


    /**
     * creates a new board from a piece array and castling files and the castling right array
     * @param a the array of the pieces
     * @param rookFiles an int array of the rook files
     */
    public Board(Piece[][] a, int[] rookFiles) {
        this.a = a;
        this.rookFiles = rookFiles;
        this.castleRights[0] = "K";
        this.castleRights[1] = "Q";
        this.castleRights[2] = "k";
        this.castleRights[3] = "q";
    }

    /**
     * creates a board from a given fen
     * @param fen the fen of the board
     * @param lastMove last move played
     * @param rookFiles as an int array of the files
     */
    public Board(String fen, Move lastMove, int[] rookFiles, Map<String, Integer> history, ArrayList<Move> movesHistory) {
        this.lastMove = lastMove;
        this.rookFiles = rookFiles;
        this.history = history;
        this.movesHistory = movesHistory;

        this.a = new Piece[8][8];
        String[] parts = fen.split("\\s+");
        System.out.println(Arrays.toString(parts));
        for (char c : parts[2].toCharArray()) {
            switch (c) {
                case 'K' -> this.castleRights[0] = "K";
                case 'Q' -> this.castleRights[1] = "Q";
                case 'k' -> this.castleRights[2] = "k";
                case 'q' -> this.castleRights[3] = "q";
            }
        }
        String[] ranks = parts[0].split("/");
        int index;
        for (int rank = 0; rank < 8; rank++) {
            char[] row = ranks[rank].toCharArray();
             index = 0;
            for (int file = 0; file < 8; file++) {
                char c = row[index];
                boolean color = Character.isUpperCase(c);
                Square square = new Square(rank, file);
                if (c > '0' && c < '9') {
                    this.a[7 - rank][file] = null;
                    if (c > '1') {
                        row[index]--;
                    } else {
                        index++;
                    }
                } else {
                    switch (c) {
                        case 'r', 'R' -> this.a[7 - rank][file] = new Rook(color, square);
                        case 'b', 'B' -> this.a[7 - rank][file] = new Bishop(color, square);
                        case 'n', 'N' -> this.a[7 - rank][file] = new Knight(color, square);
                        case 'k', 'K' -> this.a[7 - rank][file] = new King(color, square);
                        case 'q', 'Q' -> this.a[7 - rank][file] = new Queen(color, square);
                        case 'p', 'P' -> this.a[7 - rank][file] = new Pawn(color, square);
                    }
                    index++;
                }
            }
        }
        //make board
        this.color = parts[1].equals("w");
        this.enPassant = Square.create(parts[3]);
        this.fiftyMovesTrack = Integer.parseInt(parts[4]);
        this.pey = Integer.parseInt(parts[5]);
    }

    /**
     *
     * @param a pieces array
     * @param lastMove the last move played
     * @param color the color the is turn to play
     * @param pey current pey
     */
    public Board(Piece[][] a, Move lastMove, boolean color, int pey) {
        this.a = a;
        this.lastMove = lastMove;
        this.color = color;
        this.pey = pey;
        this.castleRights[0] = "K";
        this.castleRights[1] = "Q";
        this.castleRights[2] = "k";
        this.castleRights[3] = "q";
    }

    /**
     *
     * @return the piece's array
     */
    public Piece[][] getArray() {
        return this.a;
    }

    /**
     *
     * @return the castling files as a string
     */
    public String getCastlingString() {
        String[] c = this.castleRights;
        return c[0] + "" + c[1] + "" + c[2] + "" + c[3];
    }

    /**
     *
     * @param rank get the rank of a square
     * @param file get the file of a square
     * @return a piece on the board (could be null?)
     */
    public Piece getPiece(int rank, int file) {
        return this.a[rank][file];
    }

    /**
     *
     * @param s get the s of a square
     * @return a piece in that square (could be null?)
     */
    public Piece getPiece(Square s) {
        return this.a[s.getRank()][s.getFile()];
    }

    /**
     *
     * @param s a square on the board
     * @return true if the square is empty
     */
    public boolean isEmpty(Square s) {
        return getPiece(s) == null;
    }

    /**
     *
     * @param s a square on the board
     * @param color true for white, false for black
     * @param castle is it for castling check
     * @return number of times the square is controlled by the color
     */
    public int Controlled(Square s, boolean color, boolean castle) {
        boolean check = !castle;
        int count = 0;
        for (Move m : this.allMoves(color, check)) {
            if(m != null && m.getEnd() != null && s.equal(m.getEnd())) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return the color that is turn to play now, true for white false for black
     */
    public boolean getColor() {
        return color;
    }

    /**
     * check is the game should end in the correct game mode
     * @return true if the game should end
     */
    public boolean isGameOver() {
        if (isCheckmate()) {
            System.out.println("checkmate!");
        } else if (isStalemate()) {
            System.out.println("stalemate!");
        } else if (this.threeChecks && (this.blackChecks == 3 || this.whiteChecks == 3)) {
            System.out.println("3 checks!");
        } else if (this.isThreefoldRepetition()) {
            System.out.println("50 move rule!");
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @return a copy of the board
     */
    public Board copy() {
        Piece[][] array = new Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file] != null) {
                    array[rank][file] = this.a[rank][file].copy();
                }
            }
        }
        return new Board(array, this.lastMove, this.color, this.pey);
    }

    /**
     *
     * @param b a second board
     * @return true if the boards are equal (only arrays)
     */
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

    /**
     *
     * @return the position in fen without the moves
     */
    public String positionToFen() {
        int space = 0;
        StringBuilder fen = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                if (a[rank][file] == null) {
                    space += 1;
                } else {
                    if (space > 0) {
                        fen.append(space);
                        space = 0;
                    }
                    fen.append(a[rank][file].getType());
                }
            }
            if (space > 0) {
                fen.append(space);
                space = 0;
            }
            if (rank != 0) {
                fen.append('/');
            }
        }
        fen.append(this.color ? " w " : " b ");
        String[] c = this.castleRights;
        String castling = c[0] + "" + c[1] + "" + c[2] + "" + c[3];
        if (castling.equals("")) {
            castling = "- ";
        }
        fen.append(castling);
        fen.append(this.enPassant == null ? "-" : this.enPassant.toString());
        return fen.toString();
    }

    /**
     *
     * @return the position in fen with the move numbers
     */
    public String positionToFenWithMoves() {
        return positionToFen() + " " + " " + this.fiftyMovesTrack + this.pey / 2;
    }

    /**
     * update the history map of the position
     * @return true if the position repeated 3 times. otherwise false
     */
    public boolean isThreefoldRepetition() {
        String currentFen = positionToFen();
        if (this.history.containsKey(currentFen)) {
            this.history.put(currentFen, this.history.get(currentFen) + 1);
            } else {
            this.history.put(currentFen, 0);
        }
        return this.history.get(currentFen) == 3;
    }

    /**
     *
     * @return the result of function noMoves - true if there are no legal moves
     */
    public boolean isStalemate() {
        return noMoves();
    }

    /**
     *
     * @param color the color to check
     * @return true if the king of that color is in check
     */
    public boolean inCheck(boolean color) {
        for (Move m : this.allMoves(!color, false)) {
            if (m.isCapture() && m.getCapture() instanceof King) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true if it's checkmate
     */
    public boolean isCheckmate() {
        return this.inCheck(this.color) && noMoves();
    }


    /**
     *
     * @return true if there are no legal moves in the position
     */
    public boolean noMoves() {
        List<Move> moves = this.allMoves(this.color, true);
        for (Move m : moves) {
            if (m != null) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return true if it's 50 moves rule
     */
    private boolean isFiftyMoves() {
        return fiftyMovesTrack == 50;
    }

    /**
     *
     * @return the board, chess 960 variation
     */
    public static Board chess960() {
        Random r = new Random();
        List<Integer> integers = new ArrayList<>();
        for (int file = 0; file < 8; file++) {
            integers.add(file);
        }
        Integer king = r.nextInt(6) + 1, bishop1 = 1, bishop2 = 0, knight1, knight2 = 10;
        integers.remove(king);
        Integer rook1 = r.nextInt(king);
        integers.remove(rook1);
        Integer rook2 = r.nextInt(7 - king) + king + 1;
        integers.remove(rook2);
        while (bishop1 % 2 == 1 || !integers.contains(bishop1)) {
            bishop1 = r.nextInt(8);
        }
        integers.remove(bishop1);
        while (bishop2 % 2 == 0 || !integers.contains(bishop2)) {
            bishop2 = r.nextInt(8);
        }
        integers.remove(bishop2);
        do {
            knight1 = r.nextInt(7);
        }
        while (!integers.contains(knight1));
        integers.remove(knight1);
        do {
            knight2 = r.nextInt(7);
        }
        while (!integers.contains(knight2));
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    /**
     *
     * @return a board of a new game
     */
    public static Board newGame() {
        int king = 4, bishop1 = 2, bishop2 = 5, knight1 = 1, knight2 = 6, rook1 = 0, rook2 = 7;
        return newGame(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
    }

    //change to array or some
    /**
     * creates a new game with files for each piece
     * @param king 's file
     * @param rook1 's file
     * @param rook2 's file
     * @param bishop1 's file
     * @param bishop2 's file
     * @param knight1 's file
     * @param knight2 's file
     * @return the board of the new game
     */
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
        return new Board(a, new int[]{rook1, rook2});
    }

    /**
     *
     * @param m a move to be played on the board and update stats
     *
     */
    public void makeMove(Move m) {
        if (m.isCastling()) {
            this.castle(m);
            return;
        }
        Piece p = this.a[m.getStart().getRank()][m.getStart().getFile()];
        Square end = m.getEnd();
        if(!makeHalfMove(m)) {
            return;
        }
        if (p instanceof Rook) {
            if (rookFiles[0] == p.getSquare().getFile()) {
                if (this.color) {
                    this.castleRights[1] = "";
                } else {
                    this.castleRights[3] = "";
                }
            } else if (rookFiles[1] == p.getSquare().getFile()) {
                if (this.color) {
                    this.castleRights[0] = "";
                } else {
                    this.castleRights[2] = "";
                }
            }
        } else if (p instanceof King) {
            if (this.color) {
                this.castleRights[0] = "";
                this.castleRights[1] = "";
            } else {
                this.castleRights[2] = "";
                this.castleRights[3] = "";
            }
        } else if (p instanceof Pawn || m.isCapture() || m.isPromotion()) {
            this.fiftyMovesTrack = 0;
        }
        if (m.isPromotion()) {
            this.a[end.getRank()][end.getFile()] = m.getPromoted();
        }
        if (m.isEnPassant()) {
            //System.out.println("En croissant!!");
            this.a[end.getRank() + (p.getColor() ? -1 : 1)][end.getFile()] = null;
        }
        if (m.isDoublePawnPush()) {
            this.enPassant = new Square(m.getEnd().getRank() + (this.color ? -1 : 1), m.getEnd().getFile());
        } else {
            this.enPassant = null;
        }
        this.pey += 1;
        this.color = !this.color;
        this.movesHistory.add(m);
        this.lastMove = m;
        return;
    }

    /**
     *
     * @param m the move to play
     */
    public boolean makeHalfMove(Move m) {
        Square start = m.getStart();
        Square end = m.getEnd();
        Piece p = this.a[start.getRank()][start.getFile()];
        this.a[end.getRank()][end.getFile()] = p;
        this.a[start.getRank()][start.getFile()] = null;
        if (p == null) {
            System.out.println("no piece");
            return false;
        }
        p.updateSquare(end);
        return true;
    }

    /**
     * make a castling move
     * @param m the castling move
     */
    public void castle(Move m) {
        this.makeHalfMove(m.getCastleKing());
        this.makeHalfMove(m.getCastleRook());
        if(this.color) {
            this.castleRights[0] = "";
            this.castleRights[1] = "";
        } else {
            this.castleRights[2] = "";
            this.castleRights[3] = "";
        }
        this.pey += 1;
        this.color = !this.color;
        this.movesHistory.add(m);
        this.lastMove = m;
        this.enPassant = null;
    }

    /**
     *
     * @param color true for white false for black
     * @param checkForChecks false to include illegal moves that leave the king in check
     * @return a list of all possible moves for that color
     */
    public List<Move> allMoves(boolean color, boolean checkForChecks) {
        this.printBoard();
        List<Move> moves = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file] != null && this.a[rank][file].getColor() == color) {
                    List<Move> temp = this.a[rank][file].allLegalMoves(this, checkForChecks);
                    moves.addAll(temp);
                }
            }
        }
        //if move leave in check remove
       List<Move> l = new ArrayList<>(moves);
       for (int i = l.size() - 1; i >= 0; i--) {
           if (l.get(i) == null || (checkForChecks && l.get(i).isInCheck(this.getColor()))) {
               moves.remove(i);
           }
       }
        return moves;
    }

    /**
     *
     * @return the last move played
     */
    public Move getLastMove() {
        return this.lastMove;
    }

    /**
     * a test function
     */
    public void runTest() {
        boolean pvAi = false;
        Random r = new Random();
        System.out.println("test started");
        if (pvAi) {
            for (int i = 0; i < 8; i++) {
                Piece p = this.getPiece(0, i);
                if (!(p instanceof Rook || p instanceof King)) {
                    this.getArray()[0][i] = null;
                }
            }
            Piece temp;
            this.getArray()[1][4] = null;
            this.getArray()[1][3] = null;
            this.getArray()[6][4] = null;
            this.getArray()[6][3] = null;
            for (int rank = 0; rank < 8; rank += 7) {
                for (int file = 0; file < 8; file++) {
                    temp = this.a[rank][file];
                    if (!(temp instanceof Rook || temp instanceof King)) {
                        this.a[rank][file] = null;
                    }
                }
            }
        }
        this.printBoard();
        List<Move> moves;
        Scanner scan = new Scanner(System.in);
        int index;
        while (!this.isGameOver()) {
            moves = this.allMoves(this.getColor(), true);
         //   if (this.color && (pvAi || (int)(pey) % 10 == 0)) {
            if (this.color) {
                index = 0;
                System.out.println("or -1 for printing the board\nchoose an index:");
                while (index < 1 || index > moves.size()) {
                    if (scan.hasNextInt()) {
                        index = scan.nextInt();
                    }
                        if (index == -1) {
                                this.printBoard();
                        }
                    }
            } else {
                index = r.nextInt(moves.size()) + 1;
            }
            Move m = moves.get(index - 1);
            this.makeMove(m);
            if (this.threeChecks && m.toString().contains("+")) {
                if (this.color) {
                    this.blackChecks++;
                } else {
                    this.whiteChecks++;
                }
            }
            System.out.println("move made: " + m + '\n' + "pey: " + this.pey);
            if (this.getColor()) {
                System.out.println("white turn");
            } else {
                System.out.println("black turn");
            }
        }
    }

    /**
     *
     * @param p a piece to be placed (already has color)
     * @param rank the rank of the piece
     * @param file the file of the piece
     */
    public void putPiece(Piece p, int rank, int file) {
        this.a[rank][file] = p;
    }

    /**
     * print the current board to the terminal
     */
    public void printBoard() {
        System.out.println("printing:");
        for (int rank = 7; rank >= 0; rank--) {
              for (int file = 0; file < 8; file++) {
                  if (a[rank][file] == null) {
                      System.out.print("0 ");
                  } else {
                      System.out.printf(a[rank][file] + " ");
                  }
              }
            System.out.println(" ");
        }
        /*
        for (int i = 0; i < 8; i++) {
            System.out.println(a[rank][0] + " " + a[rank][1] + " " + a[rank][2] + " " + a[rank][3] + " " + a[rank][4] + " " + a[rank][5] + " " + a[rank][6] + " " + a[rank][7]);
          //  for (int file = 0; file < 8; file++) {
          //      System.out.println("s: " + new Square(rank, file));
          //      if (this.a[rank][file] != null) {
          //          System.out.println("p: " + a[rank][file]);
          //      }
          //  }
        }

         */
    }


    //remove pawn after en passant and notation

    /**
     * a simple test function
     */
    public static void simpleTest() {
        Piece[][] a = new Piece[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                a[rank][file] = null;
            }
        }
        a[4][4] = new Pawn(false, new Square(4, 4));
        a[1][7] = new Queen(true, new Square(1, 7));
        a[2][1] = new King(true, new Square(2, 1));
        a[0][0] = new King(false, new Square(0, 0));
        Board test = new Board(a, null, true, 0);
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

    /**
     *
     * @return the castling files array
     */
    public int[] getRookFiles() {
            return this.rookFiles;
    }

    /**
     *
     * @return the en passant square if there is one (could be null)
     */
    public Square getEnPassant() {
        return enPassant;
    }
}