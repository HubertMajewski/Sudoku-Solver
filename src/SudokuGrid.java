
/**
 *
 * @author Hubert
 */
public class SudokuGrid {

	// Given numbers w/ their indecies
	private final java.util.HashMap<Byte, Byte> givenIndecies = new java.util.HashMap<>(81);

	// Stores the next coords in the que to process
	private final java.util.PriorityQueue<Byte> nextCoords = new java.util.PriorityQueue<>();

	// The grid of the sudoku
	private final byte grid[] = new byte[81];

	// Track the number of elements in a given row, col or block
	private final byte rowFill[] = new byte[9], colFill[] = new byte[9], blockFill[] = new byte[9];

	//The object linked to a file if the constructure with the fileName chas been called
	private final FileReader input;

	/**
	 * Create a new Sudoku that can be solved using solveSudoku()
	 *
	 * @param grid a grid containing set values for parsing
	 *
	 * @see solveSudoku()
	 */
	public SudokuGrid(byte grid[]) {
		input = null;

		if (grid.length == 0)
			throw new IllegalArgumentException("Grid variable cannot be null or of size 0.");

		// A custom parseIntoGrid
		for (byte i = 0; i < grid.length; i++) {
			this.grid[i] = grid[i];

			if (grid[i] / 10 == 0 & grid[i] != 0) {
				givenIndecies.put(i, grid[i]);
			}
		}

		if (givenIndecies.size() < 17) {
			// http://pi.math.cornell.edu/~mec/Summer2009/Mahmood/More.html
			System.out.println(
				"The given file contains less than 17 given numbers and therefore, may have more than one answer.");
		}
	}

	/**
	 * Create a new Sudoku that can be solved using solveSudoku()
	 *
	 * @see solveSudoku()
	 * @param fileName A given file to be parsed. File format must be a CSV file.
	 *
	 * @throws java.io.IOException If there is a problem with reading/parsing the
	 *                             file, an IOException is thrown.
	 */
	public SudokuGrid(String fileName) throws java.io.IOException {

		// Try to setup the file reader if file is valid
		input = new FileReader(fileName);

		// Parse from file to object
		parseIntoGrid();

		if (givenIndecies.size() < 17) {
			// http://pi.math.cornell.edu/~mec/Summer2009/Mahmood/More.html
			System.out.println(
				"The given file contains less than 17 given numbers and therefore, may have more than one answer.");
		}

	}

	/**
	 * Solves the Sudoku grid. If there is more than 1 solution, the first solution
	 * this alg. computes will be the final one.
	 *
	 * @return the amount of time in ns it took to solve the Sudoku.
	 *
	 * @author Hubert
	 */
	public long solveSudoku() {

		// Runtime calc
		long startTime = System.nanoTime();

		// Start filling in grid starting from a semi-filled group
		setNextCoord();
		tryNumber(nextCoords.remove());

		// Runtime calc end
		long endTime = System.nanoTime();

		// Print runtime time in ms
		System.out.println(
			"Runtime in MS: " + (endTime - startTime) / 1000000 + "." + (endTime - startTime) % 1000000 + "\n");
		return endTime - startTime;

	}

	/**
	 * Prints the graph stored in the array of this object to System.out
	 *
	 * @author Hubert
	 */
	public void printGrid() {
		for (int i = 0; i < 81; i++) {

			// Horizontal Break
			if (i % 27 == 0 & i != 0) {
				System.out.println();
				for (int j = 0; j < 8; j++)
					System.out.print("--  ");
				System.out.println();
			} else if (i % 9 == 0 & i != 0) {
				System.out.println();
			}

			// Vertical Break
			if (i % 3 == 0 & i % 9 != 0)
				System.out.print("| ");

			// Print Numbers
			System.out.print(grid[i] + "  ");

		}
		// Two clean lines
		System.out.println("\n");
	}

	/**
	 * Like printGrid() except it would print the grid into a given fileName in CSV
	 * format If an IOException is thrown internally, then the file will not be
	 * written to.
	 *
	 * @param fileName The name of the file to write to. Will be written in CSV
	 *                 format
	 *
	 * @see printGrid()
	 * @author Hubert
	 */
	public void writeGridToFile(String fileName) {
		FileLogger output = new FileLogger(fileName);

		String line = "";
		for (int i = 0; i < 81; i++) {
			if (i % 9 == 8) {
				line += grid[i] + "\n";
			} else if (grid[i] != 0)
				line += grid[i] + ",";
			else
				line += ",";
		}
		output.writeLine(line);

	}

	// Taking the fileinput and parsing it into the array
	private void parseIntoGrid() {
		String currLine;
		byte gridPlacement = 0;
		try {
			for (int i = 0; i < 9; i++) { // Must be 9 rows (lines)
				// For Every input line
				currLine = input.readNextLine();

				// Split by comma
				String[] temp = new String[9];
				int c = 0;
				char prev = ',';
				for (int j = 0; j < currLine.length(); j++) {
					if (currLine.charAt(j) == ',' & prev == ',') {
						temp[c++] = "";
					}
					if (Character.isDigit(currLine.charAt(j)))
						temp[c++] = String.valueOf(currLine.charAt(j));
					prev = currLine.charAt(j);
				}

				currLine = "";
				for (int j = 0; j < 9; j++) {
					String x = temp[j];
					// Since .isBlank() is only available in java 11, use
					// .trim().isEmpty() as a substitute.
					if (x == null || x.trim().isEmpty())
						x = "0";
					currLine += x.charAt(0);
				}

				for (int j = 0; j < currLine.length(); j++) {
					grid[gridPlacement] = Byte.valueOf(String.valueOf(currLine.charAt(j)));
					if (grid[gridPlacement] != 0)
						givenIndecies.put(gridPlacement, Byte.valueOf(String.valueOf(currLine.charAt(j))));
					gridPlacement++;
				}

			}
		} catch (EndOfFileException e) {
			System.out.println("Cannot parse given file due to " + e.getMessage() + "\nCheck your input file syntax.");
		}
	}

	private boolean tryNumber(byte coords) {
		// If there are no more coords passed in from getNextCoord() sudoku is finished
		if (coords == -1) {
			return true;
		}

		// Try a integer between 1 and 9 (inclusive) to fit onto coords
		for (byte i = 1; i < 10; i++) {
			if (isValid(i, coords)) {

				// Place given number into a grid if it is correct
				grid[coords] = i;

				// Increment that a number has been placed
				incrementAtCoords(coords);

				//Sets the next coord into the priority queue
				setNextCoord();

				
				Byte nextCoord = nextCoords.poll();
				if (nextCoord != null && tryNumber(nextCoord))
					return true; // Successfully placed said number ahead

				// Could not place a valid number ahead so backtrack and try next number
				grid[coords] = 0;
				decremenetAtCoords(coords);

				// Try next number
			}
		}

		// Could not find any number (1-9) to fit into given coords
		return false;
	}

	// Let 0-8 be the row, 9-18 the column, and 19-28 the block id (-1 for none)
	// This variable keeps track of the last cell that had a number added into it
	private byte lastCell = -1;

	// Gets next closest candidate value
	private void setNextCoord() {

		// if the last cell does not exist
		if (lastCell == -1) {

			// The index of the row/col/block with the most elements
			byte largestRow = 0, largestCol = 0, largestBlock = 0;

			// Get cell of largest number of elements
			for (byte i = 1; i < 9; i++) {
				if (rowFill[i] > rowFill[largestRow])
					largestRow = i;
				if (colFill[i] > colFill[largestCol])
					largestCol = i;
				if (blockFill[i] > blockFill[largestBlock])
					largestBlock = i;
			}

			// If the largest row, column, and block indecies have 9 elements, then the grid
			// is full.
			if (rowFill[largestRow] == 9 & colFill[largestCol] == 9 & blockFill[largestBlock] == 9) {
				nextCoords.add((byte) -1);
				return;
			}
			//	return (byte) -1;

			// If the row with the most elements is larger than the col or block with most
			// elements then..
			// If the largest row has more elements return next blank coord.
			if (rowFill[largestRow] >= colFill[largestCol] & rowFill[largestRow] >= blockFill[largestBlock]) {

				lastCell = largestRow;

				// Return the index of the next empty slot of the row
				for (byte i = (byte) (largestRow * 9); i < 81; i++) {
					if (grid[i] == 0) {
						nextCoords.add((byte) i);
						return;
					}
				}
			} else // Largest row of elements must be smaller than col and block
			if (colFill[largestCol] >= blockFill[largestBlock]) {
				// Return the index of the next empty slot of the row

				lastCell = (byte) (9 + largestCol);

				for (byte i = largestCol; i < 81; i += 9) {
					if (grid[i] == 0) {
						nextCoords.add((byte) i);
						return;
					}
				}
			} else {
				// The block must have the largest number of elements
				int blockx = largestBlock % 3;
				int blocky = largestBlock / 3;
				int bCoords = blockx * 3 + blocky * 27;

				lastCell = (byte) (19 + largestBlock);

				for (byte i = 0; i < 3; i++)
					for (byte j = 0; j < 3; j++)
						if (grid[bCoords + i + j * 9] == 0) {

							nextCoords.add((byte) (bCoords + i + j * 9));
							return;
						}

			}
		} else {
			// Try to find next open number in given group
			if (lastCell < 9) {
				// Return the index of the next empty slot of the most filled row
				for (int i = lastCell * 9; i < 81; i++) {
					if (grid[i] == 0) {
						nextCoords.add((byte) i);
						return;
					}
				}
			} else if (lastCell > 8 & lastCell < 19) {
				// Return the index of the next empty slot of the most filled vertical
				for (byte i = (byte) lastCell; i < 81; i += 9) {
					if (grid[i] == 0) {
						nextCoords.add((byte) i);
						return;
					}
				}
			} else {

				int blockx = lastCell % 3;
				int blocky = lastCell / 3;
				int bCoords = blockx * 3 + blocky * 27;

				for (byte i = 0; i < 3; i++)
					for (byte j = 0; j < 3; j++)
						if (grid[bCoords + i + j * 9] == 0) {

							nextCoords.add((byte) (bCoords + i + j * 9));
							return;
						}

			}
		}
		
		//If no other coords can be found, that means there are none available and the grid must be full.
		nextCoords.add((byte) -1);
	}

	// Increment the row's, columns, and block's element number to state a number
	// has been placed
	private void incrementAtCoords(byte coords) {
		colFill[coords % 9]++;
		rowFill[coords / 9]++;

		byte blockx = (byte) ((coords % 9) / 3);
		byte blocky = (byte) (coords / 27);

		blockFill[(byte) (blocky + blockx)]++;
	}

	// Decrement the row's, columns, and block's element number to state a number
	// has been removed
	private void decremenetAtCoords(byte coords) {
		// If statements takes care of invalid negative number of elements in a case of
		// no solution
		if (colFill[coords % 9] != 0)
			colFill[coords % 9]--;
		if (rowFill[coords / 9] != 0)
			rowFill[coords / 9]--;

		byte blockx = (byte) ((coords % 9) / 3);
		byte blocky = (byte) (coords / 27);

		if (blockFill[(byte) (blocky + blockx)] != 0)
			blockFill[(byte) (blocky + blockx)]--;

	}

	// Check if a given number x is placeable at coords
	private boolean isValid(byte x, byte coords) {
		return validBlock(x, coords) && validHorizontal(x, coords) && validVertical(x, coords);
	}

	// Check if there is no given x in the vertical of the coords given
	private boolean validVertical(byte x, byte coords) {
		byte column = (byte) (coords % 9);

		for (int i = column; i < 81; i += 9) {
			if (grid[i] == x)
				return false;
		}

		return true;
	}

	// Check if there is no given x in the horixontal of the coords given
	private boolean validHorizontal(byte x, byte coords) {
		byte row = (byte) (coords / 9);

		for (int i = 0; i < 9; i++)
			if (grid[row * 9 + i] == x)
				return false;

		return true;
	}

	// Check if there is no given x in a block of the coords given
	private boolean validBlock(byte x, byte coords) {
		// Base coords of a block in x and y
		byte blockx = (byte) ((coords % 9) / 3);
		byte blocky = (byte) (coords / 27);

		// Top right coords of blocks (in 1D)
		byte blockCoords = (byte) (blocky * 27 + blockx * 3);

		// Check if number exists in the block
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[blockCoords + i + j * 9] == x)
					return false;
			}
		}

		return true;
	}

} // EOC (End of Class)
