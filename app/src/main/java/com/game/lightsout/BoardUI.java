package com.game.lightsout;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.game.myfirstapp.R;

/**
 * The UI elements for the game board. All backend stuff is handled by AbstractBoard or GameBoard.<br>
 * This UI interacts with the backend through the AbstractBoard class
 *
 * @author William Boyles
 */
public class BoardUI extends AbstractBoard {
    /**
     * The red accent color of the buttons
     */
    private static int buttonOnColor;
    /**
     * The container view that will be displayed to the user. Contains our buttons.
     */
    private LinearLayout rootView; //superclass of all view types
    /**
     * Click counter text view.
     */
    private TextView clickCounter;
    /**
     * Min clicks text view.
     */
    private TextView minClicks;
    /**
     * The text at the top of the screen. Usually says the board size. This will only be displayed
     * if the timer is not enabled.
     */
    private TextView boardTitle;
    /**
     * The chronometer object that will display the time and board size if the timer is enabled.
     */
    private Chronometer timer;
    /**
     * The button at the bottom of the screen that says new scramble.
     */
    private Button newScrambleBtn;
    /**
     * The buttons in their array format. Allows easy access when clicking.
     */
    private Button[][] buttons;
    /**
     * The number of buttons in each row/col of the board. Used in UI mostly for measuring.
     */
    private int boardSize;
    /**
     * Is the game in timer mode?
     */
    private boolean timerMode;

    /**
     * What happens when this activity screen is created. We create some objects that we will need
     * and show some UI elements to the user.
     *
     * @param savedInstanceState the saved instance state of the user.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("BoardUI", "Creating Board UI");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        this.boardSize = getIntent().getIntExtra("boardSize", -1); //Get board size
        this.timerMode = getIntent().getBooleanExtra("timerMode", false);

        Log.i("BoardUI", "USER CHOICE: Board Size: " + this.boardSize);
        Log.i("BoardUI", "USER CHOICE: Timer Mode: " + this.timerMode);

        buttonOnColor = getResources().getColor(R.color.colorAccent);

        addButtons(boardSize); //Create a buttons in the array
        createBoardView(); //Create and show view that we will manipulate
    }

    /**
     * Adds the buttons in the board to our array of buttons. Does not make them visible. Visibility
     * happens in createBoardView().
     *
     * @param squareSize the number of horizontal rows and vertical columns in the grid of buttons.
     */
    private void addButtons(int squareSize) { //We use two params in case we ever want non-rectangular boards
        Log.i("BoardUI", "Adding buttons");

        this.buttons = new Button[squareSize][squareSize];

        Point size = getScreenDimensions(); //Point where xy coordinates are screen size in pixels
        int borderSize = 15; //the size of the border on each size of the button array
        int minDimen = Math.min(size.x, size.y) - (2 * borderSize);
        ViewGroup.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(minDimen / boardSize, minDimen / boardSize, 3f);

        for (int r = 0; r < squareSize; r++) {
            for (int c = 0; c < squareSize; c++) {
                buttons[r][c] = new Button(this);
                buttons[r][c].setEnabled(false);
                buttons[r][c].setBackgroundColor(Color.LTGRAY);
                buttons[r][c].setOnClickListener(new ButtonOnClickListener(r, c));
                buttons[r][c].setLayoutParams(buttonLayoutParams);
            }
        }
    }


    /**
     * Creates and makes visible the UI elements, like the stats bar, the button grid, and the
     * option buttons.
     */
    private void createBoardView() { //TODO Find real layout params and view that work
        Log.i("BoardUI", "Creating Board View");

        //Setup root view: the container for all other views
        this.rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setBackgroundColor(Color.WHITE);
        rootView.setGravity(Gravity.CENTER_HORIZONTAL);

        createStatsBar();
        createButtonGrid();
        createBottomButton();

        setContentView(rootView); //make screen visible
    }


    /**
     * Helper method for createBoardView(). Creates the bottom button that says "new scramble".
     */
    private void createBottomButton() {
        Log.i("BoardUI", "Creating bottom \"new scramble\" button");

        ViewGroup.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        this.newScrambleBtn = new Button(this);
        newScrambleBtn.setLayoutParams(btnLayoutParams);

        newScrambleBtn.setEnabled(true);
        newScrambleBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent)); //Color when enabled

        newScrambleBtn.setText(getResources().getString(R.string.newScramble));
        newScrambleBtn.setTextSize(42f);
        newScrambleBtn.setTextColor(getResources().getColor(R.color.white));

        newScrambleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("BoardUI", "Setting bottom button on click listener");

                BoardUI.super.newScramble(); //calls abstract board method.

                newScrambleBtn.setEnabled(false);
                newScrambleBtn.setBackgroundColor(Color.DKGRAY); //Color when disabled

                if (timerMode) { //Reset & Start timer/stopwatch if needed
                    Log.i("BoardUI", "Starting timer");
                    timer.setBase(SystemClock.elapsedRealtime()); //So the clock starts at 0
                    timer.start();
                } else{
                    String title = getResources().getString(R.string.boardTitle, boardSize);
                    boardTitle.setText(title);
                }
            }
        });

        rootView.addView(newScrambleBtn);
    }

    /**
     * Helper method for createBoardView(). Creates the clicks and min clicks counter above the
     * button board and add them to the root view.
     */
    private void createStatsBar() {
        Log.i("BoardUI", "Creating stats bar");

        int largeFontSize = 50; //TODO these font sizes are completely arbitrary, but are what seems to work
        int smallFontSize = 24;

        LinearLayout statsContainer = new LinearLayout(this);
        statsContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout statsRow1 = new LinearLayout(this);
        LinearLayout statsRow2 = new LinearLayout(this);

        ViewGroup.LayoutParams statsRowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        Log.v("BoardUI", "Creating title text box");
        String title = getResources().getString(R.string.boardTitle, boardSize);
        if (timerMode) {
            this.timer = new Chronometer(this);
            timer.setText(getResources().getString(R.string.timerZero, boardSize));
            timer.setFormat(title + " - %s");
            timer.setGravity(Gravity.CENTER_HORIZONTAL);
            timer.setTextColor(buttonOnColor);
            timer.setTextSize(largeFontSize);
            timer.setLayoutParams(statsRowLayoutParams);
            statsRow1.addView(timer);
        } else {
            this.boardTitle = new TextView(this);
            boardTitle.setText(title);
            boardTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            boardTitle.setTextColor(buttonOnColor);
            boardTitle.setTextSize(largeFontSize);
            boardTitle.setLayoutParams(statsRowLayoutParams);
            statsRow1.addView(boardTitle);
        }

        //Create click count stat and add to row 2
        Log.v("BoardUI", "Creating click count text box");
        TextView clickCount = new TextView(this);
        this.clickCounter = clickCount;
        updateClickCount();
        clickCount.setGravity(Gravity.START);
        clickCount.setTextSize(smallFontSize);
        clickCount.setTextColor(buttonOnColor);
        clickCount.setLayoutParams(statsRowLayoutParams);
        statsRow2.addView(clickCount);

        //Create min clicks stat and add to row 2
        Log.v("BoardUI", "Creating min clicks text box");
        TextView minClicks = new TextView(this);
        this.minClicks = minClicks;
        updateMinClicks();
        minClicks.setGravity(Gravity.END);
        minClicks.setTextSize(smallFontSize);
        minClicks.setTextColor(buttonOnColor);
        minClicks.setLayoutParams(statsRowLayoutParams);
        statsRow2.addView(minClicks);

        //Add row to table and table to view
        Log.v("BoardUI", "Adding items to view");
        statsContainer.addView(statsRow1);
        statsContainer.addView(statsRow2);
        rootView.addView(statsContainer);
    }

    /**
     * Helper method for createBoardView(). Creates the grid of buttons and adds them to the root
     * view. Note that this method is different from addButtons(), which only creates the button
     * objects.
     */
    private void createButtonGrid() {
        Log.v("BoardUI", "Putting buttons in grid");

        TableLayout buttonGrid = new TableLayout(this);
        for (int r = 0; r < boardSize; r++) {
            TableRow tableRow = new TableRow(this);
            for (int c = 0; c < boardSize; c++) {
                tableRow.addView(buttons[r][c]);
            }
            buttonGrid.addView(tableRow);
        }
        rootView.addView(buttonGrid);
    }


    /**
     * Given an array of button states (true=on/red & false=off/gray), set the buttons to the given
     * state.
     * The total and min clicks counts are reset.
     * If the array is shorter in one dimension than the board size, buttons outside the array
     * (below, right) will not be changed.
     * If the array is longer in one dimension than the board size, only values inside the button
     * board will be represented.
     * Note that setting these states could create an impossible to solve board.
     *
     * @param btnStates a boolean array of buttons states.
     */
    @Override
    protected void setButtons(boolean[][] btnStates) {
        Log.i("Board UI", "Setting buttons to states from boolean array");
        if(!super.isOnOnlyMode()) {
            super.initSolution();
        }

        int minRows = Math.min(boardSize, btnStates.length);
        int minCols = Math.min(boardSize, btnStates[0].length);
        for (int r = 0; r < minRows; r++) {
            for (int c = 0; c < minCols; c++) {
                int btnColor = btnStates[r][c] ? buttonOnColor : Color.LTGRAY;
                buttons[r][c].setBackgroundColor(btnColor);
                buttons[r][c].setEnabled(!(isOnOnlyMode() && btnColor == Color.LTGRAY));
            }
        }

        super.resetClicks();
        updateClickCount();

        updateMinClicks();
    }


    /**
     * Updates the total clicks counter. This happens every time the user hits a button on the game
     * board.
     */
    private void updateClickCount() {
        Log.v("BoardUI", "Updating click count");

        clickCounter.setText(getResources().getString(R.string.clicksText, super.getClicks()));
    }

    /**
     * Updates the min clicks counter. This only happens immediately after a new scramble is
     * created, like when the user hits "New Scramble"
     */
    private void updateMinClicks() {
        Log.v("BoardUI", "Updating min clicks");

        int minClicksCount = super.getMinClicks();
        if(minClicksCount > 0) {
            minClicks.setText(getResources().getString(R.string.minText, Integer.toString(minClicksCount)));
        } else{
            minClicks.setText(getResources().getString(R.string.minText, "?"));
        }
    }

    /**
     * Gets the size of the screen as a Point object. Point.x is the screen width, and Point.y is
     * the screen height.
     *
     * @return a point object containing the screen size.
     */
    private Point getScreenDimensions() {
        Log.v("BoardUI", "Getting screen pixel dimensions");

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    /**
     * What happens when the user solves the board. The buttons on the board are disables to
     * maintain an empty board, the win and clicks are acknowledged, and the user can either select
     * a new size or get a new scramble.
     */
    private void solvedBoard() {
        Log.i("BoardUI", "The board has been solved");

        if (timerMode) { //Stop timer/stopwatch
            timer.stop();
            Log.i("BoardUI", "Stopping Timer");
        } else {
            boardTitle.setText(getResources().getString(R.string.youWon));
            Log.v("BoardUI", "Changed top text to \"You Won!\"");
        }

        newScrambleBtn.setEnabled(true);
        newScrambleBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        Log.i("BoardUI", "Enabled new scramble button and changed button color");

        if (super.getMinClicks() > super.getClicks()) {
            Log.e("BoardUI", "Board was solved in fewer than minimal clicks");
        }
    }


    /**
     * Custom button listener allows us to pass the position of the button.
     *
     * @author William Boyles
     */
    private class ButtonOnClickListener implements View.OnClickListener {
        /**
         * The row index of the button for which we are creating a click listener.
         */
        private int btnRow;
        /**
         * The column index of the button for which we are creating a click listener.
         */
        private int btnCol;

        /**
         * Create a click listener for a button with a given row and column index.
         *
         * @param btnRow the row index of the button.
         * @param btnCol the column index of the button.
         */
        private ButtonOnClickListener(int btnRow, int btnCol) {
            Log.v("BoardUI", "Creating on click listener for button " + btnRow + "-" + btnCol);

            this.btnRow = btnRow;
            this.btnCol = btnCol;
        }

        /**
         * What happens when a board button is pressed. In this case, we want to let the UI to
         * update, which will trigger series of events for backend classes.
         *
         * @param view the view that the button is in.
         */
        @Override
        public void onClick(View view) {
            Log.i("BoardUI", "Button " + btnRow + "-" + btnCol + " has been pressed");

            this.buttonClick(btnRow, btnCol); //change the UI; start the chain of up to the backend
        }

        /**
         * When a button is clicked in a certain row/column, that button flips state (color), and so
         * do the adjacent buttons.
         *
         * @param btnRow the row number (0-indexed) of the pressed button.
         * @param btnCol the column number (0-indexed) of the pressed button.
         */
        private void buttonClick(int btnRow, int btnCol) {
            //Flip button colors
            flipColor(BoardUI.this.buttons[btnRow][btnCol]); //Flip the button itself
            if (btnCol != 0) { //Flip the left button, if not in left column
                flipColor(BoardUI.this.buttons[btnRow][btnCol - 1]);
            }
            if (btnCol != BoardUI.this.boardSize - 1) { //Flip the right button, if not in right column
                flipColor(BoardUI.this.buttons[btnRow][btnCol + 1]);
            }
            if (btnRow != 0) { //Flip the upper button, if not in top row
                flipColor(BoardUI.this.buttons[btnRow - 1][btnCol]);
            }
            if (btnRow != BoardUI.this.boardSize - 1) { //Flip the lower button, if not in bottom row
                flipColor(BoardUI.this.buttons[btnRow + 1][btnCol]);
            }
            Log.v("BoardUI", "Flipped button colors");

            //Let AbstractBoard know there was a click
            BoardUI.super.click(btnRow, btnCol);

            //Update click count and min clicks
            Log.v("BoardUI", "Updating clicks count and min clicks");
            BoardUI.this.updateClickCount();
            BoardUI.this.updateMinClicks();

            //Check for solved board
            Log.v("BoardUI", "Checking if board is solved");
            if (BoardUI.super.isSolved()) {
                BoardUI.this.solvedBoard();
            }
        }

        /**
         * Flips the color of a button to/from gray and the accent color and updates if the button
         * is enabled based on color if on only mode is enabled.
         *
         * @param btn the button whose state to flip.
         */
        private void flipColor(Button btn) {
            int currentColor = ((ColorDrawable) btn.getBackground()).getColor();
            int newColor = currentColor == Color.LTGRAY ? buttonOnColor : Color.LTGRAY;
            btn.setBackgroundColor(newColor);

            if (isOnOnlyMode()) {
                btn.setEnabled(newColor == buttonOnColor);
            }
        }
    }
}