package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.AddressEncoding;
import utils.LexSort;

/**
 * Un {@code RecipientHeader} è una intestazione {@code Header} che specifica gli indirizzi dei
 * destinatari (o l'indirizzo del destinatario, se ne è presente solo uno).
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>Questa intestazione ha come valore gli indirizzi dei destinatari, e come tipo "To".
 *
 * <p>L'uguaglianza si basa sulla lista degli indirizzi, mentre l'ordinamento sul comparatore dato
 * da {@link LexSort}.
 */
public class RecipientHeader implements Header, Iterable<Address>, Comparable<RecipientHeader> {

  /**
   * AF: Il valore di questo header,cioè i destinatari del messaggio, sono contenuti nel campo
   * "recipients". Il tipo viene restituito dal metodo getType ereditato da {@link Header}.
   *
   * <p>RI: recipients!=null; Per ogni indirizzo in recipients: RI_Address==true.
   */

  /** La lista degli indirizzi dei destinatari */
  private final List<Address> recipients;

  /**
   * Metodo che costruisce un {@code RecipientHeader} data una lista di {@link Address} dei
   * destinatari.
   *
   * <p>Il {@code RecipientHeader} creato ha come valore la lista di indirizzi dei destinatari.
   *
   * @param recipients la lista di indirizzi.
   * @throws NullPointerException se la lista è {@code null}.
   */
  private RecipientHeader(List<Address> recipients) throws NullPointerException {
    this.recipients = Objects.requireNonNull(recipients);
  }

  /**
   * Metodo che, data una lista di liste di Stringhe rappresentanti le parti di uno o più indirizzi,
   * restituisce un {@code RecipientHeader} che ha come valore una lista di quest'ultimi.
   *
   * <p>La lista di Stringhe per ogni indirizzo deve contenere nel seguente ordine: Displayname,
   * Local e Domain.
   *
   * <p>Se il metodo riceve una lista che ha due liste che contengono, rispettivamente:
   *
   * <ul>
   *   <li>Daniele Buondonno
   *   <li>danibond
   *   <li>gmail.com
   * </ul>
   *
   * ,
   *
   * <ul>
   *   <li>""
   *   <li>marcorossi
   *   <li>mail.it
   * </ul>
   *
   * <p>Il {@code RecipientHeader} risultante avrà come valore una lista dei due indirizzi
   * risultanti: Daniele Buondonno {@literal <} danibond@gmail.com {@literal >}, {@literal <}
   * marcorossi@gmail.com {@literal >}.
   *
   * @param recipientsList la lista di parti di uno o più indirizzi.
   * @return Un {@code RecipientHeader} che ha come destinatari gli indirizzi estratti dalla lista.
   * @throws IllegalArgumentException se una delle liste non è formata da tre elementi.
   * @throws NullPointerException se la lista è {@code null}.
   */
  public static RecipientHeader from(List<List<String>> recipientsList)
      throws IllegalArgumentException, NullPointerException {
    ArrayList<Address> recipientsAddresses = new ArrayList<>();
    for (List<String> recipientParts : Objects.requireNonNull(recipientsList)) {
      recipientsAddresses.add(Address.from(recipientParts));
    }
    return new RecipientHeader(recipientsAddresses);
  }

  /**
   * Metodo che data una {@link ASCIICharSequence}, che contiene uno o più indirizzi, restituisce un
   * {@code RecipientHeader} che ha come valore una lista degli indirizzi estratti dalla sequenza.
   *
   * @param sequence la sequenza.
   * @return Un {@code FromHeader} che ha come destinatari gli indirizzi estratti dalla sequenza.
   * @throws NullPointerException se la sequenza data è {@code null}.
   */
  public static RecipientHeader from(ASCIICharSequence sequence) throws NullPointerException {
    return RecipientHeader.from(AddressEncoding.decode(Objects.requireNonNull(sequence)));
  }

  @Override
  public String getType() {
    return "To";
  }

  @Override
  public String getValue() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < recipients.size() - 1; i++) {
      sb.append(recipients.get(i).toString() + "\n");
    }
    sb.append(recipients.get(recipients.size() - 1));
    return sb.toString();
  }

  @Override
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public Iterator<Address> iterator() {
    return Collections.unmodifiableList(recipients).iterator();
  }

  /**
   * Metodo che restituisce una vista non modificabile della lista di destinatari.
   *
   * @return una vista non modificabile dei destinatari.
   */
  public List<Address> getRecipients() {
    return Collections.unmodifiableList(recipients);
  }

  @Override
  public int compareTo(RecipientHeader rec) {
    Comparator<Iterable<Address>> comparator = LexSort.lexicographicComparator();
    return comparator.compare(this.recipients, rec.getRecipients());
  }

  @Override
  public int hashCode() {
    return recipients.hashCode() * 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof RecipientHeader)) {
      return false;
    }
    return recipients.equals(((RecipientHeader) obj).getRecipients());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("To: ");
    int n = recipients.size();
    for (int i = 0; i < n - 1; i++) {
      sb.append(recipients.get(i) + ", ");
    }
    sb.append(recipients.get(n - 1));
    return sb.toString();
  }
}
