package com.game.lightsout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.game.myfirstapp.R;

import java.util.Locale;

/**
 * This activity is the level select screen. It presents users with a slider to select the level
 * they want to play, an option to only be able to click on lights, and an option to be timed.
 * This code contains some backend stuff to properly get the users preferences and send them to
 * the next activity. UI elements are described in the corresponding XML file.
 *
 * @author William Boyles
 */
public class LevelSelect extends AppCompatActivity {
    /**
     * The minimum level/board size the user and select
     */
    final int levelSelectMin = 1;
    /**
     * A level selection slider
     */
    private SeekBar levelSelectBar;
    /**
     * Displays currently selected level below slider
     */
    private TextView levelSelectProgress;
    /**
     * Button below currently selected level for the user to confirm the selection
     */
    private Button letsPlayButton;
    /**
     * Switch users select if they want to play in on lights only mode
     */
    private Switch onLightsOnlySwitch;
    /**
     * Switch users select if the want to have their solves times
     */
    private Switch timerSwitch;
    /**
     * The currently selected board size
     */
    private int levelSelection = 1; //The selected board size. -1 is no selection value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        this.onLightsOnlySwitch = findViewById(R.id.onLightsOnlySwitch);
        this.timerSwitch = findViewById(R.id.timerSwitch);

        createSeekBar();
        createProgressText();
        createLetsPlayButton();
    }

    private void createSeekBar() {
        this.levelSelectBar = findViewById(R.id.levelSelectSlider);
        levelSelectBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                levelSelection = i + levelSelectMin;
                levelSelectProgress.setText(String.format(Locale.getDefault(), "%d", levelSelection));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });
    }

    private void createProgressText() {
        this.levelSelectProgress = findViewById(R.id.barProgress);
        levelSelectProgress.setText(String.format(Locale.getDefault(), "%d", levelSelectBar.getProgress() + levelSelectMin));
    }

    private void createLetsPlayButton() {
        this.letsPlayButton = findViewById(R.id.levelSelectButton);
        letsPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBoardScreen(levelSelection);
            }
        });
    }

    private void openBoardScreen(int boardSize) {
        Intent intent = new Intent(this, BoardUI.class);
        intent.putExtra("boardSize", boardSize);
        intent.putExtra("onLightsOnly", this.onLightsOnlySwitch.isChecked());
        intent.putExtra("timerMode", this.timerSwitch.isChecked());

        startActivity(intent);
    }
}