/**
 * a class that start a game
 */
public class Game {

    /**
     * main function to run the game
     * @param args empty for now might add
     */
    public static void main(String[] args) {
       Board b = Board.newGame();
       System.out.println(b.positionToFen());
       b.runTest();
        //Board.simpleTest();
    }
}
