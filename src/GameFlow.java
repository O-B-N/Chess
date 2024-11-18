import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * a gameFlow class that run all the different variants
 */
public class GameFlow {
    boolean duckChess = false;
    boolean horde = false;

    /**
     * a test function
     */
    public static void runTest(Board b) {
        System.out.println(b.positionToFenWithMoves());
        Random r = new Random();
        b.printBoard();
        List<Move> moves;
        Scanner scan = new Scanner(System.in);
        int index;
        while (!b.isGameOver()) {
            if (b.getColor()) {
              //  b.printBoard();
                System.out.println("white's turn");
            } else {
                System.out.println("black's turn");
            }
            moves = b.allMoves(b.getColor(), true);
            if (false) {               //if (b.getColor())
                index = 0;
                System.out.println("choose an index: \nor -1 for printing the board");
                int i = 1;
                for(Move m : moves) {
                    System.out.println(i + ": " + m.toString(moves));
                    i++;
                }
                while (index < 1 || index > moves.size()) {
                    if (scan.hasNextInt()) {
                        index = scan.nextInt();
                    }
                    if (index == -1) {
                        b.printBoard();
                    }
                }
            } else {
                index = r.nextInt(moves.size()) + 1;
            }
            Move m = moves.get(index - 1);
            b.makeMove(m);
            b.printBoard();
       //     if (b.getPey() == 200) {
       //         index = scan.nextInt();
        //    }
            System.out.println("move made: " + m.toString(moves) + '\n' + "pey: " + b.getPey());
        }
    }

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
      //  a[1][7] = new Queen(true);
        a[1][4] = new King(true);
        a[6][4] = new King(false);
        a[6][6] = new Pawn(true);
      //  a[3][7] = new Queen(true);
     //   a[0][7] = new Queen(true);
    //    a[7][0] = new Rook(false);
       // a[7][7] = new Rook(false);
        Board test = new Board(a, new int[]{0, 7});
        GameFlow.runTest(test);
    }

}
