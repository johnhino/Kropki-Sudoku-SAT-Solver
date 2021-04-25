package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * Uses the SAT4J library to solve Kropki sudoku. This model could be extended to include other
 * sudoku variants by changing the addKropkiConstraints method with another one made for that
 * variant. First a CNF representation of a sudoku puzzle is generated, which is then passed to
 * the SAT solver to solve. After the list of positive literals is obtained, it is parsed to get
 * the correct values at the correct coordinates on the sudoku board. This board is then visualized
 * and output as a string.
 */
public class SudokuModel {

  /**
   * Represents a type of cell constraint, either individual, row, or column.
   */
  public enum Type{Individual, Row, Column};

  private int[][] sudoku;
  private String filePath;
  private ArrayList<CellPair> pairs;
  private HashSet<CellPair> whiteDotPairs;
  private HashSet<CellPair> blackDotPairs;
  private int clauses;

  /**
   * Represents a sudoku model.
   * @param filePath The filepath to write the CNF file to.
   * @param whiteDots The list of cells that have white dot constraints.
   * @param blackDots The list of cell that have black dot constraints.
   */
  public SudokuModel(String filePath, ArrayList<Integer> whiteDots,  ArrayList<Integer> blackDots) {
    this.sudoku = new int[9][9];
    this.filePath = filePath;
    this.clauses = 0;
    this.pairs = new ArrayList<>();
    this.whiteDotPairs = new HashSet<>();
    this.blackDotPairs = new HashSet<>();
    // Parses the white dot pairs
    for (int i = 0; i < whiteDots.size() - 1; i++) {
      int firstCell = whiteDots.get(i);
      int secondCell = whiteDots.get(i + 1);
      CellPair current = new CellPair(firstCell, secondCell);
      whiteDotPairs.add(current);
      i++;
    }
    // Parses the black dot pairs
    for (int i = 0; i < blackDots.size() - 1; i++) {
      int firstCell = blackDots.get(i);
      int secondCell = blackDots.get(i + 1);
      CellPair current = new CellPair(firstCell, secondCell);
      blackDotPairs.add(current);
      i++;
    }
  }

  /**
   * Uses the SAT solver to find the satisfiability of the sudoku puzzle.
   */
  public void solveSudoku() {
    WriteFile out = new WriteFile(filePath);
    try {
      generateCellPairs();
      StringBuilder cnf = makeCNFFile();
      String sudokuPuzzle = addKropkiConstraints(cnf);
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
   * (minus the puzzle specific constraints).
   */
  private StringBuilder makeCNFFile() {
    StringBuilder ans = new StringBuilder();
    // Individual Cell Clauses
    formatClauses(ans, Type.Individual);
    // Row Clauses
    formatClauses(ans, Type.Row);
    // Column Clauses
    formatClauses(ans, Type.Column);
    // Block Clauses
    formatBlockClauses(ans);
    return ans;
  }

  /**
   * Formats both definedness and uniqueness clauses for individual, row, or column.
   * @param ans The StringBuilder representation of the CNF file so far.
   * @param type The type of constraint to add.
   */
  private void formatClauses(StringBuilder ans, Type type) {
    formatDefinedNessClauses(ans, type);
    formatUniquenessClauses(ans, type);
  }

  /**
   * Formats definedness clauses for constraints of a certain type.
   * @param ans The StringBuilder representation of the CNF file so far.
   * @param type The type of constraints to add.
   * @throws IllegalArgumentException If type is invalid.
   */
  private void formatDefinedNessClauses(StringBuilder ans, Type type)
      throws IllegalArgumentException {
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int k = 1; k <= 9; k++) {
          int val;
          switch(type) {
            case Individual:
              val = i * 100 + j * 10 + k;
              break;
            case Row:
              val = i * 100 + k * 10 + j;
              break;
            case Column:
              val = k * 100 + i * 10 + j;
              break;
            default:
              throw new IllegalArgumentException("Invalid Type");
          }
          ans.append(val);
          ans.append(" ");
        }
        ans.append(0);
        ans.append(System.lineSeparator());
        clauses++;
      }
    }
  }

  /**
   * Formats uniqueness clauses for constraints of a certain type.
   * @param ans The StringBuilder representation of the CNF file so far.
   * @param type The type of constraints to add.
   * @throws IllegalArgumentException If type is invalid.
   */
  private void formatUniquenessClauses(StringBuilder ans, Type type)
  throws IllegalArgumentException {
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        for (int current = 1; current <= 9; current++) {
          for (int k = current + 1; k <= 9; k++) {
            int firstVal;
            int secondVal;
            switch(type) {
              case Individual:
                firstVal = -1 * (i * 100 + j * 10 + current);
                secondVal = -1 * (i * 100 + j * 10 + k);
                break;
              case Row:
                firstVal = -1 * (j * 100 + current * 10 + i);
                secondVal = -1 * (j * 100 + k * 10 + i);
                break;
              case Column:
                firstVal = -1 * (current * 100 + j * 10 + i);
                secondVal = -1 * (k * 100 + j * 10 + i);
                break;
              default:
                throw new IllegalArgumentException("Invalid Type");
            }
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
  }

  /**
   * Formats definedness clauses for block constraints.
   * @param ans The StringBuilder representation of the CNF file so far.
   * @throws IllegalArgumentException If type is invalid.
   */
  private void formatBlockClauses(StringBuilder ans) {
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
  }

  /**
   * Adds all constraints for all cell pairs.
   * @param file The StringBuilder representation of the CNF file so far.
   * @return The String representation of the finalized CNF file with added Kropki constraints.
   */
  private String addKropkiConstraints(StringBuilder file) {
    for (CellPair pair : pairs) {
     if (whiteDotPairs.contains(pair)) {
        String constraint = addWhiteDotConstraint(pair);
        file.append(constraint);
      }
     else if (blackDotPairs.contains(pair)) {
        String constraint = addBlackDotConstraint(pair);
        file.append(constraint);
      }
    else {
        String constraint = addNoDotConstraint(pair);
        file.append(constraint);
      }
    }
    return file.toString();
  }

  /**
   * Adds the constraints to a cell pair with a white dot.
   * @param pair The cell pair to be constrained upon.
   * @return The string representing the full set of white dot constraints.
   */
  private String addWhiteDotConstraint(CellPair pair) {
    StringBuilder ans = new StringBuilder();
    int firstCell = pair.getFirstCell();
    int secondCell = pair.getSecondCell();
    fourLiteralClause(ans, firstCell, secondCell, 1, 2);
    sixLiteralClause(ans, firstCell, secondCell, 2, 1, 3);
    sixLiteralClause(ans, firstCell, secondCell, 3, 2, 4);
    sixLiteralClause(ans, firstCell, secondCell, 4, 3, 5);
    sixLiteralClause(ans, firstCell, secondCell, 5, 4, 6);
    sixLiteralClause(ans, firstCell, secondCell, 6, 5, 7);
    sixLiteralClause(ans, firstCell, secondCell, 7, 6, 8);
    sixLiteralClause(ans, firstCell, secondCell, 8, 7, 9);
    fourLiteralClause(ans, firstCell, secondCell, 9, 8);
    return ans.toString();
  }

  /**
   * Adds the constraints for a cell pair with a black dot.
   * @param pair The cell pair to be constrained upon.
   * @return The string representing the full set of black dot constraints.
   */
  private String addBlackDotConstraint(CellPair pair) {
    StringBuilder ans = new StringBuilder();
    int firstCell = pair.getFirstCell();
    int secondCell = pair.getSecondCell();
    fourLiteralClause(ans, firstCell, secondCell, 1, 2);
    sixLiteralClause(ans, firstCell, secondCell, 2, 1, 4);
    fourLiteralClause(ans, firstCell, secondCell, 3, 6);
    sixLiteralClause(ans, firstCell, secondCell, 4, 8, 2);
    fourLiteralClause(ans, firstCell, secondCell, 6, 3);
    fourLiteralClause(ans, firstCell, secondCell, 8, 4);
    return ans.toString();
  }

  /**
   * Adds the constraints for a pair of cells with no dot.
   * @param pair The cell pair to be constrained upon.
   * @return The string representing the full set of no dot constraints.
   */
  private String addNoDotConstraint(CellPair pair) {
    StringBuilder ans = new StringBuilder();
    int firstCell = pair.getFirstCell();
    int secondCell = pair.getSecondCell();
    for (int i = 1; i <= 4; i++) {
      noDotClause(ans, firstCell, secondCell, i, i * 2);
      noDotClause(ans, firstCell, secondCell, i * 2, i);
    }
    for (int i = 1; i <= 8; i++) {
      noDotClause(ans, firstCell, secondCell, i, i + 1);
      noDotClause(ans, firstCell, secondCell, i + 1, i);
    }
    return ans.toString();
  }

  /**
   * Constructs a clause for a cell pair with no dots.
   * @param clause The ongoing StringBuilder representation of a set of clauses
   * @param firstCell The first cell in a CellPair
   * @param secondCell The second cell in a CellPair
   * @param firstNum The first associated value to be added.
   * @param secondNum The second associated value to be added.
   */
  private void noDotClause(StringBuilder clause, int firstCell, int secondCell, int firstNum,
      int secondNum) {
    int firstLiteral, secondLiteral;
    firstCell *= -10;
    secondCell *= -10;
    firstLiteral = firstCell - firstNum;
    secondLiteral = secondCell - secondNum;
    clause.append(firstLiteral + " " + secondLiteral + " " + 0);
    clause.append(System.lineSeparator());
    clauses++;
  }

  /**
   * Writes a pair of two clauses that each contain two literals.
   * @param clause The ongoing StringBuilder representation of a set of clauses.
   * @param firstCell The first cell in a CellPair
   * @param secondCell The second cell in a CellPair
   * @param firstNum The first associated value to be added.
   * @param secondNum The second associated value to be added.
   */
  private void fourLiteralClause(StringBuilder clause, int firstCell, int secondCell, int firstNum,
      int secondNum) {
    int firstLiteral, secondLiteral, thirdLiteral, fourthLiteral;
    firstCell *= 10;
    secondCell *= 10;
    firstLiteral = -1 * (firstCell + firstNum);
    secondLiteral = secondCell + secondNum;
    thirdLiteral = -1 * (secondCell + firstNum);
    fourthLiteral = firstCell + secondNum;
    clause.append(firstLiteral + " " + secondLiteral + " " + 0);
    clause.append(System.lineSeparator());
    clauses++;
    clause.append(thirdLiteral + " " + fourthLiteral + " " + 0);
    clause.append(System.lineSeparator());
    clauses++;
  }

  /**
   * Writes a a pair of two clauses that each contain three literals.
   * @param clause The ongoing StringBuilder representation of a set of clauses.
   * @param firstCell The first cell in a CellPair
   * @param secondCell The second cell in a CellPair
   * @param firstNum The first associated value to be added.
   * @param secondNum The second associated value to be added.
   * @param thirdNum The third associated value to be added.
   */
  private void sixLiteralClause(StringBuilder clause, int firstCell, int secondCell, int firstNum,
      int secondNum, int thirdNum) {
    int firstLiteral, secondLiteral, thirdLiteral, fourthLiteral, fifthLiteral, sixthLiteral;
    firstCell *= 10;
    secondCell *= 10;
    firstLiteral = -1 * (firstCell + firstNum);
    secondLiteral = secondCell + secondNum;
    thirdLiteral = secondCell + thirdNum;
    fourthLiteral = -1 * (secondCell + firstNum);
    fifthLiteral = firstCell + secondNum;
    sixthLiteral = firstCell + thirdNum;
    clause.append(firstLiteral + " " + secondLiteral + " " + thirdLiteral + " " + 0);
    clause.append(System.lineSeparator());
    clauses++;
    clause.append(fourthLiteral + " " + fifthLiteral + " " + sixthLiteral + " " + 0);
    clause.append(System.lineSeparator());
    clauses++;
  }

  /**
   * Creates every orthogonal cell pair.
   */
  private void generateCellPairs() {
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 9; j++) {
        int currentCell = i * 10 + j;
        if (j != 9) {
          int rightCell = i * 10 + (j + 1);
          CellPair leftRightPair = new CellPair(currentCell, rightCell);
          pairs.add(leftRightPair);
        }
        if (i != 9) {
          int downCell = (i + 1) * 10 + j;
          CellPair upDownPair = new CellPair(currentCell, downCell);
          pairs.add(upDownPair);
        }
      }
    }
  }

  /**
   * Takes the SAT solver's decoded literals and parses them into the sudoku puzzle.
   * @param model The array of literals used to construct the puzzle.
   */
  private void parseSolution(int[] model) {
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
  private void visualizeSolution() {
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

  /**
   * Used in testing to test the sudoku solutions.
   * @return The solved sudoku puzzle.
   */
  public int[][] checkSolution() {
    solveSudoku();
    return sudoku;
  }

}
