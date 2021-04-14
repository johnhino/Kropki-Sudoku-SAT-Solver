package com.company;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

public class SudokuTests {

  SudokuModel easyModel = new SudokuModel("/Users/jackhino/Desktop/KropkiSudokuSolver/src/com/company/CNFExample",
      new ArrayList<Integer>(Arrays.asList(142, 156, 177, 191, 216, 228, 257, 289, 311, 329, 364,
          375, 418, 422, 441, 484, 534, 546, 562, 579, 625, 663, 682, 698, 739, 743, 787, 794, 824,
          855, 883, 896, 917, 933, 951, 968)));

  SudokuModel hardModel = new SudokuModel("/Users/jackhino/Desktop/KropkiSudokuSolver/src/com/company/CNFExample",
      new ArrayList<Integer>(Arrays.asList(122, 246, 293, 327, 334, 358, 463, 492, 528, 554, 581,
          616, 645, 751, 777, 788, 815, 869, 984)));

  SudokuModel impossibleModel = new SudokuModel("/Users/jackhino/Desktop/KropkiSudokuSolver/src/com/company/CNFExample",
      new ArrayList<Integer>(Arrays.asList(118, 233, 246, 327, 359, 372, 425, 467, 554, 565, 577,
          641, 683, 731, 786, 798, 838, 845, 881, 929, 974)));

  @Test
  public void testSudoku() {
    easyModel.solveSudoku();
    int[][] easySudoku = easyModel.checkSolution();
    hardModel.solveSudoku();
    int[][] hardSudoku = hardModel.checkSolution();
    impossibleModel.solveSudoku();
    int[][] impossibleSudoku = impossibleModel.checkSolution();

    int[][] easyCheck = {{4,3,5,2,6,9,7,8,1},{6,8,2,5,7,1,4,9,3},{1,9,7,8,3,4,5,6,2},
        {8,2,6,1,9,5,3,4,7},{3,7,4,6,8,2,9,1,5},{9,5,1,7,4,3,6,2,8},{5,1,9,3,2,6,8,7,4},
        {2,4,8,9,5,7,1,3,6},{7,6,3,4,1,8,2,5,9}};

    int[][] hardCheck = {{1,2,6,4,3,7,9,5,8},{8,9,5,6,2,1,4,7,3},{3,7,4,9,8,5,1,2,6},
        {4,5,7,1,9,3,8,6,2},{9,8,3,2,4,6,5,1,7},{6,1,2,5,7,8,3,9,4},{2,6,9,3,1,4,7,8,5},
        {5,4,8,7,6,9,2,3,1},{7,3,1,8,5,2,6,4,9}};

    int[][] impossibleCheck = {{8,1,2,7,5,3,6,4,9},{9,4,3,6,8,2,1,7,5},{6,7,5,4,9,1,2,8,3},
        {1,5,4,2,3,7,8,9,6},{3,6,9,8,4,5,7,2,1},{2,8,7,1,6,9,5,3,4},{5,2,1,9,7,4,3,6,8},
        {4,3,8,5,2,6,9,1,7},{7,9,6,3,1,8,4,5,2}};

    assertArrayEquals(easySudoku, easyCheck);
    assertArrayEquals(hardSudoku, hardCheck);
    assertArrayEquals(impossibleSudoku, impossibleCheck);
  }

}
