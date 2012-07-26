package com.sdrocking;

/**
 * Strings and Enums needed for TicTacToe.
 * 
 * @author Siddhartha Dugar
 */
public class Constants {
	final static String PLAYED = "playedcount", WON = "woncount",
			LOST = "lostcount", DIFFICULTY = "difficulty",
			AGAINSTCPU = "againstcpu", USERBEGINS = "userbegins",
			RESTART = "restartnow", WINNERNAME = "winnername";

	public enum ButtonValue {
		PLAYER1, PLAYER2, BLANK;
	}

	public enum Winner {
		PLAYER, HUMAN, CPU, TIE, NONE;

		@Override
		public String toString() {
			switch (this) {
			case PLAYER:
				return "player";
			case HUMAN:
				return "human";
			case CPU:
				return "cpu";
			case TIE:
				return "tie";
			case NONE:
			default:
				return "none";
			}
		}

		public static Winner toWinner(String winner) {
			if (winner.equals(PLAYER.toString())) {
				return PLAYER;
			} else if (winner.equals(HUMAN.toString())) {
				return HUMAN;
			} else if (winner.equals(CPU.toString())) {
				return CPU;
			} else if (winner.equals(TIE.toString())) {
				return TIE;
			} else {
				return NONE;
			}
		}
	};

	public enum Difficulty {
		EASY, MEDIUM, HARD;

		@Override
		public String toString() {
			switch (this) {
			case EASY:
				return "easy";
			case HARD:
				return "hard";
			case MEDIUM:
			default:
				return "medium";
			}
		}

		public static Difficulty toDifficulty(String difficulty) {
			if (difficulty.equals(EASY.toString())) {
				return EASY;
			} else if (difficulty.equals(HARD.toString())) {
				return HARD;
			} else {
				return MEDIUM;
			}
		}
	};
}
