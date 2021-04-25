package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Solves Kropki Sudoku by taking in a filepath and the constraints for white and black dots. These
 * constraints are parsed and passed to the model which solves the puzzle.
 */
public class SudokuSolver {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input filepath:");
        String filePath = scan.nextLine();
        System.out.println("Please input White Dot constraints:");
        String whiteStringConstraints = scan.nextLine();
        String[] whiteDotArr = whiteStringConstraints.split("\\s+");
        ArrayList<Integer> whiteDotConstraints = new ArrayList<>();
        for (String constraint : whiteDotArr) {
            try {
                int current = Integer.valueOf(constraint);
                if (current / 100 != 0 || current / 10 == 0 || current < 0) {
                    throw new IllegalArgumentException("Invalid Constraints");
                }
                whiteDotConstraints.add(current);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Constraints");
            }
        }
        System.out.println("Please input Black Dot constraints:");
        String blackStringConstraints = scan.nextLine();
        String[] blackDotArr = blackStringConstraints.split("\\s+");
        ArrayList<Integer> blackDotConstraints = new ArrayList<>();
        for (String constraint : blackDotArr) {
            try {
                int current = Integer.valueOf(constraint);
                if (current / 100 != 0 || current / 10 == 0 || current < 0) {
                    throw new IllegalArgumentException("Invalid Constraints");
                }
                blackDotConstraints.add(current);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Constraints");
            }
        }
        SudokuModel model = new SudokuModel(filePath, whiteDotConstraints, blackDotConstraints);
        model.solveSudoku();
    }

}
