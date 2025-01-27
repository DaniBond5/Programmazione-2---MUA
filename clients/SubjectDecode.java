package clients;

import java.util.Scanner;
import mua.SubjectHeader;

/** SubjectDecode */
public class SubjectDecode {

  /**
   * Tests subject value decoding
   *
   * <p>Reads a line from stdin containing the encoding of the value of a subject header and emits
   * its decoded version in the stdout.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String subjectTestString = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        subjectTestString = s.nextLine();
      }
    }
    SubjectHeader SubjectHeaderTest = SubjectHeader.from(subjectTestString);
    System.out.println(SubjectHeaderTest.getValue());
  }
}
