package com.dillon.clapp.catchphrase;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private String[] words;

    private int count = 0;

    private boolean firstTick = true;
    private boolean firstWord = true;
    private boolean vibrationOccurred;

    private String difficulty = "easy";
    private String wordsList;

    private long startTime = 0;
    private long randomTime;

    private List<String> wordList;

    ArrayActions a1 = new ArrayActions();

    private Vibrator endPoint;

    private Button redBtn;
    private Button blueBtn;

    private TextView timerTextView;
    private RelativeLayout layout;

    private AnimationDrawable flashDrawable = new AnimationDrawable();

    private Handler timerHandler = new Handler();
    private Handler flashAnimationHandler = new Handler();

    private Runnable flashAnimationRunnable = new Runnable() {
        @Override
        public void run(){
            flashDrawable.start();
        }
    };

    private long lastRun = 0;
    private double delay = 5000;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int trueSeconds = (int) millis / 1000;
            double seconds = (double) (millis / 1000);
            int minutes = trueSeconds / 60;
            //seconds %= 60;

            //Initializes the Vibrator
            Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            //Sound Handling
            //MediaPlayer tickPhaseOne = MediaPlayer.create(getApplicationContext(), R.raw.tick_phase_one);
            //MediaPlayer tickPhaseTwo = MediaPlayer.create(getApplicationContext(), R.raw.tick_phase_two);

            //Initializes the TextView that checks to see if the the timing of the vibrations is working
            TextView vibrateTimeCheck = (TextView) findViewById(R.id.vibrateTimingCheck);

            //Log.d("d", "deez nuts " + delay);

            if (lastRun == 0)
            {
                lastRun = System.currentTimeMillis();
            }

            if (randomTime - (System.currentTimeMillis() - startTime) < 5000)
            {
                delay = 200;

                if (System.currentTimeMillis() - lastRun > delay) {
                    lastRun = System.currentTimeMillis();
                    vibrate.vibrate(100);
                }
            }
            else if (System.currentTimeMillis() - lastRun > delay)
            {
                delay /= 1.119;
                lastRun = System.currentTimeMillis();
                vibrate.vibrate(500);
            }

            timerTextView.setText(minutes + ":" + seconds);

            timerHandler.postDelayed(this, 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        timerTextView = (TextView) findViewById(R.id.timer);
        layout = (RelativeLayout) findViewById(R.id.layout);
        redBtn = (Button) findViewById(R.id.red_point);
        blueBtn = (Button) findViewById(R.id.blue_point);

        //Disables the points buttons
        redBtn.setEnabled(false);
        blueBtn.setEnabled(false);

    }

    public void newWordClick(View view){

        //Initializes the boolean variable that will check if the game is starting
        boolean firstClick = false;

        //Casts the view into a button
        Button btn = (Button) view;

        //Initializes the TextView that displays the word
        TextView word = (TextView) findViewById(R.id.selected_word);

        if(word.getText().equals("Hit the Button"))
            firstClick = true;

        //If this is the first time the game is being started with a new list, a new list is chosen at random according to their selection on the previous activity
        if(firstClick) {

            //Determines which word list to choose from
            Intent myIntent = getIntent();
            String clickType = "not anything";
            clickType = myIntent.getStringExtra("clickType");
            String file = a1.chooseSetType(clickType);

            //Fetches the file from the project and splits the long String into multiple words
            wordList = a1.getWordsFromFile(file, MainActivity.this);
        }

        //If this is the first word to start a round
        if(firstWord) {

            //Sets the firstWord boolean to be false so that the timer will start only once per round
            firstWord = false;

            //Starts the timer to run for a random time between 40 and 49 seconds
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 1);

            //Disables the point buttons
            redBtn.setEnabled(false);
            blueBtn.setEnabled(false);

            //Declares variables used to calculate the random time
            int min = 40000;
            int max = 49999;
            randomTime = (long) (Math.random() * (min - max) + max);

            //Starts the handler for the timing of the timer
            timerHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // handler has reached the time
                    // now update the TextView using a runOnUiThread
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // Update UI elements

                            //finally update the TextView
                            timerTextView.setText("Time is Up!");
                            timerHandler.removeCallbacks(timerRunnable);

                            //Vibrates the phone to signal the round is up
                            endPoint = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            //vibrator.vibrate(5000);
                            endPoint.vibrate(5000);

                            //Sets the boolean that checks if the vibration for victory has occurred
                            vibrationOccurred = true;

                            //Handles the flashing background
                            //Initializes the Animation drawable
                            final AnimationDrawable drawable = new AnimationDrawable();

                            //Adds the two colors that will flash to the AnimationDrawable
                            drawable.addFrame(new ColorDrawable(Color.RED), 500);
                            drawable.addFrame(new ColorDrawable(Color.WHITE), 500);

                            flashDrawable = drawable;
                            flashDrawable.setOneShot(false);

                            //Causes the background to flash between the above colors
                            layout.setBackground(drawable);
                            flashAnimationHandler.postDelayed(flashAnimationRunnable, 100);

                            //Initializes the newWordBtn
                            Button wordBtn = (Button) findViewById(R.id.switch_word);

                            //Causes the newWordBtn to be disabled
                            wordBtn.setEnabled(false);

                            //Causes the newWordBtn to be transparent
                            wordBtn.setAlpha(0);

                            //Re-enables the point buttons
                            redBtn.setEnabled(true);
                            blueBtn.setEnabled(true);

                        }
                    });

                }
            }, randomTime);
        }

        //Sets the TextView of the display word to be the next word in the word List
        word.setText(wordList.get(count));

        //Increments count
        count++;

    }

    public void pointClick(View view){

        //Creates the button instance and sets it equal to the button that was passed over
        Button btn = (Button) view;

        //Changes the firstWord to be true so that a new round can start
        firstWord = true;

        //Victory Strings
        String blueVictory = "BLUE TEAM WINS";
        String redVictory = "RED TEAM WINS";

        //Initializes TextViews for the point totals
        TextView redPointTotal = (TextView) findViewById(R.id.red_point_total);
        TextView bluePointTotal = (TextView) findViewById(R.id.blue_point_total);

        //Cancels the vibrator when the point is scored
        if(vibrationOccurred)
            endPoint.cancel();

        //Initializes the newWordBtn
        Button wordBtn = (Button) findViewById(R.id.switch_word);

        //Re-enables the button
        wordBtn.setEnabled(true);

        //Makes the button visible again
        wordBtn.setAlpha(1);

        //Handles a click to the blue point button
        if(btn.getText().equals("Blue")) {

            if(!bluePointTotal.getText().equals(blueVictory)) {

                //Determines the total amount of points that blue team currently has
                int points = Integer.parseInt(bluePointTotal.getText().toString());

                //Adds a point for winning the previously completed round
                points++;

                //Sets the text of the blue points TextView to be the current amount of points
                bluePointTotal.setText(String.valueOf(points));

                //Handles when blue team wins
                if (points >= 7)
                    bluePointTotal.setText("BLUE TEAM WINS");

                //Turns off the flashing background
                flashDrawable.stop();

                //Sets the background to be white of the app
                layout.setBackgroundColor(Color.WHITE);
            }

        }

        //Handles a click to the red point button
        else if(btn.getText().equals("Red")){

            if(!redPointTotal.getText().equals(redVictory)) {

                //Determines the total amount of points that red team currently has
                int points = Integer.parseInt(redPointTotal.getText().toString());

                //Adds a point for winning the previously completed round
                points++;

                //Sets the text of the red points TextView to be the current amount of points
                redPointTotal.setText(String.valueOf(points));

                //Handles when red team wins
                if (points >= 7)
                    redPointTotal.setText("RED TEAM WINS");

                //Turns off the background flashing
                flashDrawable.stop();

                //Sets the background color to be white of the game
                layout.setBackgroundColor(Color.WHITE);
            }
        }

        //Disables the point buttons until another round has ended
        redBtn.setEnabled(false);
        blueBtn.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
