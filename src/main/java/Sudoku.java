import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	private final int[][] grid;
	private int numSolutions;
	private String solutionText;
	private long elapsed;
	private final List<Spot> emptySpots;


	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		checkValidityOfInput(text);

		int[] nums = stringToInts(text);

		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers,\nbut got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}


	private static void checkValidityOfInput(String text){
		int numOfDigits = 0;
		for(char ch : text.toCharArray()){
			if(!Character.isDigit(ch) && !Character.isWhitespace(ch)){
				throw new IllegalArgumentException("Illegal Input: \nOnly digits and spaces allowed");
			} else if (Character.isDigit(ch)) {
				numOfDigits++;
			}
		}
		if(numOfDigits > SIZE*SIZE){
			throw new IllegalArgumentException("Grid Can't Contain \nMore Than 81 Numbers");
		}
	}


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		if(ints.length != SIZE || ints[0].length != SIZE){
			throw new IllegalArgumentException("Grid must be 9x9");
		}

		grid = new int[SIZE][SIZE];
		emptySpots = new ArrayList<>();

		for(int row = 0; row < SIZE; row++){
			for(int col = 0; col < SIZE; col++){
				grid[row][col] = ints[row][col];
				if(grid[row][col] == 0){
					emptySpots.add(new Spot(row, col));
				}
			}
		}

		numSolutions = 0;
		elapsed = 0;
		solutionText = "";
	}

	public Sudoku(String text) {
		this(textToGrid(text));
	}

	private class Spot implements Comparable<Spot>{
		private final int row;
		private final int col;

		public Spot(int row, int col){
			this.row = row;
			this.col = col;
		}

		 public HashSet<Integer> getValidValues(){
			// First fill all possible values
			HashSet<Integer> validValues = new HashSet<>();
			for(int i = 1; i <= SIZE; i++){
				validValues.add(i);
			}

			// Remove values in the same row
			for(int c = 0; c < SIZE; c++){
				validValues.remove(grid[row][c]);
			}

			// Remove values in the same column
			for(int r = 0; r < SIZE; r++){
				validValues.remove(grid[r][col]);
			}

			// Remove values in the same 3x3 box
			int boxRowStart = (row / PART) * PART;
			int boxColStart = (col / PART) * PART;
			for(int r = boxRowStart; r < boxRowStart + PART; r++){
				for(int c = boxColStart; c < boxColStart + PART; c++){
					validValues.remove(grid[r][c]);
				}
			}

			return validValues;
		}

		@Override
		public int compareTo(Spot other) {
			return this.getValidValues().size() - other.getValidValues().size();
		}

		private void set(int value) {
			grid[row][col] = value;
		}
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		numSolutions = 0;
		solutionText = "";
		long startTime = System.currentTimeMillis();

		// We check if given sudoku is valid before solving
		if (!isInitialStateValid()) {
			elapsed = System.currentTimeMillis() - startTime;
			return 0;
		}

		Collections.sort(emptySpots);
		solveRecursively(0);

		elapsed = System.currentTimeMillis() - startTime;
		return numSolutions;
	}

	private boolean isInitialStateValid() {
		// We track seen values
		boolean[][] rowCheck = new boolean[SIZE][SIZE + 1];
		boolean[][] colCheck = new boolean[SIZE][SIZE + 1];
		boolean[][] boxCheck = new boolean[SIZE][SIZE + 1];

		for (int r = 0; r < SIZE; r++) {
			for (int c = 0; c < SIZE; c++) {
				int val = grid[r][c];
				if (val != 0) {
					int boxIndex = (r / PART) * PART + (c / PART);

					if (rowCheck[r][val] || colCheck[c][val] || boxCheck[boxIndex][val]) {
						return false;
					}

					rowCheck[r][val] = true;
					colCheck[c][val] = true;
					boxCheck[boxIndex][val] = true;
				}
			}
		}
		return true;
	}

	private void solveRecursively(int idx){
		if(numSolutions >= MAX_SOLUTIONS) {
			return;
		}

		if(idx == emptySpots.size()){
			numSolutions++;
			if(numSolutions == 1){
				solutionText = this.toString();
			}
			return;
		}

		Spot spot = emptySpots.get(idx);
		for (int value : spot.getValidValues()) {
			spot.set(value);
			solveRecursively(idx + 1);
			spot.set(0); // backtrack
		}
	}
	
	public String getSolutionText() {
		return solutionText;
	}
	
	public long getElapsed() {
		return elapsed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < SIZE; r++) {
			for (int c = 0; c < SIZE; c++) {
				if (c > 0) sb.append(" ");
				sb.append(grid[r][c]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
