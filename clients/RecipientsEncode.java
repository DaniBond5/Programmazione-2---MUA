package clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import mua.RecipientHeader;

/** RecipientsEncode */
public class RecipientsEncode {

  /**
   * Tests recipients encoding
   *
   * <p>Reads a series of lines from stidn, each containing a comma separated list of three strings
   * corresponding to the (possibly empty) <em>display name</em>, <em>local</em>, and
   * <em>domain</em> parts of an address and emits a line in stdout containing the encoding of the
   * recipients header obtained using such addresses.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    List<List<String>> addressesParts = new ArrayList<>();
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        List<String> parti = List.of(s.nextLine().split(", "));
        addressesParts.add(parti);
      }
    }
    RecipientHeader RecipientTest = RecipientHeader.from(addressesParts);
    System.out.println(RecipientTest);
  }
}
