import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * an abstract class to create all the pieces on the chessboard
 */
abstract class Piece {
    protected boolean color;
    protected char type;
    protected int value;


    /**
     *  creates a piece
     * @param color the color of the piece
     * @param type the type of the piece
     * @param value piece value
     */
    public Piece(boolean color, char type, int value) {
        this.color = color;
        if (!color) {
            String r = (type + "").toLowerCase(Locale.ROOT);
            type = r.toCharArray()[0];
        }
        this.type = type;
        this.value = value;
    }

    /**
     *
     * @param b a board to check in
     * @param checkForChecks false to include illegal moves that leave the king in check
     * @return a list of all legal moves
     */
    public abstract List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks);

    /**
     *
     * @return a copy of the piece
     * maybe can be shared q b n?
     */
    public abstract Piece copy();

    /**
     *
     * @return the color of the piece
     */
    public boolean getColor() {
        return color;
    }

    /**
     *
     * @param p a piece to compare
     * @return true if the pieces are the same color
     */
    public boolean differentColor(Piece p) {
        return p.getColor() != this.color;
    }

    /**
     *
     * @param b on the board b
     * @param s the start square
     * @return a list of all the legal straight moves
     */
    public List<Move> allStraightMoves(Board b, Square s) {
        List<Move> moves = this.straightMoves(b, s, true, true);
        moves.addAll(this.straightMoves(b, s, true, false));
        moves.addAll(this.straightMoves(b, s, false, true));
        moves.addAll(this.straightMoves(b, s, false, false));
        return moves;
    }

    /**
     *
     * @param b on the board b
     * @param s the start square
     * @param isRank if the axis is y
     * @param increase if going up the axis
     * @return a list of moves in the specified direction
     */
    public List<Move> straightMoves(Board b,Square s, boolean isRank, boolean increase) {
        Piece[][] a = b.getArray();
        Move m;
        List<Move> moves = new ArrayList<>();
        int i = s.getRank();
        int j = s.getFile();
        Piece pTemp = null;
        int x;
        if (isRank) {
            if (increase) {
                i++;
            } else {
                i--;
            }
            x = i;
        } else {
            if (increase) {
                j++;
            } else {
                j--;
            }
            x = j;
        }
        while (x >= 0 && x < 8 && pTemp == null) {
            pTemp = a[i][j];
            m = new Move(s, new Square(i, j), b);
            if (pTemp == null || this.differentColor(pTemp)) {
                moves.add(m);
            }
            if (isRank) {
                if (increase) {
                    i++;
                } else {
                    i--;
                }
                x = i;
            } else {
                if (increase) {
                    j++;
                } else {
                    j--;
                }
                x = j;
            }
        }
        return moves;
    }

    /**
     *
     * @param b board to check moves on
     * @param s the start square
     * @return a list of all diagonal moves
     */
    public List<Move> allDiagonalMoves(Board b, Square s) {
        List<Move> moves = this.diagonalMoves(b, s, true, true);
        moves.addAll(this.diagonalMoves(b, s, true, false));
        moves.addAll(this.diagonalMoves(b, s, false, true));
        moves.addAll(this.diagonalMoves(b, s, false, false));
        return moves;
    }

    /**
     *
     * @param b board to check moves on
     * @param s the start square
     * @return a list of all diagonal move in the specified direction
     */
    public List<Move> diagonalMoves(Board b, Square s, boolean up, boolean right) {
        Piece[][] a = b.getArray();
        List<Move> moves = new ArrayList<>();
        Move m;
        int i = s.getRank();
        int j = s.getFile();

        if (up) {
            i++;
        } else {
            i--;
        }
        if (right) {
            j++;
        } else {
            j--;
        }
        Piece pTemp = null;
        while (i >= 0 && i < 8 && j >= 0 && j < 8 && pTemp == null) {
            pTemp = a[i][j];
            m = new Move(s, new Square(i, j), b);
            if (pTemp == null || this.differentColor(pTemp)) {
                moves.add(m);
            }
                if (up) {
                    i++;
                } else {
                    i--;
                }
                if (right) {
                    j++;
                } else {
                    j--;
                }
            }
        return moves;
    }

    /**
     *
     * @return the piece as a string
     */
    public String toString() {
        return this.type + "";
    }

    /**
     *
     * @param b the board
     * @param s the start square
     * @return a list of knight moves from where the piece is
     */
    protected List<Move> knightMoves(Board b, Square s) {
        List<Move> moves = new ArrayList<>();
        Square end;
        Piece capture;
        Move m;
        for (int rank = -2; rank < 3; rank++) {
            for (int file = -2; file < 3; file++) {
                if ((Math.abs(rank) == 2 && Math.abs(file) == 1) || (Math.abs(file) == 2 && Math.abs(rank) == 1)) {
                    end = Square.create(s.getRank() + rank, s.getFile() + file);
                    if (end != null) {
                        capture = b.getPiece(end);
                        m = new Move(s, end, b);
                        if (capture == null || this.differentColor(capture)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves;
    }
}
