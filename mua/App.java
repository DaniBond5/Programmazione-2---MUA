package mua;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.*;

/** The application class */
public class App {

  /**
   * Runs the REPL.
   *
   * <p>Develop here the REPL, see the README.md for more details.
   *
   * @param args the first argument is the mailbox base directory.
   * @throws IOException se avviene un errore di I/O.
   */
  public static void main(String[] args) throws IOException {
    Storage S = new Storage(args[0]);
    List<Storage.Box> boxes = S.boxes();

    ArrayList<Mailbox> mailboxes = new ArrayList<>();
    for (Storage.Box box : boxes) {
      mailboxes.add(new Mailbox(box));
    }

    Mailbox currentMailbox = null;
    try (UIInteract ui = UIInteract.getInstance()) {
      for (; ; ) {
        String[] input = ui.command("> ");
        if (input == null) break;
        switch (input[0].toUpperCase().trim()) {
          case "LSM":
            ui.output(mailboxesTable(mailboxes));
            break;

          case "MBOX":
            if (input.length == 1) {
              ui.error("Error: No index given");
              break;
            }
            int index = Integer.parseInt(input[1]) - 1;
            if (index < 0 || index > mailboxes.size()) {
              ui.error("Error: Given index is not valid");
              break;
            }
            currentMailbox = mailboxes.get(index);
            break;

          case "LSE":
            if (currentMailbox == null) {
              ui.error("Error: Operation requires to select a mailbox first");
              break;
            }
            if (input.length == 1) {
              currentMailbox.dateOrder(true);
              ui.output(messagesTable(currentMailbox));
            } else {
              switch (input[1].toUpperCase().trim()) {
                case "F":
                  currentMailbox.senderOrder(false);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "F-":
                  currentMailbox.senderOrder(true);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "T":
                  currentMailbox.recipientsOrder(false);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "T-":
                  currentMailbox.recipientsOrder(true);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "S":
                  currentMailbox.subjectOrder(false);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "S-":
                  currentMailbox.subjectOrder(true);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "D":
                  currentMailbox.dateOrder(false);
                  ui.output(messagesTable(currentMailbox));
                  break;

                case "D-":
                  currentMailbox.dateOrder(true);
                  ui.output(messagesTable(currentMailbox));
                  break;

                default:
                  ui.error("Error: Unknown sorting operand given: " + input[1]);
                  break;
              }
            }
            break;

          case "READ":
            if (input.length == 1) {
              ui.error("Error: No index given");
              break;
            }
            if (currentMailbox == null) {
              ui.error("Error: Operation requires to select a mailbox first");
              break;
            }
            try {
              Message m = currentMailbox.read(Integer.parseInt(input[1]) - 1);
              ui.output(messageCard(m));
            } catch (IllegalArgumentException e) {
              ui.error("Error: " + e.getMessage());
            }
            break;

          case "DELETE":
            if (input.length == 1) {
              ui.error("Error: No index given");
              break;
            }
            if (currentMailbox == null) {
              ui.error("Error: Operation requires to select a mailbox first");
              break;
            }
            try {
              if (!currentMailbox.delete(Integer.parseInt(input[1]) - 1))
                ui.error("Error: Delete encountered a problem");
            } catch (IllegalArgumentException e) {
              ui.error("Error: " + e.getMessage());
            }
            break;

          case "COMPOSE":
            if (currentMailbox == null) {
              ui.error("Error: Operation requires to select a mailbox first");
              break;
            }
            ArrayList<Header> headers = new ArrayList<>();

            headers.add(FromHeader.from(ui.line("From: ")));
            headers.add(
                RecipientHeader.from(
                    AddressEncoding.decode(ASCIICharSequence.of(ui.line("To: ")))));
            headers.add(SubjectHeader.from(ui.line("Subject: ")));
            headers.add(DateHeader.from(ui.line("Date: ")));

            ArrayList<List<Header>> partsHeaders = new ArrayList<>();
            ui.prompt("Text Body (. to end):");
            String textbody = "";
            for (; ; ) {
              String line = ui.line();
              if (line.equals(".")) break;
              textbody = textbody + line + "\n";
            }
            ui.prompt("HTML Body (. to end):");
            String htmlbody = "";
            for (; ; ) {
              String line = ui.line();
              if (line.equals(".")) break;
              htmlbody = htmlbody + line + "\n";
            }
            List<String> bodies = List.of(textbody, htmlbody);
            if (!htmlbody.isEmpty())
              partsHeaders.add(List.of(ContentHeader.from("multipart/alternative", "")));
            if (!ASCIICharSequence.isAscii(textbody)) {
              partsHeaders.add(List.of(ContentHeader.from("text/plain", "utf-8")));
            } else {
              partsHeaders.add(List.of(ContentHeader.from("text/plain", "us-ascii")));
            }
            partsHeaders.add(List.of(ContentHeader.from("text/html", "utf-8")));
            currentMailbox.addMessage(Message.from(headers, partsHeaders, bodies));
            break;

          case "EXIT":
            return;

          default:
            ui.error("Unknown command: " + input[0]);
            break;
        }
      }
    } catch (IOException e) {
      throw new IOException();
    }
  }

  /**
   * Metodo che dato un messaggio {@link Message}, ne restituisce una Stringa rappresentante la sua
   * card {@link UICard}.
   *
   * @param m il messaggio.
   * @return la {@code UICard} del messaggio dato.
   */
  private static String messageCard(Message m) {
    ArrayList<String> headers = new ArrayList<>(Arrays.asList("From", "To", "Subject", "Date"));
    ArrayList<String> values = new ArrayList<>();
    String to = "";
    for (Address a : m.getToHeader().getRecipients()) {
      to = to + a + "\n";
    }

    values.add(m.getFromHeader().getValue());
    values.add(to.trim());
    values.add(m.getSubjectHeader().getValue());
    values.add(m.getDateHeader().getValue());
    for (Message.Part part : m.getParts()) {
      ContentHeader content = part.getContentHeader();
      headers.add("Part\n" + content.getContentType());
      values.add(part.getBody());
    }
    return UICard.card(headers, values);
  }

  /**
   * Metodo che, data una mailbox {@link Mailbox}, ne restituisce la tabella {@link UITable}.
   *
   * @param mbox la mailbox.
   * @return la {@code UICard} della mailbox data.
   */
  private static String messagesTable(Mailbox mbox) {
    List<Mailbox.MailboxEntry> mbox_entries = mbox.getMessages();

    ArrayList<List<String>> values = new ArrayList<>();
    for (Mailbox.MailboxEntry mbox_entry : mbox_entries) {
      Message m = mbox_entry.message();

      String date =
          m.getDateHeader().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
              + "\n"
              + m.getDateHeader().getDate().format(DateTimeFormatter.ISO_LOCAL_TIME);

      String from =
          m.getFromHeader().getSender().getLocal()
              + "@"
              + m.getFromHeader().getSender().getDomain();

      String to = "";
      for (Address a : m.getToHeader().getRecipients()) {
        to = to + a.getLocal() + "@" + a.getDomain() + "\n";
      }
      to = to.trim();
      String subject = m.getSubjectHeader().getValue();
      values.add(List.of(date, from, to, subject));
    }
    return UITable.table(List.of("Date", "From", "To", "Subject"), values, true, true);
  }

  /**
   * Metodo che, data una lista di mailbox {@link Mailbox}, ne restituisce la tabella {@link
   * UITable}.
   *
   * @param mailboxes la lista di mailbox.
   * @return la {@code UITable} della lista di mailbox.
   */
  private static String mailboxesTable(List<Mailbox> mailboxes) {
    ArrayList<List<String>> values = new ArrayList<>();
    for (Mailbox mbox : mailboxes) {
      values.add(List.of(mbox.getName(), Integer.toString(mbox.size())));
    }
    return UITable.table(List.of("Mailbox", "# messages"), values, true, false);
  }
}
