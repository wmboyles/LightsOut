package com.game.lightsout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.game.myfirstapp.R;

/**
 * This is the first screen users see when they launch the app. It displays the app's title, with
 * buttons below that allow users to either select a level to play, or learn more about how to play.
 *
 * @author William Boyles
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Button allowing users to select a level to play
     */
    private Button levelSelectButton;
    /**
     * Button allowing users to learn how to play
     */
    private Button howToPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //superclass call
        setContentView(R.layout.activity_main); //calls the activity_main.xml file

        howToPlayButton = findViewById(R.id.howToPlayButton);
        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHowToPlayScreen();
            }
        });

        levelSelectButton = findViewById(R.id.levelSelectButton);
        levelSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLevelSelectScreen();
            }
        });
    }

    private void openHowToPlayScreen() {
        Intent intent = new Intent(this, HowToPlay.class);
        startActivity(intent);
    }

    private void openLevelSelectScreen() {
        Intent intent = new Intent(this, LevelSelect.class);
        startActivity(intent);
    }
}

