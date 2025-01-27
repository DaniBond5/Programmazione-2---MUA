package clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import mua.Address;
import mua.RecipientHeader;
import utils.ASCIICharSequence;
import utils.AddressEncoding;

/** RecipientsDecode */
public class RecipientsDecode {

  /**
   * Tests recipients decoding
   *
   * <p>Reads a line from stdin containing the encoding of the recipients header and for every
   * address in the header emits a line in stdout containing a comma separated list of three strings
   * corresponding to the (possibly empty) <em>display name</em>, <em>local</em>, and
   * <em>domain</em> parts of the address.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String line = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        line = s.nextLine();
      }
    }

    List<List<String>> addresses = new ArrayList<List<String>>();

    line = line.substring(4);
    addresses = AddressEncoding.decode(ASCIICharSequence.of(line));
    RecipientHeader RecipientTest = RecipientHeader.from(addresses);

    List<Address> recipients = RecipientTest.getRecipients();
    for (int i = 0; i < recipients.size() - 1; i++) {
      System.out.println(
          recipients.get(i).getDisplayname()
              + ", "
              + recipients.get(i).getLocal()
              + ", "
              + recipients.get(i).getDomain());
    }
    System.out.print(
        recipients.get(recipients.size() - 1).getDisplayname()
            + ", "
            + recipients.get(recipients.size() - 1).getLocal()
            + ", "
            + recipients.get(recipients.size() - 1).getDomain());
  }
}
