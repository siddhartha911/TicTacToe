package com.sdrocking;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.sdrocking.Constants.*;

/**
 * Back-end code for supporting TicTacToe game play.
 * 
 * @author Siddhartha Dugar
 */
public class GameBackend {

	public static final int[][] winCombo = new int[][] { { 1, 5, 9 },
			{ 3, 5, 7 }, { 1, 4, 7 }, { 2, 5, 8 }, { 3, 6, 9 }, { 1, 2, 3 },
			{ 4, 5, 6 }, { 7, 8, 9 } };

	private boolean againstCPU, userBegins;
	private int turns, winComboIndex;

	private Difficulty difficulty;
	private Winner winner;
	private ButtonValue chance;
	private ButtonValue[] buttonValue;

	public GameBackend(boolean againstCPU, boolean userBegins,
			Difficulty difficulty) {
		this.againstCPU = againstCPU;
		this.userBegins = userBegins;
		this.difficulty = difficulty;

		turns = 0;
		winComboIndex = -1;
		chance = ButtonValue.PLAYER1;
		winner = Winner.NONE;

		buttonValue = new ButtonValue[10];
		for (int i = 1; i < 10; ++i) {
			buttonValue[i] = ButtonValue.BLANK;
		}

		chance = userBegins ? ButtonValue.PLAYER1 : ButtonValue.PLAYER2;
	}

	public int getWinComboIndex() {
		return winComboIndex;
	}

	public ButtonValue getChance() {
		return chance;
	}

	public boolean isAgainstCPU() {
		return againstCPU;
	}

	public Winner getWinner() {
		return winner;
	}

	public boolean getUserBegins() {
		return userBegins;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void processMove(int choice) {
		buttonValue[choice] = chance;
		++turns;
		chance = (chance == ButtonValue.PLAYER1) ? ButtonValue.PLAYER2
				: ButtonValue.PLAYER1;
	}

	public int getCPUMove() {
		if (turns >= 3) {
			// win if possible
			for (int i = 0; i < winCombo.length; ++i) {
				for (int j = 0; j < 3; ++j) {
					if (buttonValue[winCombo[i][j]] == ButtonValue.PLAYER2
							&& buttonValue[winCombo[i][(j + 1) % 3]] == ButtonValue.PLAYER2
							&& buttonValue[winCombo[i][(j + 2) % 3]] == ButtonValue.BLANK)
						return winCombo[i][(j + 2) % 3];
				}
			}

			// block opponent from winning
			for (int i = 0; i < winCombo.length; ++i) {
				for (int j = 0; j < 3; ++j) {
					if (buttonValue[winCombo[i][j]] == ButtonValue.PLAYER1
							&& buttonValue[winCombo[i][(j + 1) % 3]] == ButtonValue.PLAYER1
							&& buttonValue[winCombo[i][(j + 2) % 3]] == ButtonValue.BLANK)
						return winCombo[i][(j + 2) % 3];
				}
			}
		}

		if (difficulty == Difficulty.EASY) {
			// Play randomly in EASY mode
			Random rand = new Random();
			int choice;
			do {
				choice = 1 + rand.nextInt(buttonValue.length - 1);
			} while (buttonValue[choice] != ButtonValue.BLANK);
			return choice;
		}

		if (buttonValue[5] == ButtonValue.BLANK) {
			return 5;
		}

		List<Integer> edgeCenters;
		edgeCenters = Arrays.asList(2, 6, 8, 4);
		Collections.shuffle(edgeCenters);

		if (turns == 3 && userBegins) {
			// force opponent to defend if opposite corners are occupied
			if ((buttonValue[1] == ButtonValue.PLAYER1 && buttonValue[9] == ButtonValue.PLAYER1)
					|| (buttonValue[3] == ButtonValue.PLAYER1 && buttonValue[7] == ButtonValue.PLAYER1)) {
				return edgeCenters.get(0);
			}
		} else if (turns > 3 && difficulty == Difficulty.HARD) {
			// create a fork if possible
			for (int i = 1; i < buttonValue.length; ++i) {
				if (buttonValue[i] != ButtonValue.BLANK)
					continue;

				for (int j = 0; j < winCombo.length - 1; ++j) {
					for (int k = 0; k < 3; k++) {
						if (winCombo[j][k] != i)
							continue;
						if ((buttonValue[winCombo[j][(k + 1) % 3]] == ButtonValue.PLAYER2 && buttonValue[winCombo[j][(k + 2) % 3]] == ButtonValue.BLANK)
								|| (buttonValue[winCombo[j][(k + 1) % 3]] == ButtonValue.BLANK && buttonValue[winCombo[j][(k + 2) % 3]] == ButtonValue.PLAYER2)) {
							for (int l = j + 1; l < winCombo.length; ++l) {
								for (int m = 0; m < 3; ++m) {
									if (winCombo[l][m] != i)
										continue;
									if ((buttonValue[winCombo[l][(m + 1) % 3]] == ButtonValue.PLAYER2 && buttonValue[winCombo[l][(m + 2) % 3]] == ButtonValue.BLANK)
											|| (buttonValue[winCombo[l][(m + 1) % 3]] == ButtonValue.BLANK && buttonValue[winCombo[l][(m + 2) % 3]] == ButtonValue.PLAYER2))
										return i;
								}
							}
						}
					}
				}
			}

			// block opponent's fork if possible
			for (int i = 1; i < buttonValue.length; ++i) {
				if (buttonValue[i] != ButtonValue.BLANK)
					continue;

				for (int j = 0; j < winCombo.length - 1; ++j) {
					for (int k = 0; k < 3; k++) {
						if (winCombo[j][k] != i)
							continue;
						if ((buttonValue[winCombo[j][(k + 1) % 3]] == ButtonValue.PLAYER1 && buttonValue[winCombo[j][(k + 2) % 3]] == ButtonValue.BLANK)
								|| (buttonValue[winCombo[j][(k + 1) % 3]] == ButtonValue.BLANK && buttonValue[winCombo[j][(k + 2) % 3]] == ButtonValue.PLAYER1)) {
							for (int l = j + 1; l < winCombo.length; ++l) {
								for (int m = 0; m < 3; ++m) {
									if (winCombo[l][m] != i)
										continue;
									if ((buttonValue[winCombo[l][(m + 1) % 3]] == ButtonValue.PLAYER1 && buttonValue[winCombo[l][(m + 2) % 3]] == ButtonValue.BLANK)
											|| (buttonValue[winCombo[l][(m + 1) % 3]] == ButtonValue.BLANK && buttonValue[winCombo[l][(m + 2) % 3]] == ButtonValue.PLAYER1))
										return i;
								}
							}
						}
					}
				}
			}
		}

		List<Integer> corners = Arrays.asList(1, 3, 9, 7);
		Collections.shuffle(corners);

		if (difficulty == Difficulty.HARD) {
			for (int i = 0; i < corners.size(); ++i) {
				if (buttonValue[5] == ButtonValue.PLAYER2
						&& buttonValue[corners.get(i)] == ButtonValue.BLANK
						&& buttonValue[10 - corners.get(i)] == ButtonValue.BLANK)
					return corners.get(i);
			}

			for (int i = 0; i < edgeCenters.size(); ++i) {
				if (buttonValue[5] == ButtonValue.PLAYER2
						&& buttonValue[edgeCenters.get(i)] == ButtonValue.BLANK
						&& buttonValue[10 - edgeCenters.get(i)] == ButtonValue.BLANK)
					return edgeCenters.get(i);
			}
		}

		for (int i = 0; i < corners.size(); ++i) {
			if (buttonValue[corners.get(i)] == ButtonValue.BLANK)
				return corners.get(i);
		}

		for (int i = 0; i < edgeCenters.size(); ++i) {
			if (buttonValue[edgeCenters.get(i)] == ButtonValue.BLANK)
				return edgeCenters.get(i);
		}

		return 0;
	}

	public boolean isGameOver() {
		if (turns < 5) {
			return false;
		}

		for (int i = 0; i < winCombo.length; ++i) {
			if (buttonValue[winCombo[i][0]] != ButtonValue.BLANK
					&& buttonValue[winCombo[i][0]] == buttonValue[winCombo[i][1]]
					&& buttonValue[winCombo[i][0]] == buttonValue[winCombo[i][2]]) {
				winComboIndex = i;

				if ((userBegins && turns % 2 == 1)
						|| (againstCPU && !userBegins && turns % 2 == 0)) {
					winner = Winner.PLAYER;
				} else if (againstCPU) {
					winner = Winner.CPU;
				} else {
					winner = Winner.HUMAN;
				}

				return true;
			}
		}

		if (turns == 9) {
			winner = Winner.TIE;
			return true;
		}

		return false;
	}
}
