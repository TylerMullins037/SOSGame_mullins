import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SOSComputerPlayer extends SOSPlayer {
    private Random random;
    private static final double RANDOM_THRESHOLD = 0.05; // 5% chance of making a random move
    
    public SOSComputerPlayer(char player) {
            super(player);
            this.random = new Random();
    }

    @Override
    public int[] makeMove(SOSGame game) {
        if (random.nextDouble() < RANDOM_THRESHOLD) {
            int[] randomMove = makeRandomMove(game);
            if (randomMove != null) {
                return randomMove;
            }
        }

        int[] safeMove = findTacticalMove(game);
        if (safeMove != null) {
            return safeMove;
        }
        return findSmartMove(game);
    }

    private int[] makeRandomMove(SOSGame game) {
        int boardSize = game.getBoardSize();
        List<int[]> validMoves = new ArrayList<>();
        
        // Keep track of all valid moves
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (game.isValidMove(row, col)) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }
        
        if (!validMoves.isEmpty()) {
            int[] selectedMove = validMoves.get(random.nextInt(validMoves.size()));
            // Randomly Select S or O and place it in a randomly selected square.
            char choice = random.nextDouble() < 0.6 ? 'S' : 'O';
            return new int[]{selectedMove[0], selectedMove[1], choice};
        }
        
        return null; 
    }


    private int[] findTacticalMove(SOSGame game) {
        int boardSize = game.getBoardSize();
        char originalChoice = getChoice();
       
        int[] move = findTacticalChoice(game, boardSize, 'S');
        if (move != null) {
            return move;
        }

        move = findTacticalChoice(game, boardSize, 'O');
        if (move != null) {
            return move;
        }

        setChoice(originalChoice);
        return null;
    }

    private int[] findTacticalChoice(SOSGame game, int boardSize, char choice) {
        setChoice(choice);
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (game.isValidMove(row, col) && wouldCompleteSOS(game, row, col, 'O')) {
                    return new int[] {row, col, 'O'};
                }
            }
        }
        return null;
    }



    private boolean wouldCompleteSOS(SOSGame game, int row, int col, char choice) {
        SOSGame tempGame = copyGame(game);
        
        if (choice == 'S') {
            tempGame.setSquare(row, col, SOSGame.Square.S);
        } else {
            tempGame.setSquare(row, col, SOSGame.Square.O);
        }
        
        return tempGame.SOSCheck(row, col, getPlayer());
    }


    private int[] findSmartMove(SOSGame game) {
        int boardSize = game.getBoardSize();
        int smartRow = -1, smartCol = -1;
        char smartChoice = 'S';
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int row = 0; row < boardSize; row++) {
            for(int col = 0; col < boardSize; col++) {
                if(game.isValidMove(row,col)) {
                    double scoreS = evalMove(game,row,col,'S');
                    double scoreO = evalMove(game,row,col,'O');

                    if (scoreS > bestScore) {
                        bestScore = scoreS;
                        smartRow = row;
                        smartCol = col;
                        smartChoice = 'S';
                    }

                    if (scoreO > bestScore) {
                        bestScore = scoreO;
                        smartRow = row;
                        smartCol = col;
                        smartChoice = 'O';
                    }
                }
            }
        }
        if (smartRow != -1) {
            return new int[] {smartRow, smartCol, smartChoice};
        }
        return null;
    }

    private double evalMove(SOSGame game, int row, int col, char choice) {
        double score = 0;
        SOSGame tempGame = copyGame(game);
        SOSGame.Square squareValue = choice == 'S' ? SOSGame.Square.S : SOSGame.Square.O;
        tempGame.setSquare(row, col, squareValue);
        score += evaluateFutureSOS(tempGame, row, col, choice);
        
        // Positional evaluation - prefer center and avoid edges early in game
        score += evalPosition(tempGame, row, col);
        
        return score;
    }
    
    private double evaluateFutureSOS(SOSGame game, int row, int col, char choice) {
        double score = 0;
        
        if (choice == 'S') {
            // Check if this S could be part of future SOS
            score += countPatternsFuture(game, row, col, 0, 1, 0, 2);  
            score += countPatternsFuture(game, row, col, 1, 0, 2, 0);  
            score += countPatternsFuture(game, row, col, 1, 1, 2, 2);  
            score += countPatternsFuture(game, row, col, 1, -1, 2, -2); 
            
            // Check if this S could complete SOS with existing O
            score += countPartialSOS(game, row, col);
        }
        else {
            score += countPotentialOConnections(game, row, col);
        }
        
        return score;
    }
    
    private double countPatternsFuture(SOSGame game, int row, int col, 
                                      int dr1, int dc1, int dr2, int dc2) {
        int r1 = row + dr1, c1 = col + dc1;
        int r2 = row + dr2, c2 = col + dc2;
        
        if (!game.isValidMove(r1, c1) || !game.isValidMove(r2, c2)) {
            return 0;
        }
        
        if (game.getSquare(r1, c1) == SOSGame.Square.E && 
            game.getSquare(r2, c2) == SOSGame.Square.E) {
            return 0.5; // An SOS might be formed here in the future
        }
        
        // Check if we have S-_-S pattern (missing O)
        if (game.getSquare(r1, c1) == SOSGame.Square.E && 
            game.getSquare(r2, c2) == SOSGame.Square.S) {
            return 0.8; // An SOS is close to being formed
        }
        
        return 0;
    }
    
    private double countPartialSOS(SOSGame game, int row, int col) {
        double score = 0;
        
        int[][] directions = {
            {0, 1}, {1, 0}, {1, 1}, {1, -1},
            {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}
        };
        
        for (int[] dir : directions) {
            int dr = dir[0], dc = dir[1];
            
            // S-O-_
            int r1 = row - (2 * dr);
            int c1 = col - (2 * dc);
            int r2 = row - dr;
            int c2 = col - dc;
            
            if (game.isValidMove(r1, c1) && game.isValidMove(r2, c2) &&
                game.getSquare(r1, c1) == SOSGame.Square.S &&
                game.getSquare(r2, c2) == SOSGame.Square.O) {
                score += 1.0; // This is given the highest value as it forms an SOS
            }
        }
        
        return score;
    }
    
    private double countPotentialOConnections(SOSGame game, int row, int col) {
        double score = 0;
        int[][] directions = {
            {0, 1}, {1, 0}, {1, 1}, {1, -1},
            {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}
        };
        
        for (int[] dir : directions) {
            int dr = dir[0], dc = dir[1];
            
            // Check for S-_-S pattern (where we place O in middle)
            int r1 = row - dr;
            int c1 = col - dc;
            int r2 = row + dr;
            int c2 = col + dc;
            
            if (game.isValidMove(r1, c1) && game.isValidMove(r2, c2) &&
                game.getSquare(r1, c1) == SOSGame.Square.S &&
                game.getSquare(r2, c2) == SOSGame.Square.S) {
                score += 1.0; // Very high value - completes SOS
            }
        }
        
        return score;
    }
    
    private double evalPosition(SOSGame game, int row, int col) {
        int boardSize = game.getBoardSize();
        double score = 0;
        
        double centerRow = (boardSize - 1) / 2.0;
        double centerCol = (boardSize - 1) / 2.0;
        double distanceFromCenter = Math.sqrt(Math.pow(row - centerRow, 2) + 
                                           Math.pow(col - centerCol, 2));
        
        // closer to center is better
        double normalizedDistance = 1.0 - (distanceFromCenter / (boardSize / 2.0));
        
        // Early game strategy is to control the middle of the board
        int moveCount = countOccupiedSquares(game);
        if (moveCount < boardSize * boardSize / 3) {
            score += normalizedDistance * 0.4; 
        } else {
            score += normalizedDistance * 0.2;
        }
        
        return score;
    }
    
    private int countOccupiedSquares(SOSGame game) {
        int count = 0;
        int boardSize = game.getBoardSize();
        
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (game.getSquare(row, col) != SOSGame.Square.E) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private SOSGame copyGame(SOSGame current) {
        SOSGame copy;
        if (current.getGameMode() == SOSGame.GameMode.SIMPLE) {
            copy = new SOSSimpleGame(current.getBoardSize());
        } else {
            copy = new SOSGeneralGame(current.getBoardSize());
        }
        
        for (int i = 0; i < current.getBoardSize(); i++) {
            for (int j = 0; j < current.getBoardSize(); j++) {
                copy.setSquare(i, j, current.getSquare(i, j));
            }
        }
        
        return copy;
    }
}
