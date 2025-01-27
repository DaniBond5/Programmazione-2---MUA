package clients;

import java.util.Scanner;
import mua.Address;

/** AddressDecode */
public class AddressDecode {

  /**
   * Tests address decoding
   *
   * <p>Reads a line from stdin containing the encoding of an email address and emits three lines in
   * the stout corresponding to the (possibly empty) <em>display name</em>, <em>local</em>, and
   * <em>domain</em> parts of the address.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String email = "";
    try (Scanner s = new Scanner(System.in)) {
      while (s.hasNextLine()) {
        email = s.nextLine();
      }
    }
    Address AddressTest = Address.from(email);
    System.out.println(AddressTest.getDisplayname());
    System.out.println(AddressTest.getLocal());
    System.out.println(AddressTest.getDomain());
  }
}
