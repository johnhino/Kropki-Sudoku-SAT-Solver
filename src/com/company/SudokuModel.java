package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SudokuModel {

  private int[][] sudoku;
  private String filePath;
  private ArrayList<Integer> constraints;
  private int clauses;

  /**
   * Represents a new sudoku model
   * @param filePath The string representation for the file being written to.
   * @param constraints The list of constraints for this particular sudoku puzzle.
   */
  public SudokuModel(String filePath, ArrayList<Integer> constraints) {
    this.sudoku = new int[9][9];
    this.filePath = filePath;
    this.constraints = constraints;
    this.clauses = 0;
  }

  /**
   * Uses the SAT solver to find the satisfiability of the sudoku puzzle.
   */
  public void solveSudoku() {
    WriteFile out = new WriteFile(filePath);
    try {
      StringBuilder cnf = makeCNFFile();
      String sudokuPuzzle = addSudokuConstraints(cnf);
      StringBuilder fullFile = new StringBuilder();
      fullFile.append("p cnf 999 ");
      fullFile.append(clauses);
      fullFile.append(System.lineSeparator());
      fullFile.append(sudokuPuzzle);
      out.writeToFile(fullFile.toString());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write to file");
    }
    ISolver solver = SolverFactory.newDefault();
    solver.setTimeout(300); // 5 minute timeout
    Reader reader = new DimacsReader(solver);
    try {
      IProblem problem = reader.parseInstance(filePath);
      if (problem.isSatisfiable()) {
        System.out.println("Satisfiable!");
        int[] model = problem.model();
        parseSolution(model);
        visualizeSolution();
      } else {
        System.out.println("Unsatisfiable!");
      }
    } catch (FileNotFoundException e) {
      System.out.println("File Not Found");
    } catch (ParseFormatException e) {
      System.out.println("Parse Format Error");
    } catch (IOException e) {
      System.out.println("IOException");
    } catch (ContradictionException e) {
      System.out.println("Unsatisfiable (trivial)!");
    } catch (TimeoutException e) {
      System.out.println("Timeout, sorry!");
    }
  }

  /**
   * Represents the puzzle as a CNF file.
   * @return A StringBuilder representing the CNF reduced puzzle
   * (minus the puzzle specific contraints).
   */
  public StringBuilder makeCNFFile() {
    StringBuilder ans = new StringBuilder();
    // Individual Cell Clauses
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int k = 1; k <= 9; k++) {
          int val = i * 100 + j * 10 + k;
          ans.append(val);
          ans.append(" ");
        }
        ans.append(0);
        ans.append(System.lineSeparator());
        clauses++;
      }
    }
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int currentCell = 1; currentCell <= 9; currentCell++) {
          for (int k = currentCell + 1; k <= 9; k++) {
            int firstVal = -1 * (i * 100 + j * 10 + currentCell);
            int secondVal = -1 * (i * 100 + j * 10 + k);
            ans.append(firstVal);
            ans.append(" ");
            ans.append(secondVal);
            ans.append(" 0");
            ans.append(System.lineSeparator());
            clauses++;
          }
        }
      }
    }
    // Row Clauses
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int k = 1; k <= 9; k++) {
          int val = i * 100 + k * 10 + j;
          ans.append(val);
          ans.append(" ");
        }
        ans.append(0);
        ans.append(System.lineSeparator());
        clauses++;
      }
    }
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int currentRow = 1; currentRow <= 9; currentRow++) {
          for (int k = currentRow + 1; k <= 9; k++) {
            int firstVal = -1 * (j * 100 + currentRow * 10 + i);
            int secondVal = -1 * (j * 100 + k * 10 + i);
            ans.append(firstVal);
            ans.append(" ");
            ans.append(secondVal);
            ans.append(" 0");
            ans.append(System.lineSeparator());
            clauses++;
          }
        }
      }
    }
    // Column Clauses
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int k = 1; k <= 9; k++) {
          int val = k * 100 + i * 10 + j;
          ans.append(val);
          ans.append(" ");
        }
        ans.append(0);
        ans.append(System.lineSeparator());
        clauses++;
      }
    }
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int currentColumn = 1; currentColumn <= 9; currentColumn++) {
          for (int k = currentColumn + 1; k <= 9; k++) {
            int firstVal = -1 * (currentColumn * 100 + j * 10 + i);
            int secondVal = -1 * (k * 100 + j * 10 + i);
            ans.append(firstVal);
            ans.append(" ");
            ans.append(secondVal);
            ans.append(" 0");
            ans.append(System.lineSeparator());
            clauses++;
          }
        }
      }
    }
    // Block Clauses
    int jBlock = 1;
    int kBlock = 1;
    for (int b = 1; b <= 9; b++) {
      for (int i = 1; i <= 9; i++) {
        for (int j = jBlock % 9; j < jBlock % 9 + 3; j++) {
          for (int k = kBlock; k < kBlock + 3; k++) {
            int val = k * 100 + j * 10 + i;
            ans.append(val);
            ans.append(" ");
          }
        }
        ans.append(0);
        ans.append(System.lineSeparator());
        clauses++;
      }
      if (b % 3 == 0) {
        kBlock += 3;
      }
      jBlock += 3;
    }
    return ans;
  }

  /**
   * Adds the constraints to the sudoku puzzle.
   * @param file The pre-processed CNF file.
   * @return The finalized String representation of the file.
   */
  public String addSudokuConstraints(StringBuilder file) {
    for (int constraint : constraints) {
      file.append(constraint);
      file.append(" 0");
      file.append(System.lineSeparator());
      clauses++;
    }
    return file.toString();
  }

  /**
   * Takes the SAT solver's decoded literals and parses them into the sudoku puzzle.
   * @param model The array of literals used to construct the puzzle.
   */
  public void parseSolution(int[] model) {
    ArrayList<Integer> solvedNums = new ArrayList<>();
    for (int literal : model) {
      if (literal > 0) {
        solvedNums.add(literal);
      }
    }
    for (int num : solvedNums) {
      int value = num % 10;
      num /= 10;
      int column = (num % 10) - 1;
      num /= 10;
      int row = (num % 10) - 1;
      sudoku[row][column] = value;
    }
  }

  /**
   * Outputs the parsed solution as a string.
   */
  public void visualizeSolution() {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        if(j == 3 || j == 6) {
          output.append(" ");
        }
        int value = sudoku[i][j];
        output.append(value);
        if (j != 8) {
          output.append(" ");
        }
      }
      output.append(System.lineSeparator());
      if (i == 2 || i == 5) {
        output.append(System.lineSeparator());
      }
    }
    System.out.print(output.toString());
  }

  public int[][] checkSolution() {
    return sudoku;
  }

}
