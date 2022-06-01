//Maxim Kim
public class MyCell implements Cell {

    int row;
    int col;
    boolean visible;
    boolean mine;
    boolean flagged;
    int neighborMines;
    int revealedNeighbors;
    int numNeighbors;
    MyMineModel model;

    public MyCell(int row, int col, MyMineModel model) {
        this.row = row;
        this.col = col;
        visible = false;
        flagged = false;
        this.model = model; //gets minemodel for list of cells
    }

    /**
     * Returns the row number of this cell.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column number of this cell.
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns true if this cell is visible, meaning that its number of neighboring mines should be displayed.
     */
    public boolean isVisible() {
        return visible;
    }


    public void setVisible() {
        visible = true;
    }

    /**
     * Returns true if this cell contains a mine (regardless of whether it is visible or has been flagged).
     */
    public boolean isMine() {
        return mine;
    }

    public void setMine() {
        mine = true;
    }

    public void remMine() {
        mine = false;
    }

    /**
     * Returns true if this cell has been flagged (regardless of whether it is visible or contains a mine).
     */
    public boolean isFlagged() {
        return flagged;
    }

    public void setFlag() {
        if (flagged) {
            model.numFlags--;
        } else {
            model.numFlags++;
        }
        flagged = !flagged;
    }


    /**
     * Returns the number of mines that are in cells that are adjacent to this one.  Most cells have
     * eight neighbors (N, S, E, W, NE, NW, SE, SW).  Cells along the edges of the field or cells in the
     * corners have fewer neighbors.  Mines are counted regardless of whether or not they are visible
     * or flagged.  If there is a mine in the current cell, it is not counted.
     */
    public int getNeighborMines() {
        neighborMines = 0; //counts from 0 each time it checks
        MyCell[][] cells = model.getCells();
        //goes through all 8 neighboring cells around this cell and checks if mine
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if (r >= 0 && c >= 0 && r < model.getNumRows() && c < model.getNumCols()) { //if in bounds
                    if (cells[r][c].isMine()) {
                        neighborMines++;
                    }
                }
            }
        }
        return neighborMines;
    }

    public void getNeighborsExtra() {
        numNeighbors = 0;
        revealedNeighbors = 0;
        MyCell[][] cells = model.getCells();

        //goes through the neighbors and counts the total neighbors and the revealed neighbors
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if (r >= 0 && c >= 0 && r < model.getNumRows() && c < model.getNumCols()) { //if in bounds
                    numNeighbors++;
                    if (cells[r][c].isVisible()) {
                        revealedNeighbors++;
                    }
                }
            }
        }

    }

    public void singleAutoFlag() {
        MyCell[][] cells = model.getCells();
        //if single cell and all neighbors except 1 have been revealed, flag non visible because must be mine
        if (neighborMines == 1 && revealedNeighbors == numNeighbors - 1) {
            for (int r = row - 1; r < row + 2; r++) {
                for (int c = col - 1; c < col + 2; c++) {
                    if (r >= 0 && c >= 0 && r < model.getNumRows() && c < model.getNumCols()) { //if in bounds
                        if (!cells[r][c].isVisible()) {
                            if (!cells[r][c].isFlagged()) {
                                model.numFlags++;
                            }
                            cells[r][c].flagged = true;

                        }
                    }
                }
            }
        }

    }


}
