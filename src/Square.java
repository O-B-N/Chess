/**
 * a class to represent squares on the board
 */
public class Square {
    private final int rank;
    private final int file;

    /**
     *
     * @param rank the rank of the square
     * @param file the file of the square
     */
    public Square(int rank, int file) {
        this.rank = rank;
        this. file = file;
    }

    /**
     * creates a square with coordinates
     * @param rank the rank of the square
     * @param file the file of the square
     * @return the created square
     */
    public static Square create(int rank, int file) {
        if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
            return new Square(rank, file);
        }
        return null;
    }

    /**
     * creates a square from a string
     * @param str the input string
     * @return the created square
     */
    public static Square create(String str) {
        if (str.isBlank() || str.length() != 2) {
            return null;
        }

        char[] a = str.toCharArray();
        int rank = a[0];
        int file = a[1];

        if (rank > '0' && rank < '9' && file > '`' && file < 'i') {
            return null;
        }
        return new Square(rank - (int)'0', file - (int)'`');
    }

    /**
     *
     * @param s the square to compare to
     * @return true if they're the same
     */
    public boolean equal(Square s) {
        if (s == null) {
            return true;
        }
        return this.toString().equals(s.toString());
    }

    /**
     *
     * @return the square's rank
     */
    public int getRank() {
        return this.rank;
    }

    /**
     *
     * @return the square's file
     */
    public int getFile() {
        return this.file;
    }

    /**
     *
     * @return the square as a string
     */
    public String toString() {
        char a = 'a';
        a += this.file;
        return a + Integer.toString(this.rank + 1);
    }
}