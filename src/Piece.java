import java.util.ArrayList;
import java.util.List;

abstract class Piece {
    protected boolean color;
    protected char type;
    protected Square s;
    protected int value;

    public Piece(boolean color, char type, Square s, int value) {
        this.color = color;
        this.type = type;
        this.s = s;
        this.value = value;
    }

    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.s.equal(p.getSquare()));
    }

    public Square getSquare() {
        return this.s;
    }

    public abstract List<Move> allLegalMoves(Board b, boolean checkForChecks);

    public abstract Piece copy();

    public char getType() {
        return type;
    }

    public boolean getColor() {
        return color;
    }

    public boolean sameColor(Piece p) {
        return p.getColor() ^ this.color;
    }

    public void move(Square s) {
        this.s = s;
    }

    public List<Move> allStraightMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.straightMoves(b, true, true, checkForChecks);
        moves.addAll(this.straightMoves(b, true, false, checkForChecks));
        moves.addAll(this.straightMoves(b, false, true, checkForChecks));
        moves.addAll(this.straightMoves(b, false, false, checkForChecks));
        return moves;
    }

    public void timePassed(Board b) {
        Piece[][] a = b.getArray();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (this == a[rank][file]) {
                    this.s = new Square(rank, file);
                }
                return;
            }
        }
    }
    public List<Move> straightMoves(Board b, boolean isRank, boolean increase, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
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
            if (pTemp == null || pTemp.getColor() != this.color) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (!checkForChecks || !m.isCheck()) {
                    moves.add(m);
                }
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

    public List<Move> allDiagonalMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.diagonalMoves(b, true, true, checkForChecks);
        moves.addAll(this.diagonalMoves(b, true, false, checkForChecks));
        moves.addAll(this.diagonalMoves(b, false, true, checkForChecks));
        moves.addAll(this.diagonalMoves(b, false, false, checkForChecks));
        return moves;
    }

    public List<Move> diagonalMoves(Board b, boolean up, boolean right, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        List<Move> moves = new ArrayList<>();
        Move m;
        int i = s.getRank();
        int j = s.getFile();
        Piece pTemp = null;
        while (i >= 0 && i < 8 && j >= 0 && j < 8 && pTemp == null) {
            pTemp = a[i][j];
            if (pTemp == null || pTemp.getColor() != this.color) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (!checkForChecks || !m.isCheck()) {
                    moves.add(m);
                }
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

    public String toString() {
        return this.type + "";
    }
}
