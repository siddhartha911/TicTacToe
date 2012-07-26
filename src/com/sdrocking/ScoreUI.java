package com.sdrocking;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Handles and stores scores for TicTacToe.
 * 
 * @author Siddhartha Dugar
 */
public class ScoreUI extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);

		refreshScoreTable();

		Bundle bundle = getIntent().getExtras();
		final int playedCount = bundle.getInt(Constants.PLAYED);
		final int wonCount = bundle.getInt(Constants.WON);
		final int lostCount = bundle.getInt(Constants.LOST);
		final String against = bundle.getString(Constants.AGAINSTCPU);
		System.out.print("In scoreUI, Score: " + playedCount + ", " + wonCount
				+ ", " + lostCount + "\n");

		final Button bBack = (Button) findViewById(R.id.bBack);
		bBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		final EditText etName = ((EditText) findViewById(R.id.etName));
		final Button bAddScore = (Button) findViewById(R.id.bAddScore);

		if (playedCount == 0) {
			etName.setVisibility(View.GONE);
			bAddScore.setVisibility(View.GONE);
			((TextView) findViewById(R.id.tvUsername)).setVisibility(View.GONE);
			return;
		}

		String defaultUser = bundle.getString("defaultUser");
		if (defaultUser != null && !defaultUser.equals("")) {
			etName.setText(defaultUser);
		}

		bAddScore.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String userName = etName.getText().toString();
				if (userName.equals("")) {
					return;
				}

				DbHandler entry = new DbHandler(ScoreUI.this);
				entry.open();
				entry.updateOrSave(userName, playedCount, wonCount, lostCount,
						against);
				entry.close();

				etName.setEnabled(false);
				bAddScore.setVisibility(View.GONE);
				try {
					bBack.setSelected(true);
				} catch (Exception e) {
				}

				refreshScoreTable();
			}
		});
	}

	private void refreshScoreTable() {
		DbHandler info = new DbHandler(this);
		info.open();
		// TODO: make this Async
		SparseArray<String> scores = info.getScores();
		// System.out.print(info.convertScoresToString(scores));
		info.close();

		TableLayout tableScore = (TableLayout) findViewById(R.id.tableScore);
		if (tableScore.getChildCount() > 1) {
			tableScore.removeViews(1, tableScore.getChildCount() - 1);
		}

		for (int i = scores.size() - 1; i >= 0; --i) {
			TableRow tableRow = new TableRow(this);
			tableRow.setLayoutParams(((TableRow) findViewById(R.id.trHeader))
					.getLayoutParams());

			String[] parts = scores.valueAt(i).split(" ");
			for (int j = 0; j < parts.length; ++j) {
				TextView textView = new TextView(this);
				textView.setText(parts[j]);
				if (j == 0) {
					textView.setLayoutParams(((TextView) findViewById(R.id.thUserName))
							.getLayoutParams());
				} else {
					textView.setLayoutParams(((TextView) findViewById(R.id.thPlayed))
							.getLayoutParams());
				}

				tableRow.addView(textView);
			}

			tableScore.addView(tableRow);
		}
	}
}
