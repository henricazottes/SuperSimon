package com.simon.henri.supersimon;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Console;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.LogRecord;

import models.PushButton;


public class Game extends AppCompatActivity implements Observer {
        private ArrayList<PushButton> buttons;
        private int level;
        private ArrayList<Integer> sequence;
        private boolean gameOver;
        private LinkedList<Integer> inputs;
        private Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Create and add buttons to the layout, instantiate and initialize some variables
        init();

        // Instantiate and initialize variables related to a new instance of the game
        reset();

        // Wait the app is totally launched before blinking anything
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                while(true){
                    blinkAll(handler, 150); // fast blink, success / begening

                    // Add a random number between 0 and 3 in the sequence
                    sequence.add(r.nextInt(4));



                    // Show the sequence: blink each button corresponding to the random number
                    showSequence(handler);

                    // Unlock the buttons
                    PushButton.unlock();

                    // Wait the input of the player
                    getInputs();

                    // Prevent the user to push a button when the sequence is showed
                    PushButton.lock();

                    if(gameOver){
                        blinkAll(handler, 300); // slow blink, error...
                        reset();
                    }else{
                        level++;
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

        @Override
        protected void onResume(){
            super.onResume();

        }

        public void init(){

            r = new Random();

            ViewGroup firstLine = (ViewGroup) findViewById(R.id.firstLine);
            ViewGroup secondLine = (ViewGroup) findViewById(R.id.secondLine);

            inputs = new LinkedList<>();
            buttons = new ArrayList<>();

            buttons.add(0, new PushButton(getApplicationContext(), R.drawable.bt_blue, R.drawable.bt_blue_p, 0));
            buttons.add(1, new PushButton(getApplicationContext(), R.drawable.bt_green, R.drawable.bt_green_p, 1));
            buttons.add(2, new PushButton(getApplicationContext(), R.drawable.bt_red, R.drawable.bt_red_p, 2));
            buttons.add(3, new PushButton(getApplicationContext(), R.drawable.bt_yellow, R.drawable.bt_yellow_p, 3));

            for(int i = 0; i<4; i++){
                buttons.get(i).addObserver(this);
            }

            firstLine.addView(buttons.get(0).getImgButton());
            firstLine.addView(buttons.get(1).getImgButton());

            secondLine.addView(buttons.get(2).getImgButton());
            secondLine.addView(buttons.get(3).getImgButton());
        }

        public void reset(){
            sequence = new ArrayList<>();
            level = 0;
            gameOver = false;
        }


        /*
        Wait until the payer enter a wrong input or succeed in entring all the correct inputs
         */
        public void getInputs(){
            int index = 0;
            int nbInputs = 0;
            while(nbInputs <= level){
                if(inputs.size() != 0){
                    if(inputs.removeFirst() == sequence.get(index)){
                        nbInputs++;
                        index++;
                    }else{
                        gameOver = true;
                        break;
                    }
                }
            }
        }

    /*
    Blink a specified button
     */
    public void blink(Handler handler, int delay, final int index){
        handler.post(new Runnable(){
            @Override
            public void run() {
                buttons.get(sequence.get(index)).setPressed();
            }
        });

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                buttons.get(sequence.get(index)).setReleased();
            }
        });

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /*
    Blink all the buttons 4 times.
     */
    public void blinkAll(Handler handler, int delay){
        for(int i = 0; i < 3; i++){
            final int index = i;

            handler.post(new Runnable(){
                @Override
                public void run() {
                    buttons.get(0).setPressed();
                    buttons.get(1).setPressed();
                    buttons.get(2).setPressed();
                    buttons.get(3).setPressed();
                }
            });

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    buttons.get(0).setReleased();
                    buttons.get(1).setReleased();
                    buttons.get(2).setReleased();
                    buttons.get(3).setReleased();
                }
            });

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Show the sequence by blinking buttons
     */
    public void showSequence(Handler handler){
            for(int i = 0; i <= level; i++){
                final int index = i;
                blink(handler, 150, index);
            }
    }



    /*
    Update is called whend a button is pressed. It receive the id of the pressed button
     */

    @Override
    public void update(Observable observable, Object data) {
        inputs.add((int) data);
    }
}


