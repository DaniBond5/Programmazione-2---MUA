package clients;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import mua.DateHeader;
import utils.DateEncoding;

/** DateEncode */
public class DateEncode {

  /**
   * Tests date encoding
   *
   * <p>Reads three integers from stdin corresponding to an year, month and day and emits a line in
   * the stout containing the encoding of the date corresponding to the exact midnight of such date
   * (in the "Europe/Rome" timezone).
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String data = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        data = s.nextLine();
      }
    }

    int[] dati_int = new int[3];
    String[] dati_stringa = data.split(" ");

    for (int i = 0; i < dati_int.length; i++) {
      dati_int[i] = Integer.parseInt(dati_stringa[i]);
    }

    ZonedDateTime date =
        ZonedDateTime.of(
            dati_int[0], dati_int[1], dati_int[2], 0, 0, 0, 0, DateEncoding.EUROPE_ROME);

    DateHeader TestData = DateHeader.from(date);
    System.out.println(TestData.getDate().format(DateTimeFormatter.RFC_1123_DATE_TIME));
  }
}
