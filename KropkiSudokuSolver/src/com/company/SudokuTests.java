package com.company;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

public class SudokuTests {

  String filePath = "/Users/jackhino/Desktop/KropkiSudokuSolver/src/com/company/CNFExample";

  /**
   * Tests that valid Kropki sudoku puzzles are properly solved.
   */
  @Test
  public void testValidPuzzle() {
    SudokuModel firstValid = new SudokuModel(filePath,
        new ArrayList<>(Arrays.asList(16,17,21,22,25,26,28,29,31,41,32,42,33,34,36,46,37,47,37,38,
            44,54,45,55,53,63,56,66,57,67,58,59,63,64,64,65,68,69,71,81,73,83,73,74,74,84,76,86,78,
            88,86,96,87,97,88,98,91,92)), new ArrayList<>(Arrays.asList(16,26,17,27,23,33,36,37,42,
        43,43,53,48,58,58,68,64,74,67,77,68,78,81,82,83,93,83,84,85,95)));
    SudokuModel secondValid = new SudokuModel(filePath,
        new ArrayList<>(Arrays.asList(11,12,13,14,19,29,22,32,28,38,34,35,38,48,39,49,45,55,51,61,51
            ,52,53,63,53,54,58,68,61,71,61,62,62,72,66,76,69,79,71,72,72,73,76,86,79,89,81,91,93,94)
        ), new ArrayList<>(Arrays.asList(12,22,12,13,16,26,18,28,23,33,25,26,36,46,38,39,45,46,54,55
        ,57,67,63,73,67,77,72,82,84,94,84,85,87,97,94,95,96,97)));

    int[][] firstTest = {{4,6,9,1,8,3,2,5,7},{7,8,2,9,5,6,1,3,4},{5,3,1,2,7,4,8,9,6},
        {6,2,4,7,3,5,9,1,8},{9,5,8,6,4,1,7,2,3},{3,1,7,8,9,2,6,4,5},{1,7,5,4,6,9,3,8,2},
        {2,4,6,3,1,8,5,7,9},{8,9,3,5,2,7,4,6,1}};
    int[][] secondTest = {{5,4,2,3,7,1,9,6,8},{1,8,6,9,4,2,5,3,7},{9,7,3,5,6,8,1,4,2},
        {6,9,1,8,2,4,7,5,3},{4,5,7,6,3,9,2,8,1},{3,2,8,1,5,7,4,9,6},{2,3,4,7,9,6,8,1,5},
        {8,6,9,2,1,5,3,7,4},{7,1,5,4,8,3,6,2,9}};

    int[][] firstSolution = firstValid.checkSolution();
    int[][] secondSolution = secondValid.checkSolution();

    assertArrayEquals(firstTest, firstSolution);
    assertArrayEquals(secondTest, secondSolution);
  }

  /**
   * Tests that an invalid Kropki sudoku puzzle is unsatisfiable.
   */
  @Test
  public void testUnsatisfiablePuzzle() {
    SudokuModel unsatisfiable = new SudokuModel(filePath, new ArrayList<>(),
        new ArrayList<>(Arrays.asList(12, 22, 21, 22, 22, 32, 22, 23)));
    int[][] unsatisfiableTest = {{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0}};
    int[][] unsatisfiableSolution = unsatisfiable.checkSolution();
    assertArrayEquals(unsatisfiableTest, unsatisfiableSolution);
  }

  /**
   * Tests that a Kropki sudoku with no white or black dots is satisfiable, and will always
   * have only one solution.
   */
  @Test
  public void testEmptyPuzzle() {
    SudokuModel empty = new SudokuModel(filePath, new ArrayList<>(), new ArrayList<>());
    int[][] emptyTest = {{7,1,4,9,5,3,8,2,6},{2,8,6,4,7,1,3,5,9},{5,3,9,6,2,8,1,7,4},
        {9,5,3,8,6,2,7,4,1},{6,2,8,1,4,7,5,9,3},{4,7,1,3,9,5,2,6,8},{1,4,7,5,3,9,6,8,2},
        {3,9,5,2,8,6,4,1,7},{8,6,2,7,1,4,9,3,5}};
    int[][] testSolution = empty.checkSolution();
    assertArrayEquals(emptyTest, testSolution);
  }

  @Test
  public void testInvalidConstraint() {

  }

}
