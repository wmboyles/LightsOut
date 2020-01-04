package com.game.lightsout;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The backend of the game. This class represents a board of any size. It stores both the state of
 * the board that the user sees, but also the net clicks used to achieve this state.
 *
 * @author William Boyles
 */
class GameBoard {
    /**
     * The number of buttons per side of the board
     */
    private int boardSize;
    /**
     * The state of the board as shown to the user
     */
    private boolean[][] board;
    /**
     * The clicks the user would need to do to solve the board most efficiently
     */
    private boolean[][] boardSolution;
    /**
     * Is the board a on lights only mode?
     */
    private boolean onLightsOnly;
    /**
     * The minimum number of clicks needed to solve the current board
     */
    private int minClicks;
    /**
     * Updates the optimal solution of the board
     */
    private GameBoardSolver solver;


    /**
     * Creates a new square board with a given number of buttons per side.
     *
     * @param boardSize the number of buttons per side of the board.
     */
    GameBoard(int boardSize, boolean onLightsOnly) {
        Log.i("GameBoard", "Creating Game Board");

        this.boardSize = boardSize;
        this.board = new boolean[boardSize][boardSize];

        this.onLightsOnly = onLightsOnly;
        if (!onLightsOnly) {
            this.minClicks = 0;
            this.boardSolution = new boolean[boardSize][boardSize];
            this.solver = new GameBoardSolver();
        } else {
            this.minClicks = -1; //We can't find out right now the min clicks number
            this.boardSolution = null;
            this.solver = null;
        }
    }


    /**
     * Updates both the solution board and board that the user sees when the user clicks a given
     * location in the board.
     *
     * @param btnRow the row index of the user's click
     * @param btnCol the column index of the user's click
     */
    void click(int btnRow, int btnCol) {
        Log.v("GameBoard", "Click on button "+btnRow+"-"+btnCol);

        //Flip the 3, 4, or 5 button colors that the user sees
        board[btnRow][btnCol] = !board[btnRow][btnCol];
        if (btnCol != 0) { //Flip the left button, if not in left column
            board[btnRow][btnCol - 1] = !board[btnRow][btnCol - 1];
        }
        if (btnCol != this.boardSize - 1) { //Flip the right button, if not in right column
            board[btnRow][btnCol + 1] = !board[btnRow][btnCol + 1];
        }
        if (btnRow != 0) { //Flip the upper button, if not in top row
            board[btnRow - 1][btnCol] = !board[btnRow - 1][btnCol];
        }
        if (btnRow != this.boardSize - 1) { //Flip the lower button, if not in bottom row
            board[btnRow + 1][btnCol] = !board[btnRow + 1][btnCol];
        }

        //Flip the single location in the solution
        if (!onLightsOnly) {
            boardSolution[btnRow][btnCol] = !boardSolution[btnRow][btnCol];
        }
    }


    void updateSolution() { //Assumes you can click all lights
        Log.i("GameBoard","Updating solution");
        if(boardSize == 9){
            Log.w("GameBoard","A 9x9 performs very slowly and uses lots of memory. The application may respond slowly");
        }

        this.boardSolution = solver.findBestSolution(this.boardSolution);
        //this.boardSolution = solver.execute(this.boardSolution); //solver.findBestSolution(boardSolution);
        int newMinClicks = 0;
        for (int r = 0; r < this.boardSize; r++) {
            for (int c = 0; c < this.boardSize; c++) {
                newMinClicks += this.boardSolution[r][c] ? 1 : 0;
            }
        }

        Log.v("GameBoard", "New min clicks: "+newMinClicks);
        this.minClicks = newMinClicks;
    }

    int getMinClicks() {
        Log.v("GameBoard", "Min clicks: "+this.minClicks);

        return this.minClicks;
    }


    /**
     * Is the board solved? That is, are all of the lights that use user sees turned off?
     *
     * @return true if all of the lights are out, else false.
     */
    boolean isSolved() {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                //If any lights are on, or there are still clicks needed to solve
                if (board[r][c]) {
                    Log.v("GameBoard", "The board is solved: false");
                    return false;
                }
            }
        }

        Log.v("GameBoard", "The board is solved: true");
        return true;
    }


    /**
     * Returns the current state of the board that the user sees.
     *
     * @return the state of the user's board as a 2D boolean array.
     */
    boolean[][] getBoard() {
        Log.v("GameBoard", "Returning the current state of the board");
        return this.board;
    }


    /**
     * Clicks randomly a number of times equal to the amount of lights on the board.
     * All lights out puzzles can be solved in at most the number of buttons on the board.
     */
    void newScramble() {
        Log.i("GameBoard", "Creating a new scramble with at least "+(boardSize*boardSize)+" clicks");

        newScramble(boardSize * boardSize);
    }


    /**
     * Scrambles the current board by clearing its current state and clicking a given
     * number of times. It is possible for clicks to happen multiple times on the same
     * light, possible resulting in no net change to the tile.
     *
     * @param scrambleClicksMax the maximum number of times to randomly click the board. The number
     *                          of actual clicks is always at most this value. Although higher
     *                          values are legal arguments, there is no reason for this value to be
     *                          greater than the number of buttons on the board.
     */
    private void newScramble(int scrambleClicksMax) {
        Log.i("GameBoard", "Creating a new scramble with at least "+scrambleClicksMax+" clicks");

        //Clear the board
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                this.board[r][c] = false;
                if (!this.onLightsOnly) {
                    this.boardSolution[r][c] = false;
                }
            }
        }
        this.minClicks = 0;
        Log.v("GameBoard", "Cleared the board");

        //Click randomly scramble clicks times
        Random r = new Random();
        int scrambleClicks = scrambleClicksMax - r.nextInt(scrambleClicksMax / boardSize); //click a random number of times
        Log.i("GameBoard", "Clicking randomly "+scrambleClicks+" times");
        for (int i = 0; i < scrambleClicks; i++) {
            click(r.nextInt(boardSize), r.nextInt(boardSize));
        }

        if (isSolved()) { //In the unlikely chance that the board is solved after scrambling once, which can happen on 2x2, try again.
            Log.i("GameBoard", "Random clicking kept board solved... Trying again");
            
            newScramble(scrambleClicksMax);
        }
    }


    /**
     * Uses basis patterns to find the best solution for the board.
     *
     * @author William Boyles
     */
    private class GameBoardSolver {
        /**
         * List of null patterns
         */
        private List<boolean[][]> nullPatterns; //This should only be computed once upon construction

//        //Takes a solution, finds best solution
//        protected boolean[][] doInBackground(boolean[][]... boards){ //should only pass one argument.
//            if(nullPatterns == null) { //This really shouldn't ever happen
//                Log.e("GameBoardSolver", "NullPatterns not initialized");
//                findNullPatterns();
//            }
//
//            return findBestSolution(boards[0]);
//        }

        /**
         * Creates a new solver for a given board size. This will call the linear algebra library
         * to find the null patterns.
         */
        private GameBoardSolver() {
            this.nullPatterns = new LinkedList<>();
            findNullPatterns();
        }


        /**
         * Given a possible sub-optimal solution for a board, this method finds the best equivalent
         * solution
         *
         * @param currentSolution a possibly sub-optimal solution to a board
         * @return the equivalent solution to the given solution that uses the lest number of clicks.
         */
        private boolean[][] findBestSolution(boolean[][] currentSolution) {
            Log.i("GameBoardSolver", "Finding the best solution for the current board");

            boolean[][] bestSolution = currentSolution;
            for (boolean[][] nullPattern : nullPatterns) {
                bestSolution = betterSolution(bestSolution, getSolution(currentSolution, nullPattern));
            }

            Log.v("GameBoardSolver", "Found the best solution");
            return bestSolution;
        }


        private void findNullPatterns() {
            Log.i("GameBoardSolver", "Beginning Calculation of null patterns");
            //Generate the matrix that gives us the null forms
            Matrix<Integer> nullsMatrix = new Matrix<>(boardSize * boardSize, boardSize * boardSize, new PrimeField(2));
            nullsMatrix.setAll(0);

            for (int d = 0; d < boardSize * boardSize; d++) {
                nullsMatrix.set(d, d, 1);             //center diagonals
                if (d > 0 && d % boardSize != 0) {                              //bordering diagonals
                    nullsMatrix.set(d - 1, d, 1);  //left
                    nullsMatrix.set(d, d - 1, 1);   //above
                }
                if (d < boardSize * (boardSize - 1)) {        //diagonals along sides
                    nullsMatrix.set(boardSize + d, d, 1); //bottom side diagonal
                    nullsMatrix.set(d, boardSize + d, 1); //top side diagonal
                }
            }
            Log.v("GameBoardSolver","Created big matrix");

            nullsMatrix.reducedRowEchelonForm();
            Log.v("GameBoardSolver", "Reduced big matrix");

            //We have to manually fill in the last few columns ourselves to get the nulls
            int numBasisPatterns = findNumBasisPatterns(nullsMatrix);
            for (int c = nullsMatrix.columnCount() - numBasisPatterns; c < nullsMatrix.columnCount(); c++) {
                for (int r = 0; r < nullsMatrix.rowCount(); r++) {
                    if (r == c) {
                        nullsMatrix.set(r, c, 1);
                    }
                }
            }

            //Each column of the last columns of the matrix are a basis pattern
            Log.v("GameBoardSolver", "Generating basis patterns");
            for (int c = nullsMatrix.columnCount()-numBasisPatterns; c < nullsMatrix.columnCount(); c++) {
                boolean[][] basisPattern = new boolean[boardSize][boardSize];
                for (int r = 0; r < nullsMatrix.rowCount(); r++) {
                    basisPattern[r / boardSize][r % boardSize] = nullsMatrix.get(r, c) != 0;
                }

                nullPatterns.add(basisPattern);
            }
            Log.i("GameBoardSolver", "Generated "+ nullPatterns.size()+" basis patterns"
            );

            getAllPermutations(nullPatterns);
        }

        /**
         * Guaranteed to get all unique patterns of lights. Since the basis null patterns are
         * orthogonal to each other, we only need to consider combinations of at most 2 basis
         * patterns to generate all unique null patterns.
         *
         * @param basisPatterns a list of basis patterns
         */
        private void getAllPermutations(List<boolean[][]> basisPatterns){
            int max1 =  basisPatterns.size();
            for(int i = 0; i < max1; i++){
                int max2 = basisPatterns.size();
                for(int j = i+1; j < max2; j++){
                    basisPatterns.add(getSolution(basisPatterns.get(i),basisPatterns.get(j)));
                }
            }

            Log.i("GameBoardSolver","Got "+basisPatterns.size()+" total patterns.");
        }


        /**
         * Finds the number of basis patterns in a given n^2 x n^2 reduced row echelon form matrix.
         * These "basis patterns" are the number of linearly independent, orthogonal vectors of
         * dimension n^2.
         *
         * @param nullsMatrix a n^2 x n^2 reduced row echelon form matrix
         * @return the number of basis patterns in the board represented by the given solved matrix.
         */
        private int findNumBasisPatterns(Matrix<Integer> nullsMatrix) {
            Log.v("GameBoardSolved", "Finding the number of basis patterns");

            int basisPatternsCount = 0;
            for (int r = nullsMatrix.rowCount() - 1; r >= 0; r--) {
                boolean allZeroes = true;
                for (int c = 0; c < nullsMatrix.columnCount(); c++) {
                    if (nullsMatrix.get(r, c) != 0) {
                        allZeroes = false;
                        break;
                    }
                }

                if (allZeroes) {
                    basisPatternsCount++;
                } else {
                    break;
                }
            }

            Log.v("GameBoardSolver", "Found "+basisPatternsCount+" basis patterns");
            return basisPatternsCount;
        }


        private boolean[][] getSolution(boolean[][] currentSolution, boolean[][] nullPattern) {
            boolean[][] resultantState = new boolean[boardSize][boardSize];
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    resultantState[r][c] = currentSolution[r][c] ^ nullPattern[r][c]; //xor
                }
            }

            return resultantState;
        }


        private boolean[][] betterSolution(boolean[][] currentSolution, boolean[][] newSolution) {
            int currentSolutionClicks = 0, newSolutionClicks = 0;
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    currentSolutionClicks += currentSolution[r][c] ? 1 : 0;
                    newSolutionClicks += newSolution[r][c] ? 1 : 0;
                }
            }

            return currentSolutionClicks <= newSolutionClicks ? currentSolution : newSolution;
        }
    }
}