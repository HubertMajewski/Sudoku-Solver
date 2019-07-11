/*
 * @author Hubert
 */
public final class Main {

	public static void main(String[] args) {
		// Avg time
		long avg = 0;

		// Create array for 4 Sudokus
		SudokuGrid sudokus[] = new SudokuGrid[4];

		// Setup easy0.csv
		try {
			sudokus[0] = new SudokuGrid("easy0.csv");
		} catch (java.io.IOException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Setup easy1.csv
		try {
			sudokus[1] = new SudokuGrid("easy1.csv");
		} catch (java.io.IOException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Setup hard0.csv
		try {
			sudokus[2] = new SudokuGrid("hard0.csv");
		} catch (java.io.IOException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Setup hard1.csv
		try {
			sudokus[3] = new SudokuGrid("hard1.csv");
		} catch (java.io.IOException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Solve, print, and write all Sudokus
		for (int i = 0; i < 4; i++) {
			avg += sudokus[i].solveSudoku();
			sudokus[i].printGrid();
			sudokus[i].writeGridToFile(i + ".csv");
		}

		avg /= sudokus.length;

		System.out.println("Average time in ms: " + avg / 1000000 + "." + avg % 1000000);
	}

}
