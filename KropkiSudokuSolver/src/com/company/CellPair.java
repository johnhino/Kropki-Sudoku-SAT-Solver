package com.company;

import java.util.Objects;

/**
 * Represents a pair of sudoku cells, formatted like xy, with x being the row and y being the
 * column. The coordinates are 1 indexed.
 */
public class CellPair {
  private int firstCell;
  private int secondCell;

  /**
   * Constructs a pair of sudoku cells.
   * @param firstCell The coordinates of the first sudoku cell.
   * @param secondCell The coordinates of the second sudoku cell.
   */
  public CellPair(int firstCell, int secondCell) {
    this.firstCell = firstCell;
    this.secondCell = secondCell;
  }

  /**
   * Getter for the first cell.
   * @return The coordinates of the first cell.
   */
  public int getFirstCell() {
    return this.firstCell;
  }

  /**
   * Getter for the second second cell.
   * @return The coordinates of the second cell. n
   */
  public int getSecondCell() {
    return this.secondCell;
  }

  @Override
  public String toString() {
    StringBuilder ans = new StringBuilder();
    ans.append("(" + firstCell + ", " + secondCell + ")");
    return ans.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if(o instanceof CellPair) {
      CellPair compare = (CellPair) o;
      return this.firstCell == compare.firstCell && this.secondCell == compare.secondCell;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.firstCell, this.secondCell);
  }
}
