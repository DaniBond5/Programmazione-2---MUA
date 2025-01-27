package clients;

import java.util.Scanner;
import mua.DateHeader;

/** DateDecode */
public class DateDecode {

  /**
   * Tests date decoding
   *
   * <p>Reads a line from stdin containing the encoding of a date and emits the corresponding day of
   * week in the stout.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String dataString = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        dataString = s.nextLine();
      }
    }

    DateHeader DataTest = DateHeader.from(dataString);
    System.out.println(DataTest.getDate().getDayOfWeek());
  }
}
