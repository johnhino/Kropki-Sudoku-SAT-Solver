package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class SudokuSolver {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input filepath:");
        String filePath = scan.nextLine();
        System.out.println("Please input Sudoku constraints:");
        String stringConstraints = scan.nextLine();
        String[] constraintsArr = stringConstraints.split("\\s+");
        ArrayList<Integer> constraints = new ArrayList<>();
        for (String constraint : constraintsArr) {
            try {
                int current = Integer.valueOf(constraint);
                constraints.add(current);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Constraints");
            }
        }
        SudokuModel model = new SudokuModel(filePath, constraints);
        model.solveSudoku();
    }

}
