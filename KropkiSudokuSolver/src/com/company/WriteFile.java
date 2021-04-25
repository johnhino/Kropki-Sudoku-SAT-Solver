package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class to write a file.
 */
public class WriteFile {
  private String filePath;
  private boolean appendToFile = false;

  /**
   * Constructor for the write file class.
   * @param filePath the path desired to the file to write to
   */
  public WriteFile(String filePath) {
    this.filePath = filePath;
  }

  /**
   * Method to go in and find the file and print to it.
   * @param textLine the line of text to be written
   * @throws IOException exception to be thrown
   */
  public void writeToFile(String textLine) throws IOException {
    FileWriter write = new FileWriter(filePath, appendToFile);
    PrintWriter printLine = new PrintWriter(write);
    printLine.print(textLine);
    printLine.close();
  }
}
