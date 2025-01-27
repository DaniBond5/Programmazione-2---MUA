package clients;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import mua.*;
import utils.ASCIICharSequence;

/** MessageEncode */
public class MessageEncode {

  public static final ZonedDateTime DATE =
      ZonedDateTime.of(2023, 12, 6, 12, 30, 20, 200, ZoneId.of("Europe/Rome"));

  /**
   * Tests message encoding
   *
   * <p>Reads a message from stdin and emits its encoding on the stdout.
   *
   * <p>The stdin contains:
   *
   * <ul>
   *   <li>the sender address (three lines, see {@link AddressDecode}),
   *   <li>two recipient addresses (three lines each, as above),
   *   <li>the subject (one line),
   *   <li>the text part (one line, possibly empty),
   *   <li>the HTML part (one line, possibly empty).
   * </ul>
   *
   * To such information, the program adds the date corresponding to {@link #DATE}.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    ArrayList<String> lines = new ArrayList<>();
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        lines.add(s.nextLine());
      }
    }

    ArrayList<String> senderParts = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      senderParts.add(lines.get(i));
    }

    ArrayList<List<String>> recipientsParts = new ArrayList<>();
    for (int i = 3; i < 9; i = i + 3) {
      recipientsParts.add(List.of(lines.get(i), lines.get(i + 1), lines.get(i + 2)));
    }

    ArrayList<Header> headers = new ArrayList<>();
    headers.add(FromHeader.from(senderParts));
    headers.add(RecipientHeader.from(recipientsParts));
    headers.add(SubjectHeader.from(lines.get(9)));
    headers.add(DateHeader.from(DATE));

    ArrayList<List<Header>> partsHeaders = new ArrayList<>();
    String body1 = lines.get(10);
    String body2 = lines.get(11);
    if (body2.isEmpty()) {
      if (!ASCIICharSequence.isAscii(body1)) {
        partsHeaders.add(List.of(ContentHeader.from("text/plain", "utf-8")));
      } else {
        partsHeaders.add(List.of(ContentHeader.from("text/plain", "us-ascii")));
      }
    } else {
      partsHeaders.add(List.of(ContentHeader.from("multipart/alternative", "")));
      if (!ASCIICharSequence.isAscii(body1)) {
        partsHeaders.add(List.of(ContentHeader.from("text/plain", "utf-8")));
      } else {
        partsHeaders.add(List.of(ContentHeader.from("text/plain", "us-ascii")));
      }
      partsHeaders.add(List.of(ContentHeader.from("text/html", "utf-8")));
    }

    Message MessageTest = Message.from(headers, partsHeaders, List.of(body1, body2));
    System.out.println(MessageTest);
  }
}
