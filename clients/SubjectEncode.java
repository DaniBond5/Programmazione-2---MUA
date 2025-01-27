package clients;

import java.util.Scanner;
import mua.SubjectHeader;

/** SubjectEncode */
public class SubjectEncode {

  /**
   * Tests subject value encoding
   *
   * <p>Reads a line from stdin containing the value of a subject header and emits its encoded
   * version in the stdout.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String subjectTest = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        subjectTest = s.nextLine();
      }
    }
    SubjectHeader SubjectHeaderTest = SubjectHeader.from(subjectTest);
    System.out.println(SubjectHeaderTest.getSubject());
  }
}
