package com.game.lightsout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.game.myfirstapp.R;

/**
 * This screen describes to users how to play lights out. UI elements are in the corresponding
 * XML file. This code simply handles the button press to take users to the level selection screen
 * after they are familiar with how to play.
 *
 * @author William Boyles
 */
public class HowToPlay extends AppCompatActivity {
    private Button letsPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        letsPlay = findViewById(R.id.letsPlayButton);
        letsPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLevelSelectScreen();
            }
        });
    }

    private void openLevelSelectScreen() {
        Intent intent = new Intent(this, LevelSelect.class);
        startActivity(intent);
    }
}
