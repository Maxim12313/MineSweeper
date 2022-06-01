//Maxim Kim

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class MyMineModel implements MineModel {

    MyCell[][] cells;
    int numRows;
    int numCols;
    int numMines;
    int numFlags;
    long startTime;
    boolean dead;
    boolean started = false;
    int placedMines;
    private Random rand = new Random();

    public void newGame(int numRows, int numCols, int numMines) {
        dead = false;
        started = true;
        startTime = currentTimeMillis();
        this.numRows = numRows;
        this.numCols = numCols;
        this.numMines = numMines;
        placedMines = 0;
        cells = new MyCell[numRows][numCols];
        numFlags = 0;
        createCells();
        setMines();

    }

    public void createCells() {
        for (int row = 0; row < numRows; row++) { //goes through the number of rows and cells and creates a cell
            for (int col = 0; col < numCols; col++) {
                cells[row][col] = new MyCell(row, col, this);
            }
        }
    }

    public void setMines() {
        //loop that goes random cells and set to mine only if not a mine to avoid duplicate
        while (placedMines < numMines) {
            int rowRand = rand.nextInt(numRows);
            int colRand = rand.nextInt(numCols);
            if (!cells[rowRand][colRand].isMine()) {
                cells[rowRand][colRand].setMine();
                placedMines++;
            }
        }
    }


    public boolean firstCell() {
        //if any cell is visible, return false, else return true
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (cells[row][col].isVisible()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Returns the number of rows in the field.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Returns the number of columns in the field.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Returns the total number of mines in the field.
     */
    public int getNumMines() {
        return numMines;
    }

    /**
     * Returns the number of flags that have been placed in the field.
     */
    public int getNumFlags() {
        return numFlags;
    }

    /**
     * Returns the number of seconds that have elapsed since this game started.
     */
    public int getElapsedSeconds() {
        return (int) ((currentTimeMillis() - startTime) / 1000); //time since beggining game
    }

    /**
     * Returns the Cell in the field at the given coordinates.
     *
     * @param row the row number of the cell [precondition: <code> 0 <= row < getNumRows()</code>]
     * @param col the column number of the cell [precondition: <code> 0 <= col < getNumCols()</code>]
     * @return a valid, non-null Cell object
     */
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    /**
     * Called when the player has stepped onto the cell at the given coordinates.
     * This cell is now made visible.  If it is a mine, the player is dead and the game is over.
     * If it is a safe (non-mine) cell with no neighboring mines (a zero cell), then every cell
     * that can be reached from this one by only stepping on zero cells should be made visible as well.
     *
     * @param row the row number of the cell [precondition: <code> 0 <= row < getNumRows()</code>]
     * @param col the column number of the cell [precondition: <code> 0 <= col < getNumCols()</code>]
     */
    public void stepOnCell(int row, int col) {
        if (firstCell()) {
            safeCell(row, col); //if first cell, run safecell
        }
        autoHelper();

        cells[row][col].setVisible();

        if (cells[row][col].isFlagged()) {
            cells[row][col].setFlag();
        }
        if (cells[row][col].isMine()) {
            dead = true;
        }

        //goes through all neighboring cells and runs stepOnCell knowing they arent mines
        if (cells[row][col].getNeighborMines() == 0) {
            for (int r = row - 1; r < row + 2; r++) {
                for (int c = col - 1; c < col + 2; c++) {
                    //if in bounds and if not visible(not already checked)
                    if (r >= 0 && c >= 0 && r < numRows && c < numCols && !cells[r][c].isVisible()) {
                        stepOnCell(r, c);
                    }
                }
            }
        }
    }


    public void autoHelper() {

        for (int row = 0; row < numRows; row++) { //goes through every visible cell and runs the singleAutoFlag helper
            for (int col = 0; col < numCols; col++) {
                if (cells[row][col].isVisible()) {
                    cells[row][col].getNeighborsExtra();
                    cells[row][col].singleAutoFlag();
                }
            }
        }
    }

    public void safeCell(int row, int col) {
        //looks at itself and neighbor cells
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if (r >= 0 && c >= 0 && r < numRows && c < numCols) { //if in bounds
                    //if mine, remove then set a new random mine
                    if (cells[r][c].isMine()) {
                        cells[r][c].remMine();
                        placedMines--;
                        setMines();
                    }
                }
            }
        }
        if (cells[row][col].getNeighborMines() != 0) { //if any neighbor cells become mines, run safeCell again
            safeCell(row, col);
        }
    }


    /**
     * Called when the player wants to change the flagged status of a cell.
     * If the indicated cell has no flag, then place a flag there.
     * If the indicated cell already has a flag, then remove it.
     * Note that it is safe to place a flag on any cell, even if it has a mine.
     *
     * @param row the row number of the cell [precondition: <code> 0 <= row < getNumRows()</code>]
     * @param col the column number of the cell [precondition: <code> 0 <= col < getNumCols()</code>]
     */
    public void placeOrRemoveFlagOnCell(int row, int col) {
        cells[row][col].setFlag();
    }

    /**
     * Returns true if a game was started, whether or not it has ended.
     * Returns false only if no game has ever been started.
     */
    public boolean isGameStarted() {
        return started;
    }

    /**
     * Returns true if the current game has ended.
     * Returns false if the current game is running.
     * If the game hasn't started, then the value returned by this method is not defined.
     */
    public boolean isGameOver() {
        return dead;
    }

    /**
     * Returns true if the player is dead, because they stepped on a mine.
     * Returns false if the player has not yet stepped on a mine.
     */
    public boolean isPlayerDead() {
        return dead;
    }


    /**
     * Returns true if the player has won the current game, by exposing every non-mine cell.
     * Returns false if player hasn't won (either the game hasn't started, it is still going, or the player is dead).
     */
    public boolean isGameWon() {

        //if any cell is not visible, return false, else return true
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!cells[row][col].isVisible() && !cells[row][col].isMine()) {
                    return false;
                }
            }
        }
        return true;
    }

    public MyCell[][] getCells() {
        return cells;
    }


}
