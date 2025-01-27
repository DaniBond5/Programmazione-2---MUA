package clients;

import java.io.IOException;
import mua.App;

/** MailboxList */
public class MailboxList {
  /**
   * Tests mailbox listing
   *
   * <p>Runs the app on the commands in the stdin, the commands are limited to: MBOX, LSM, LSE (with
   * no sort parameter).
   *
   * @param args not used
   */
  public static void main(String[] args) {
    try {
      String[] array = new String[] {"tests/mbox"};
      App.main(array);
    } catch (IOException e) {
      System.out.println("Errore");
    }
  }
}
