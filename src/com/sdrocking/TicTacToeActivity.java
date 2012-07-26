package com.sdrocking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sdrocking.Constants.*;

/**
 * @author Siddhartha Dugar
 */
public class TicTacToeActivity extends Activity {
	Intent gameIntent, scoreIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameIntent = new Intent(TicTacToeActivity.this, GameUI.class);
		scoreIntent = new Intent(TicTacToeActivity.this, ScoreUI.class);

		resetCounts();
		showOpponentSelector();
	}

	private void resetCounts() {
		Bundle extras = gameIntent.getExtras();
		if (extras != null) {
			scoreIntent.putExtra(Constants.PLAYED,
					extras.getInt(Constants.PLAYED, 0));
			scoreIntent
					.putExtra(Constants.WON, extras.getInt(Constants.WON, 0));
			scoreIntent.putExtra(Constants.LOST,
					extras.getInt(Constants.LOST, 0));
			String against;
			if (extras.getBoolean(Constants.AGAINSTCPU)
					&& extras.getString(Constants.DIFFICULTY) != null) {
				against = Winner.CPU.toString() + "-"
						+ extras.getString(Constants.DIFFICULTY);
			} else {
				against = Winner.HUMAN.toString();
			}
			scoreIntent.putExtra(Constants.AGAINSTCPU, against);
		} else {
			scoreIntent.putExtra(Constants.PLAYED, 0);
			scoreIntent.putExtra(Constants.WON, 0);
			scoreIntent.putExtra(Constants.LOST, 0);
			scoreIntent.putExtra(Constants.AGAINSTCPU,
					Winner.HUMAN.toString());
		}

		gameIntent.putExtra(Constants.PLAYED, 0);
		gameIntent.putExtra(Constants.WON, 0);
		gameIntent.putExtra(Constants.LOST, 0);
	}

	private void showOpponentSelector() {
		setContentView(R.layout.welcome);

		final Button bSetOpponent = (Button) findViewById(R.id.bSetOpponent);
		final RadioGroup rgOpponent = (RadioGroup) findViewById(R.id.rOpponent);

		bSetOpponent.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				switch (rgOpponent.getCheckedRadioButtonId()) {
				case R.id.rUser2:
					gameIntent.putExtra(Constants.AGAINSTCPU, false);
					startActivityForResult(gameIntent, 0);
					break;
				case R.id.rCPU:
				default:
					gameIntent.putExtra(Constants.AGAINSTCPU, true);
					showOptionsSelector();
					break;
				}
			}
		});

		rgOpponent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (rgOpponent.getCheckedRadioButtonId()) {
				case R.id.rUser2:
					bSetOpponent.setText("Start game");
					break;
				case R.id.rCPU:
				default:
					bSetOpponent.setText("Next");
					break;
				}
			}
		});

		Button bTopScore = (Button) findViewById(R.id.bTopScore);
		bTopScore.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(scoreIntent);
			}
		});

		int playedCount = scoreIntent.getExtras().getInt(Constants.PLAYED);
		int wonCount = scoreIntent.getExtras().getInt(Constants.WON);
		int lostCount = scoreIntent.getExtras().getInt(Constants.LOST);

		if (playedCount > 0) {
			TextView tvStats, tvPlayed, tvWon, tvLost;
			tvStats = (TextView) findViewById(R.id.tvStats);
			tvPlayed = (TextView) findViewById(R.id.tvPlayed);
			tvWon = (TextView) findViewById(R.id.tvWon);
			tvLost = (TextView) findViewById(R.id.tvLost);

			tvStats.setText("Statistics (for player 1):");
			tvPlayed.setText("Played: " + playedCount);
			tvWon.setText("Won: " + wonCount);
			tvLost.setText("Lost: " + lostCount);

			bTopScore.setText("Save your score");
		} else {
			bTopScore.setText("View top scores");
		}
	}

	private void showOptionsSelector() {
		setContentView(R.layout.options);

		Button bDifficulty = (Button) findViewById(R.id.bSetOptions);
		bDifficulty.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Difficulty difficulty;
				RadioGroup rgDifficulty = (RadioGroup) findViewById(R.id.rDifficulty);
				switch (rgDifficulty.getCheckedRadioButtonId()) {
				case R.id.rEasy:
					difficulty = Difficulty.EASY;
					break;
				case R.id.rHard:
					difficulty = Difficulty.HARD;
					break;
				case R.id.rMedium:
				default:
					difficulty = Difficulty.MEDIUM;
					break;
				}
				gameIntent.putExtra(Constants.DIFFICULTY, difficulty.toString());

				RadioGroup beginner = (RadioGroup) findViewById(R.id.rBegin);
				Boolean userBegins = (beginner.getCheckedRadioButtonId() == R.id.rUser);
				gameIntent.putExtra(Constants.USERBEGINS, userBegins);

				startActivityForResult(gameIntent, 0);
			}
		});

		Button bRestart = (Button) findViewById(R.id.bRestart);
		bRestart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showOpponentSelector();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			return;
		}

		Bundle resultBundle = data.getExtras();
		Winner winner = Winner.toWinner(resultBundle
				.getString(Constants.WINNERNAME));

		if (winner != Winner.NONE) {
			gameIntent.putExtra(Constants.PLAYED, gameIntent.getExtras()
					.getInt(Constants.PLAYED) + 1);

			if (winner == Winner.PLAYER) {
				gameIntent.putExtra(Constants.WON, gameIntent.getExtras()
						.getInt(Constants.WON) + 1);
			} else if (winner == Winner.CPU || winner == Winner.HUMAN) {
				gameIntent.putExtra(Constants.LOST, gameIntent.getExtras()
						.getInt(Constants.LOST) + 1);
			}
		}

		if (resultBundle.getBoolean(Constants.RESTART)) {
			startActivityForResult(gameIntent, 0);
		} else {
			resetCounts();
			showOpponentSelector();
		}
	}
}