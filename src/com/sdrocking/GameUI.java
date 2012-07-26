package com.sdrocking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.sdrocking.Constants.*;

/**
 * Android implementation of TicTacToe
 * 
 * @author Siddhartha Dugar
 */
public class GameUI extends Activity implements View.OnClickListener {
	TextView tvInput, tvResult;
	Button[] button;
	GameBackend game;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

		initUI();
		initGame();

		if (!game.getUserBegins()) {
			tvInput.setText("UserBegins: " + game.getUserBegins()
					+ ", Difficulty: " + game.getDifficulty().toString()
					+ "\nInput: ");

			makeCPUMove(game.getCPUMove());
		} else {
			tvInput.setText("Input: ");
		}
	}

	private void initGame() {
		Bundle data = getIntent().getExtras();
		boolean againstCPU = data.getBoolean(Constants.AGAINSTCPU);
		if (againstCPU) {
			boolean userBegins = data.getBoolean(Constants.USERBEGINS);
			Difficulty difficulty = Difficulty.toDifficulty(data
					.getString(Constants.DIFFICULTY));

			game = new GameBackend(againstCPU, userBegins, difficulty);
		} else {
			game = new GameBackend(false, true, Difficulty.EASY);
		}
	}

	private void initUI() {
		button = new Button[10];
		button[1] = (Button) findViewById(R.id.b1);
		button[2] = (Button) findViewById(R.id.b2);
		button[3] = (Button) findViewById(R.id.b3);
		button[4] = (Button) findViewById(R.id.b4);
		button[5] = (Button) findViewById(R.id.b5);
		button[6] = (Button) findViewById(R.id.b6);
		button[7] = (Button) findViewById(R.id.b7);
		button[8] = (Button) findViewById(R.id.b8);
		button[9] = (Button) findViewById(R.id.b9);
		for (int i = 1; i < 10; ++i) {
			button[i].setOnClickListener(this);
			button[i].setText("_");
			button[i].setTextSize(50);
			button[i].setTypeface(Typeface.MONOSPACE);
		}

		tvInput = (TextView) findViewById(R.id.tvInput);

		tvResult = (TextView) findViewById(R.id.tvResult);
		tvResult.setVisibility(View.GONE);

		Button bPlayAgain = (Button) findViewById(R.id.bPlayAgain);
		bPlayAgain.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString(Constants.WINNERNAME, game.getWinner().toString());
				bundle.putBoolean(Constants.RESTART, true);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		bPlayAgain.setVisibility(View.GONE);

		Button bFinish = (Button) findViewById(R.id.bFinish);
		bFinish.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString(Constants.WINNERNAME, game.getWinner().toString());
				bundle.putBoolean(Constants.RESTART, false);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	public void onClick(View v) {
		int userChoice = getClickSource((Button) v);

		if (game.getChance() == ButtonValue.PLAYER1) {
			tvInput.append(" " + userChoice);
			button[userChoice].setText("X");
			button[userChoice].setTextColor(Color.GREEN);
		} else {
			tvInput.append(" (" + userChoice + ")");
			button[userChoice].setText("O");
			button[userChoice].setTextColor(Color.RED);
		}

		button[userChoice].setEnabled(false);
		game.processMove(userChoice);

		if (game.isGameOver()) {
			handleGameOver();
		} else if (game.isAgainstCPU()) {
			makeCPUMove(game.getCPUMove());
		}
	}

	private void makeCPUMove(int cpuChoice) {
		tvInput.append(" (" + cpuChoice + ")");
		button[cpuChoice].setText("O");
		button[cpuChoice].setTextColor(Color.RED);
		button[cpuChoice].setEnabled(false);
		game.processMove(cpuChoice);

		if (game.isGameOver()) {
			handleGameOver();
		}
	}

	private void handleGameOver() {
		for (int i = 1; i < button.length; ++i) {
			button[i].setEnabled(false);
		}

		highlightWinComboIndex();

		String message;
		Winner winner = game.getWinner();
		if (winner == Winner.PLAYER) {
			message = "Congratulations Player 1, you have won!";
		} else if (winner == Winner.HUMAN) {
			message = "Congratulations Player 2, you have won!";
		} else if (winner == Winner.CPU) {
			message = "You've lost! Try harder next time.";
		} else {
			message = "The game has ended in a tie.";
		}

		tvResult.setVisibility(View.VISIBLE);
		tvResult.setText(message);

		((Button) findViewById(R.id.bPlayAgain)).setVisibility(View.VISIBLE);
	}

	private void highlightWinComboIndex() {
		int i = game.getWinComboIndex();
		if (i == -1) {
			return;
		}

		for (int j = 0; j < 3; ++j) {
			button[GameBackend.winCombo[i][j]].setText("$");
			button[GameBackend.winCombo[i][j]].setTextColor(Color.YELLOW);
		}
	}

	private int getClickSource(Button btn) {
		switch (btn.getId()) {
		case R.id.b1:
			return 1;
		case R.id.b2:
			return 2;
		case R.id.b3:
			return 3;
		case R.id.b4:
			return 4;
		case R.id.b5:
			return 5;
		case R.id.b6:
			return 6;
		case R.id.b7:
			return 7;
		case R.id.b8:
			return 8;
		case R.id.b9:
			return 9;
		default:
			return 0;
		}
	}
}
