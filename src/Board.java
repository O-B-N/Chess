import java.util.*;

/**
 * a class to create a playable chessboard
 */
public class Board {
    private final Piece[][] a;
    private ArrayList<Move> moveHistory = new ArrayList<>();
    private Map<String, Integer> history = new HashMap<>();
    private final String[] castleRights = {"", "", "", ""};
    private final int[] rookFiles;
    private Square enPassant = null;
    private Move lastMove = null;
    private boolean color = true;
    private int pey = 0;
    private int fiftyMovesTrack = 0;
    boolean threeChecks = false;
    private int whiteChecks = 0;
    private int blackChecks = 0;
    String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0";

    /**
     * creates a new board from a piece array and castling files and the castling right array
     *
     * @param a         the array of the pieces
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
     * creates a board from a given fen and some information
     *
     * @param fen         the fen of the board
     * @param lastMove    last move played
     * @param rookFiles   as an int array of the files
     * @param history     for three-fold repetition
     * @param moveHistory list of the previous moves played
     */
    public Board(String fen, Move lastMove, int[] rookFiles, Map<String, Integer> history, ArrayList<Move> moveHistory) {
        this.lastMove = lastMove;
        this.rookFiles = rookFiles;
        this.history = history;
        this.moveHistory = moveHistory;
        this.a = new Piece[8][8];
        String[] parts = fen.split(" ");
        if(parts.length == 5) {
            System.out.println(fen);
        }
        String[] ranks = parts[0].split("/");
        int index;
        for (int rank = 0; rank < 8; rank++) {
            char[] row = ranks[rank].toCharArray();
            index = 0;
            for (int file = 0; file < 8; file++) {
                char c = row[index];
                boolean color = Character.isUpperCase(c);
                if (c > '0' && c < '9') {
                    this.a[7 - rank][file] = null;
                    if (c > '1') {
                        row[index]--;
                    } else {
                        index++;
                    }
                } else {
                    switch (c) {
                        case 'r', 'R' -> this.a[7 - rank][file] = new Rook(color);
                        case 'b', 'B' -> this.a[7 - rank][file] = new Bishop(color);
                        case 'n', 'N' -> this.a[7 - rank][file] = new Knight(color);
                        case 'k', 'K' -> this.a[7 - rank][file] = new King(color);
                        case 'q', 'Q' -> this.a[7 - rank][file] = new Queen(color);
                        case 'p', 'P' -> this.a[7 - rank][file] = new Pawn(color);
                    }
                    index++;
                }
            }
        }
        this.color = parts[1].equals("w");
        for (char c : parts[2].toCharArray()) {
            switch (c) {
                case 'K' -> this.castleRights[0] = "K";
                case 'Q' -> this.castleRights[1] = "Q";
                case 'k' -> this.castleRights[2] = "k";
                case 'q' -> this.castleRights[3] = "q";
            }
        }
        this.enPassant = Square.create(parts[3]);
        this.fiftyMovesTrack = Integer.parseInt(parts[4]);
        this.pey = Integer.parseInt(parts[5]);
    }

    /**
     * @return the piece's array
     */
    public Piece[][] getArray() {
        return this.a;
    }

    /**
     * @return the castling files as a string
     */
    public String getCastlingString() {
        String[] c = this.castleRights;
        return c[0] + "" + c[1] + "" + c[2] + "" + c[3];
    }

    /**
     * @param rank get the rank of a square
     * @param file get the file of a square
     * @return a piece on the board (could be null?)
     */
    public Piece getPiece(int rank, int file) {
        return this.a[rank][file];
    }

    /**
     * @param s get the s of a square
     * @return a piece in that square (could be null?)
     */
    public Piece getPiece(Square s) {
        return this.a[s.getRank()][s.getFile()];
    }

    /**
     * @param s a square on the board
     * @return true if the square is empty
     */
    public boolean isEmpty(Square s) {
        return getPiece(s) == null;
    }

    /**
     * this method is checking a list of squares and if they are clear to castle through them returns true
     * @param l      a list of squares on the board that need to be checked
     * @param color  true to check for white king safety
     * @param control true if needed to check if the square is attacked
     * @param king      a square where the king is
     * @param rook      a square where the rook is
     * @return true is the squares are clear
     */
    public boolean squaresClear(List<Square> l, boolean color, boolean control, Square king, Square rook) {
        //check if there is a piece in the way
        for (Square s : l) {
            if (this.getPiece(s) != null && !s.equals(king) && !s.equals(rook)) {
              //  System.out.println("blocked");
                return false;
            }
        }
        if(control) {
            for (Move m : this.allMoves(!color, false)) {
                if(m == null || m.getPiece() == null) {
                    System.out.println("error in squaresClear");
                    System.exit(1);
                }
                for (Square s : l) {
                    //checks if the squares are attacked by the other color
                    if (s.equals(m.getEnd())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return the color that is turn to play now, true for white false for black
     */
    public boolean getColor() {
        return this.color;
    }

    /**
     * check is the game should end in the correct game mode
     *
     * @return true if the game should end
     */
    public boolean isGameOver() {
        if (isCheckmate()) {
            System.out.println("Checkmate!");
        } else if (isStalemate()) {
            System.out.println("Stalemate!");
        } else if (this.threeChecks && (this.blackChecks == 3 || this.whiteChecks == 3)) {
            System.out.println("3 checks!");
        } else if (this.isThreefoldRepetition()) {
            System.out.println("Three-fold repetition!");
        } else if (this.fiftyMovesTrack >= 50) {
            System.out.println("50 move rule!");
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return a copy of the board
     */
    public Board copy() {
        return new Board(this.positionToFenWithMoves(), this.lastMove, this.rookFiles, this.history, this.moveHistory);
    }

    /**
     * @param b a second board
     * @return true if the boards are equal (only using fen)
     */
    public boolean equal(Board b) {
        return this.positionToFen().equals(b.positionToFen());
    }

    /**
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
                    fen.append(a[rank][file].toString());
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
        String castling = this.getCastlingString();
        if (castling.equals("")) {
            castling = "-";
        }
        fen.append(castling);
        fen.append(this.enPassant == null ? " -" : " " + this.enPassant);
        return fen.toString();
    }

    /**
     * @return the position in fen with the move numbers
     */
    public String positionToFenWithMoves() {
        return positionToFen() + " " + this.fiftyMovesTrack + " " + this.pey / 2;
    }

    /**
     * update the history map of the position
     *
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
     * @return the result of function noMoves - true if there are no legal moves
     */
    public boolean isStalemate() {
        return noMoves();
    }

    /**
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
     * @return true if it's checkmate
     */
    public boolean isCheckmate() {
        return this.inCheck(this.color) && noMoves();
    }


    /**
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
     * @return the board, chess 960 variation
     */
    public static Board chess960() {
        Random r = new Random();
        List<Integer> integers = new ArrayList<>();
        for (int file = 0; file < 8; file++) {
            integers.add(file);
        }
        int king = r.nextInt(6) + 1, bishop1 = 1, bishop2 = 0, knight1, knight2;
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
        Piece[][] matrix = intsToPieceMatrix(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
        return new Board(matrix, new int[]{Math.min(rook2, rook1), Math.max(rook2, rook1)});
    }

    /**
     * @return a board of a new game
     */
    public static Board newGame() {
        int king = 4, bishop1 = 2, bishop2 = 5, knight1 = 1, knight2 = 6, rook1 = 0, rook2 = 7;
        Piece[][] matrix = intsToPieceMatrix(king, rook1, rook2, bishop1, bishop2, knight1, knight2);
        return new Board(matrix, new int[]{0, 7});
    }

    //change to array or some

    /**
     * creates a new game with files for each piece
     *
     * @param king    's file
     * @param rook1   's file
     * @param rook2   's file
     * @param bishop1 's file
     * @param bishop2 's file
     * @param knight1 's file
     * @param knight2 's file
     * @return the board of the new game
     */
    private static Piece[][] intsToPieceMatrix(int king, int rook1, int rook2, int bishop1,
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
                    a[rank][file] = new Pawn(color);
                } else if (rank == 7 || rank == 0) {
                    if (file == rook1 || file == rook2) {
                        a[rank][file] = new Rook(color);
                    } else if (file == knight1 || file == knight2) {
                        a[rank][file] = new Knight(color);
                    } else if (file == bishop1 || file == bishop2) {
                        a[rank][file] = new Bishop(color);
                    } else if (file == king) {
                        a[rank][file] = new King(color);
                    } else {
                        a[rank][file] = new Queen(color);
                    }
                }
            }
        }
        return a;
    }

    /**
     * make a castling move
     *
     * @param m the castling move
     */
    public void castle(Move m) {
        this.makeHalfMove(m.getCastleKing());
        this.makeHalfMove(m.getCastleRook());
        if (this.color) {
            this.castleRights[0] = "";
            this.castleRights[1] = "";
        } else {
            this.castleRights[2] = "";
            this.castleRights[3] = "";
        }
        this.pey += 1;
        this.color = !this.color;
        this.moveHistory.add(m);
        this.lastMove = m;
        this.enPassant = null;
    }

    /**
     * @param m the move to play
     */
    public boolean makeHalfMove(Move m) {
        Square start = m.getStart();
        Square end = m.getEnd();
        Piece p = this.a[start.getRank()][start.getFile()];
        this.a[end.getRank()][end.getFile()] = p;
        this.a[start.getRank()][start.getFile()] = null;
        if (p == null) {
        //    System.out.println("no piece");
            return false;
        }
        return true;
    }

    /**
     * @param m a move to be played on the board and update stats
     */
    public void makeMove(Move m) {
        if (m.isCastling()) {
            this.castle(m);
            return;
        }
        Piece p = this.a[m.getStart().getRank()][m.getStart().getFile()];
        Square end = m.getEnd();
        if (!makeHalfMove(m)) {
            return;
        }
        if (p instanceof Rook) {
            if (this.rookFiles[0] == m.getStart().getFile()) {
                if (this.color) {
                    this.castleRights[1] = "";
                } else {
                    this.castleRights[3] = "";
                }
            } else if (rookFiles[1] == m.getStart().getFile()) {
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
        }
        if (p instanceof Pawn || m.isCapture() || m.isPromotion() || m.isCastling()) {
            this.fiftyMovesTrack = 0;
        } else {
            this.fiftyMovesTrack++;
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
        this.moveHistory.add(m);
        this.lastMove = m;
        if (this.threeChecks && this.inCheck(this.color)) {
            if (this.color) {
                this.blackChecks++;
            } else {
                this.whiteChecks++;
            }
        }
    }

    /**
     * @param color          true for white false for black
     * @param checkForChecks false to include illegal moves that leave the king in check
     * @return a list of all possible moves for that color
     */
    public List<Move> allMoves(boolean color, boolean checkForChecks) {
        List<Move> moves = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this.a[rank][file] != null && this.a[rank][file].getColor() == color) {
                    moves.addAll(this.a[rank][file].allLegalMoves(this, new Square(rank, file), checkForChecks));
                }
            }
        }
        //if move result in self check remove it
        List<Move> l = new ArrayList<>(moves);
        for (int i = l.size() - 1; i >= 0; i--) {
            if (l.get(i) == null || (checkForChecks && l.get(i).isInCheck(this.getColor()))) {
                moves.remove(i);
            }
        }
        return moves;
    }

    /**
     * @return the last move played
     */
    public Move getLastMove() {
        return this.lastMove;
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
    }

    /**
     * @return the castling files array
     */
    public int[] getRookFiles() {
        return this.rookFiles;
    }

    /**
     * @return the en passant square if there is one (could be null)
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * @return the current pey
     */
    public int getPey() {
        return this.pey;
    }
}