package clients;

import java.io.IOException;
import mua.App;

/** MailboxCompose */
public class MailboxCompose {

  /**
   * Tests message composition and deletion
   *
   * <p>Runs the app on the commands in the stdin, the commands are limited to: MBOX, COMPOSE, READ,
   * DELTE.
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
