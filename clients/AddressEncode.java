package clients;

import java.util.ArrayList;
import java.util.Scanner;
import mua.Address;

/** AddressEncode */
public class AddressEncode {

  /**
   * Tests address encoding
   *
   * <p>Reads three lines from stdin corresponding to the (possibly empty) <em>display name</em>,
   * <em>local</em>, and <em>domain</em> parts of the address and emits a line in the stout
   * containing the encoding of the email address.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    ArrayList<String> data = new ArrayList<>();
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        data.add(s.nextLine());
      }
    }
    Address AddressTest = Address.from(data);
    System.out.println(AddressTest);
  }
}
