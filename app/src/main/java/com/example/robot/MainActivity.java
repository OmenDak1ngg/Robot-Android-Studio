package com.example.robot;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridLayout gameGrid;

    TextView tvCommands;
    TextView tvPreviousCommands;
    TextView tvLevel;
    TextView tvTimer;
    TextView tvWin;

    Button btnUp;
    Button btnDown;
    Button btnLeft;
    Button btnRight;
    Button btnGo;
    Button btnClear;
    Button btnRestart;
    Button btnNewGame;

    int rows = 4;
    int cols = 4;

    int robotRow = 0;
    int robotCol = 0;

    int goalRow = 3;
    int goalCol = 3;

    int level = 1;

    int timeLimit = 30;

    boolean[][] walls;

    ArrayList<String> commands = new ArrayList<>();

    Handler handler = new Handler(Looper.getMainLooper());

    CountDownTimer timer;

    boolean robotError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameGrid = findViewById(R.id.gameGrid);

        tvCommands = findViewById(R.id.tvCommands);
        tvPreviousCommands = findViewById(R.id.tvPreviousCommands);
        tvLevel = findViewById(R.id.tvLevel);
        tvTimer = findViewById(R.id.tvTimer);
        tvWin = findViewById(R.id.tvWin);

        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnGo = findViewById(R.id.btnGo);
        btnClear = findViewById(R.id.btnClear);
        btnRestart = findViewById(R.id.btnRestart);
        btnNewGame = findViewById(R.id.btnNewGame);

        createLevel();

        btnUp.setOnClickListener(v -> addCommand("U"));
        btnDown.setOnClickListener(v -> addCommand("D"));
        btnLeft.setOnClickListener(v -> addCommand("L"));
        btnRight.setOnClickListener(v -> addCommand("R"));

        btnClear.setOnClickListener(v -> {
            commands.clear();
            updateCommandsText();
        });

        btnRestart.setOnClickListener(v -> {

            robotRow = 0;
            robotCol = 0;

            commands.clear();

            updateCommandsText();

            btnUp.setEnabled(true);
            btnDown.setEnabled(true);
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
            btnGo.setEnabled(true);

            drawGrid();

            startTimer();
        });
        btnNewGame.setOnClickListener(v -> {

            level = 1;

            rows = 4;
            cols = 4;

            robotRow = 0;
            robotCol = 0;

            goalRow = rows - 1;
            goalCol = cols - 1;

            timeLimit = 30;

            commands.clear();

            tvPreviousCommands.setText("Предыдущая команда:");

            tvLevel.setText("Уровень 1");

            tvWin.setVisibility(TextView.GONE);
            btnNewGame.setVisibility(Button.GONE);

            tvLevel.setVisibility(TextView.VISIBLE);
            tvTimer.setVisibility(TextView.VISIBLE);
            tvCommands.setVisibility(TextView.VISIBLE);
            tvPreviousCommands.setVisibility(TextView.VISIBLE);

            gameGrid.setVisibility(GridLayout.VISIBLE);

            btnUp.setVisibility(Button.VISIBLE);
            btnDown.setVisibility(Button.VISIBLE);
            btnLeft.setVisibility(Button.VISIBLE);
            btnRight.setVisibility(Button.VISIBLE);
            btnGo.setVisibility(Button.VISIBLE);
            btnClear.setVisibility(Button.VISIBLE);
            btnRestart.setVisibility(Button.VISIBLE);

            btnUp.setEnabled(true);
            btnDown.setEnabled(true);
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
            btnGo.setEnabled(true);

            updateCommandsText();

            createLevel();
        });
        btnGo.setOnClickListener(v -> {
            if(commands.size() == 0) {
                Toast.makeText(this, "Введите команды", Toast.LENGTH_SHORT).show();
                return;
            }

            executeCommands(0);
        });
    }

    void createLevel() {

        walls = new boolean[rows][cols];

        if(level == 1) {
            walls[1][1] = true;
        }

        if(level == 2) {
            walls[1][1] = true;
            walls[2][2] = true;
            walls[3][1] = true;
        }

        if(level >= 3) {
            walls[1][1] = true;
            walls[1][2] = true;
            walls[2][2] = true;
            walls[3][3] = true;
            walls[4][2] = true;
        }

        drawGrid();

        startTimer();
    }

    void drawGrid() {

        gameGrid.removeAllViews();

        gameGrid.setRowCount(rows);
        gameGrid.setColumnCount(cols);

        for(int r = 0; r < rows; r++) {

            for(int c = 0; c < cols; c++) {

                TextView cell = new TextView(this);

                cell.setWidth(120);
                cell.setHeight(120);

                cell.setGravity(Gravity.CENTER);

                cell.setTextSize(20);

                if(walls[r][c]) {

                    cell.setBackgroundColor(Color.BLACK);
                    cell.setText("X");
                    cell.setTextColor(Color.WHITE);

                }
                else if(r == robotRow && c == robotCol) {

                    if(robotError) {
                        cell.setBackgroundColor(Color.RED);
                    }
                    else {
                        cell.setBackgroundColor(Color.GREEN);
                    }

                    cell.setText("R");

                }
                else if(r == goalRow && c == goalCol) {

                    cell.setBackgroundColor(Color.YELLOW);
                    cell.setText("G");

                }
                else {

                    cell.setBackgroundColor(Color.LTGRAY);
                }

                GridLayout.LayoutParams params =
                        new GridLayout.LayoutParams();

                params.setMargins(5,5,5,5);

                cell.setLayoutParams(params);

                gameGrid.addView(cell);
            }
        }
    }

    void addCommand(String command) {

        commands.add(command);

        updateCommandsText();
    }

    void updateCommandsText() {

        String text = "Команды: ";

        for(String cmd : commands) {

            switch (cmd) {

                case "U":
                    text += "↑ ";
                    break;

                case "D":
                    text += "↓ ";
                    break;

                case "L":
                    text += "← ";
                    break;

                case "R":
                    text += "→ ";
                    break;
            }
        }

        tvCommands.setText(text);
    }
    void savePreviousCommands() {

        String text = "Предыдущая команда: ";

        for(String cmd : commands) {

            switch (cmd) {

                case "U":
                    text += "↑ ";
                    break;

                case "D":
                    text += "↓ ";
                    break;

                case "L":
                    text += "← ";
                    break;

                case "R":
                    text += "→ ";
                    break;
            }
        }

        tvPreviousCommands.setText(text);
    }

    void executeCommands(int index) {

        if(index >= commands.size()) {

            savePreviousCommands();

            commands.clear();

            updateCommandsText();

            checkWin();

            return;
        }

        String cmd = commands.get(index);

        int newRow = robotRow;
        int newCol = robotCol;

        switch (cmd) {

            case "U":
                newRow--;
                break;

            case "D":
                newRow++;
                break;

            case "L":
                newCol--;
                break;

            case "R":
                newCol++;
                break;
        }

        if(newRow >= 0 &&
                newRow < rows &&
                newCol >= 0 &&
                newCol < cols &&
                !walls[newRow][newCol]) {

            robotRow = newRow;
            robotCol = newCol;

            drawGrid();

            handler.postDelayed(() ->
                    executeCommands(index + 1), 500);

        }
        else {

            robotError = true;

            drawGrid();

            handler.postDelayed(() -> {

                robotError = false;

                drawGrid();

                executeCommands(index + 1);

            }, 200);
        }
    }

    void checkWin() {

        if(robotRow == goalRow &&
                robotCol == goalCol) {

            Toast.makeText(this,
                    "Уровень пройден!",
                    Toast.LENGTH_LONG).show();

            nextLevel();

        } else {

            Toast.makeText(this,
                    "Робот не дошел",
                    Toast.LENGTH_LONG).show();
        }
    }

    void nextLevel() {

        if(level >= 3) {

            gameGrid.setVisibility(GridLayout.GONE);

            tvLevel.setVisibility(TextView.GONE);
            tvTimer.setVisibility(TextView.GONE);
            tvCommands.setVisibility(TextView.GONE);
            tvPreviousCommands.setVisibility(TextView.GONE);

            btnUp.setVisibility(Button.GONE);
            btnDown.setVisibility(Button.GONE);
            btnLeft.setVisibility(Button.GONE);
            btnRight.setVisibility(Button.GONE);
            btnGo.setVisibility(Button.GONE);
            btnClear.setVisibility(Button.GONE);
            btnRestart.setVisibility(Button.GONE);

            tvWin.setVisibility(TextView.VISIBLE);

            btnNewGame.setVisibility(Button.VISIBLE);

            return;
        }

        level++;

        rows++;
        cols++;

        robotRow = 0;
        robotCol = 0;

        goalRow = rows - 1;
        goalCol = cols - 1;

        commands.clear();

        timeLimit -= 5;

        if(timeLimit < 10) {
            timeLimit = 10;
        }

        tvLevel.setText("Уровень " + level);

        tvPreviousCommands.setText("Предыдущая команда:");

        updateCommandsText();

        createLevel();
    }

    void startTimer() {

        if(timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(timeLimit * 1000L, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                tvTimer.setText(
                        "Время: " +
                                millisUntilFinished / 1000
                );
            }

            @Override
            public void onFinish() {

                Toast.makeText(MainActivity.this,
                        "Время вышло",
                        Toast.LENGTH_LONG).show();

                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
                btnLeft.setEnabled(false);
                btnRight.setEnabled(false);
                btnGo.setEnabled(false);
            }
        };

        timer.start();
    }
}