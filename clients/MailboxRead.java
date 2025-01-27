package clients;

import java.io.IOException;
import mua.App;

/** MailboxRead */
public class MailboxRead {
  /**
   * Tests message reading
   *
   * <p>Runs the app on the commands in the stdin, the commands are limited to: MBOX, READ.
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
