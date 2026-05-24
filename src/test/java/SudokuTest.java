import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SudokuTest {

    @Test
    public void testEasyGrid() {
        Sudoku sudoku = new Sudoku(Sudoku.easyGrid);
        assertEquals(1, sudoku.solve());
        assertTrue(sudoku.getSolutionText().startsWith("1 6 4"));
        assertTrue(sudoku.getElapsed() >= 0);
    }

    @Test
    public void testMediumGrid() {
        Sudoku sudoku = new Sudoku(Sudoku.mediumGrid);
        assertEquals(1, sudoku.solve());
    }

    @Test
    public void testHardGrid() {
        Sudoku sudoku = new Sudoku(Sudoku.hardGrid);
        assertEquals(1, sudoku.solve());
    }

    @Test
    public void testMaxSolutionsLimit() {
        String emptyBoard = "0 ".repeat(81);
        Sudoku sudoku = new Sudoku(emptyBoard);
        assertEquals(Sudoku.MAX_SOLUTIONS, sudoku.solve());
    }

    @Test
    public void testInvalidGridDimensions() {
        int[][] badRows = new int[8][9];
        assertThrows(IllegalArgumentException.class, () -> new Sudoku(badRows));

        int[][] badCols = new int[9][8];
        assertThrows(IllegalArgumentException.class, () -> new Sudoku(badCols));
    }

    @Test
    public void testInvalidInputCharacters() {
        String badString = "1 6 4 a 0 0 0 0 2" + "0 ".repeat(72);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Sudoku(badString));
        assertTrue(e.getMessage().contains("Illegal Input"));
    }

    @Test
    public void testTooManyNumbersInput() {
        String tooLong = "1 ".repeat(82);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Sudoku(tooLong));
        assertTrue(e.getMessage().contains("More Than 81"));
    }

    @Test
    public void testTooFewNumbersInput() {
        String tooShort = "1 ".repeat(80);
        RuntimeException e = assertThrows(RuntimeException.class, () -> new Sudoku(tooShort));
        assertTrue(e.getMessage().contains("Needed 81 numbers"));
    }

    @Test
    public void testInitialStateInvalidRow() {
        int[][] grid = new int[9][9];
        grid[0][0] = 5;
        grid[0][8] = 5;
        Sudoku sudoku = new Sudoku(grid);
        assertEquals(0, sudoku.solve());
    }

    @Test
    public void testInitialStateInvalidColumn() {
        int[][] grid = new int[9][9];
        grid[0][0] = 5;
        grid[8][0] = 5;
        Sudoku sudoku = new Sudoku(grid);
        assertEquals(0, sudoku.solve());
    }

    @Test
    public void testInitialStateInvalidBox() {
        int[][] grid = new int[9][9];
        grid[0][0] = 5;
        grid[1][1] = 5;
        Sudoku sudoku = new Sudoku(grid);
        assertEquals(0, sudoku.solve());
    }

    @Test
    public void testStringsToGridUtility() {
        int[][] grid = Sudoku.stringsToGrid("123", "456");
        assertEquals(2, grid.length);
        assertEquals(3, grid[0].length);
        assertEquals(1, grid[0][0]);
        assertEquals(6, grid[1][2]);
    }

    @Test
    public void testMainMethodCompletesWithoutExceptions() {
        // we can just execute main too (for line coverage :) )
        assertDoesNotThrow(() -> Sudoku.main(new String[]{}));
    }
}