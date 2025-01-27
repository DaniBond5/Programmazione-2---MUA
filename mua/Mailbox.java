package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import utils.Storage;

/**
 * Una {@code Mailbox} è una collezione di messaggi salvati su disco.
 *
 * <p>In particolare una mailbox è composta da:
 *
 * <ul>
 *   <li>un nome che corrisponde al nome della sua cartella;
 *   <li>un insieme di entry, formati dai messaggi e dal loro riferimento su disco;
 *   <li>un riferimento alla cartella contenente i messaggi della mailbox.
 * </ul>
 *
 * <p>Le mailbox sono oggetti mutabili, che permettono di leggere, aggiungere, rimuovere e
 * riordinare i messaggi secondo vari ordini:
 *
 * <ul>
 *   <li>Ordine per data;
 *   <li>Ordine per soggetto;
 *   <li>Ordine per mittente;
 *   <li>Ordine lessicografico per destinatari;
 * </ul>
 *
 * I vari ordinamenti possono essere svolti in ordine crescente o decrescente.
 */
public class Mailbox implements Iterable<Mailbox.MailboxEntry> {

  /**
   * AF: Il nome della mailbox è contenuto nel campo name. La lista di messaggi è formata da una
   * lista di MailboxEntries che collegano ai vari messaggi salvati nella mailbox il loro
   * riferimento su disco. Il riferimento su disco della mailbox è contenuto nel campo box.
   *
   * <p>RI: name!=null; messages!=null; box!=null;
   */

  /** Il nome della mailbox. */
  private final String name;

  /** L'insieme di elementi contenuti nella mailbox. */
  private final List<MailboxEntry> messages;

  /** Un riferimento alla cartella in cui sono salvati i dati della mailbox. */
  private final Storage.Box box;

  /**
   * Record che associa ad ogni messaggio prelevato da disco il suo riferimento su disco.
   *
   * <p>Ho optato per questa soluzione per semplificare l'operazione di delete, poichè senza un
   * collegamento tra il messaggio e il suo riferimento "fisico" su disco, cancellare un messaggio
   * utilizzando l'applicazione risulta complicato: L'ordinamento dei messaggi, senza alcun
   * collegamento, non rispecchia sempre l'ordine dei loro riferimenti fisici.
   */
  public record MailboxEntry(Storage.Box.Entry entry, Message message) {

    /**
     * Costruttore che crea una entry della mailbox dato il riferimento su disco di un messaggio e
     * il messaggio stesso.
     *
     * @param entry la {@link Storage} del messaggio, cioè il suo riferimento su disco.
     * @param message il messaggio.
     * @throws NullPointerException se entry o message sono {@code null}.
     */
    public MailboxEntry {
      Objects.requireNonNull(entry);
      Objects.requireNonNull(message);
    }
  }

  /**
   * Costruttore che costruisce una {@code Mailbox} dal suo riferimento fisico {@link Storage.Box}.
   *
   * <p>Data una box {@link Storage.Box} infatti, questo metodo costruisce una {@code Mailbox} che
   * ha come riferimento la box, il suo nome e i messaggi salvati in essa, collegandoli ai loro
   * riferimenti su disco.
   *
   * @param box la cartella su disco rappresentante la mailbox.
   * @throws NullPointerException se la box o una delle sue {@link Storage.Box.Entry} è {@code
   *     null}.
   */
  public Mailbox(Storage.Box box) throws NullPointerException {
    ArrayList<MailboxEntry> messages = new ArrayList<>();
    for (Storage.Box.Entry entry : Objects.requireNonNull(box).entries()) {
      messages.add(new MailboxEntry(entry, Message.from(Objects.requireNonNull(entry.content()))));
    }
    this.name = box.toString();
    this.messages = messages;
    this.box = box;
  }

  /**
   * Metodo che restituisce il nome della mailbox.
   *
   * @return il nome della mailbox.
   */
  public String getName() {
    return name;
  }

  /**
   * Metodo che restituisce il numero di messaggi contenuti nella mailbox, ovvero la sua dimensione
   * attuale.
   *
   * @return il numero di messaggi nella mailbox, cioè la sua dimensione.
   */
  public int size() {
    return messages.size();
  }

  /**
   * Metodo che legge un messaggio contenuto nella mailbox dato il suo indice.
   *
   * <p>L'indice si basa sull'ultimo ordinamento effettuato.
   *
   * @param index l'indice del messaggio da leggere.
   * @return il messaggio indicato dall'indice.
   * @throws IllegalArgumentException se l'indice non è valido, cioè se è minore di zero o è
   *     maggiore della dimensione della mailbox.
   */
  public Message read(int index) throws IllegalArgumentException {
    if (index < 0 || index >= messages.size())
      throw new IllegalArgumentException("L'indice inserito non è valido");

    return messages.get(index).message;
  }

  /**
   * Metodo che ordina la lista basandosi sulle date dei messaggi.
   *
   * <p>L'ordinamento viene effettuato in ordine crescente o decrescente a seconda del parametro
   * dato, se {@code true} l'ordinamento è in ordine decrescente, crescente altrimenti
   *
   * @param reverse indica se effettuare l'ordinamento in ordine crescente, o decrescente.
   */
  public void dateOrder(boolean reverse) {
    Comparator<Mailbox.MailboxEntry> dataComparator =
        new Comparator<Mailbox.MailboxEntry>() {

          @Override
          public int compare(MailboxEntry m1, MailboxEntry m2) {
            return m1.message.getDateHeader().compareTo(m2.message.getDateHeader());
          }
        };

    if (!reverse) {
      messages.sort(dataComparator);
    } else {
      messages.sort(dataComparator.reversed());
    }
  }

  /**
   * Metodo che ordina la lista basandosi sui mittenti dei messaggi.
   *
   * <p>L'ordinamento viene effettuato in ordine crescente o decrescente a seconda del parametro
   * dato, se {@code true} l'ordinamento è in ordine decrescente, crescente altrimenti
   *
   * @param reverse indica se effettuare l'ordinamento in ordine crescente, o decrescente.
   */
  public void senderOrder(boolean reverse) {
    Comparator<Mailbox.MailboxEntry> sendeComparator =
        new Comparator<Mailbox.MailboxEntry>() {

          @Override
          public int compare(MailboxEntry m1, MailboxEntry m2) {
            return m1.message.getFromHeader().compareTo(m2.message.getFromHeader());
          }
        };

    if (!reverse) {
      messages.sort(sendeComparator);
    } else {
      messages.sort(sendeComparator.reversed());
    }
  }

  /**
   * Metodo che ordina la lista basandosi sui destinatari dei messaggi.
   *
   * <p>L'ordinamento viene effettuato in ordine crescente o decrescente a seconda del parametro
   * dato, se {@code true} l'ordinamento è in ordine decrescente, crescente altrimenti
   *
   * @param reverse indica se effettuare l'ordinamento in ordine crescente, o decrescente.
   */
  public void recipientsOrder(boolean reverse) {
    Comparator<Mailbox.MailboxEntry> recipientsComparator =
        new Comparator<Mailbox.MailboxEntry>() {

          @Override
          public int compare(MailboxEntry m1, MailboxEntry m2) {
            return m1.message.getToHeader().compareTo(m2.message.getToHeader());
          }
        };

    if (!reverse) {
      messages.sort(recipientsComparator);
    } else {
      messages.sort(recipientsComparator.reversed());
    }
  }

  /**
   * Metodo che ordina la lista basandosi sugli oggetti dei messaggi.
   *
   * <p>L'ordinamento viene effettuato in ordine crescente o decrescente a seconda del parametro
   * dato, se {@code true} l'ordinamento è in ordine decrescente, crescente altrimenti
   *
   * @param reverse indica se effettuare l'ordinamento in oridne crescente, o decrescente.
   */
  public void subjectOrder(boolean reverse) {
    Comparator<Mailbox.MailboxEntry> subjeComparator =
        new Comparator<Mailbox.MailboxEntry>() {

          @Override
          public int compare(MailboxEntry m1, MailboxEntry m2) {
            return m1.message.getSubjectHeader().compareTo(m2.message.getSubjectHeader());
          }
        };

    if (!reverse) {
      messages.sort(subjeComparator);
    } else {
      messages.sort(subjeComparator.reversed());
    }
  }

  /**
   * Metodo che restituisce una vista non modificabile dell'elenco dei messaggi, associati al loro
   * riferimento fisico.
   *
   * @return una vista non modificabile dei messaggi associati al loro riferimento fisico.
   */
  public List<MailboxEntry> getMessages() {
    return Collections.unmodifiableList(messages);
  }

  /**
   * Metodo che elimina il messaggio specificato dall'indice dato.
   *
   * <p>Questo indice si basa sull'ultimo ordinamento effettuato.
   *
   * @param index l'indice dell'elemento che si vuole cancellare.
   * @return {@code true} se l'elemento è stato cancellato, {@code false} se la cancellazione non è
   *     andata a buon fine.
   * @throws IllegalArgumentException se l'indice dato non è valido, cioè è minore di zero o è
   *     maggiore della dimensione della mailbox.
   */
  public boolean delete(int index) throws IllegalArgumentException {
    if (index < 0 || index >= messages.size())
      throw new IllegalArgumentException("L'indice inserito non è valido");

    MailboxEntry mbox_entry = messages.get(index);
    if (!mbox_entry.entry.delete()) {
      return false;
    }
    messages.remove(index);
    return true;
  }

  /**
   * Metodo che aggiunge un messaggio alla mailbox.
   *
   * <p>Dopo aver aggiunto il messaggio, la lista viene ordinata in ordine di data decrescente.
   * Questo per mantenere la lista sempre ordinata, e ho scelto quell'ordine perchè è quello
   * "standard" del comando LSE.
   *
   * @param m il messaggio da salvare nella mailbox.
   * @throws NullPointerException se il messaggio è {@code null}.
   */
  public void addMessage(Message m) throws NullPointerException {
    Storage.Box.Entry entry = box.entry(Objects.requireNonNull(m).toSequence());
    messages.add(new MailboxEntry(Objects.requireNonNull(entry), m));
    dateOrder(true);
  }

  @Override
  public Iterator<Mailbox.MailboxEntry> iterator() {
    return Collections.unmodifiableList(messages).iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Mailbox: \n");
    for (Mailbox.MailboxEntry mbox_entry : messages) {
      sb.append(mbox_entry.message.toString() + "\n");
    }
    return sb.toString().trim();
  }
}
