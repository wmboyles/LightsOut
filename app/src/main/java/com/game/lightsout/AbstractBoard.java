package com.game.lightsout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Basic, non-UI functionality for all game boards of any size. Provides a buffer between the UI
 * elements of BoardUI and the abstract, backend elements in GameBoard.
 *
 * @author William Boyles
 */
public abstract class AbstractBoard extends AppCompatActivity {
    /**
     * The number of clicks that the user has made in the current solve attempt
     */
    private int clicks;
    /**
     * The mathematical backend keeping track of how to solve the board
     */
    private GameBoard gb;
    /**
     * Is the user playing in on lights only mode?
     */
    private boolean onOnlyMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("AbstractBoard", "Created abstract board");

        super.onCreate(savedInstanceState);
        this.clicks = 0;

        Intent intent = getIntent();

        this.onOnlyMode = getIntent().getBooleanExtra("onLightsOnly", false);
        Log.i("AbstractBoard", "USER CHOICE: On Lights Only Mode: "+this.onOnlyMode);

        int boardSize = intent.getIntExtra("boardSize", -1);
        gb = new GameBoard(boardSize, onOnlyMode);
    }


    public int getClicks() {
        Log.i("AbstractBoard", "Clicks: "+this.clicks);

        return this.clicks;
    }


    public void resetClicks() {
        Log.v("AbstractBoard", "Reset clicks to 0");

        this.clicks = 0;
    }


    public int getMinClicks() {
        int minClicks = gb.getMinClicks();
        if(this.clicks == 0) {
            Log.i("AbstractBoard", "Min Clicks: " + minClicks);
        } else{
            Log.v("AbstractBoard", "Min Clicks: " + minClicks);
        }

        return minClicks;
    }


    public void click(int btnRow, int btnCol) {
        Log.v("AbstractBoard","Detected click for button "+btnRow+"-"+btnCol);

        this.clicks++;
        gb.click(btnRow, btnCol);
    }


    public boolean isSolved() {
        boolean isSolved = gb.isSolved();
        Log.v("AbstractBoard","The board is solved: "+isSolved);

        return isSolved;
    }


    protected void newScramble() {
        Log.i("AbstractBoard","Creating a new scramble");

        gb.newScramble();
        setButtons(gb.getBoard());
    }


    public boolean isOnOnlyMode() { return this.onOnlyMode; }

    public void initSolution(){
        gb.updateSolution();
    }

    /**
     * All UI boards must have a way to set the values of the buttons for scrambling.
     *
     * @param btnStates a 2D boolean array of button states (true=on & false=off).
     */
    protected abstract void setButtons(boolean[][] btnStates);
}
