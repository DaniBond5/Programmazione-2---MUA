package mua;

import java.util.List;
import java.util.Objects;
import utils.ASCIICharSequence;

/**
 * Un {@code FromHeader} è una intestazione {@link Header} che specifica l'indirizzo del mittente di
 * un messaggio.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>Questa intestazione ha come valore l'indirizzo del mittente, e come tipo "From".
 *
 * <p>L'uguaglianza e ordinamento sono basati sull'indirizzo del mittente, facendo riferimento ai
 * metodi equals e compareTo di {@link Address}.
 */
public class FromHeader implements Header, Comparable<FromHeader> {

  /*-
   * AF: Il mittente è contenuto nel campo sender. Il tipo viene restituito dal metodo getType ereditato da {@link Header}.
   *     Per esempio dato l'indirizzo mittente: Daniele Buondonno <danibond@gmail.com>, questa intestazione avrà nel campo sender questo indirizzo.
   *
   * RI:
   *      sender!=null && RI_Address(sender)==true;
   */

  /** L'indirizzo del mittente. */
  private final Address sender;

  /**
   * Costruttore che, dato un indirizzo, costruisce un {@code FromHeader} che ha come valore
   * l'indirizzo del mittente.
   *
   * @param sender L'{@link Address} del mittente.
   * @throws NullPointerException se il mittente è {@code null}.
   */
  private FromHeader(final Address sender) throws NullPointerException {
    this.sender = Objects.requireNonNull(sender);
  }

  /**
   * Metodo che data una Stringa che rappresenta l'indirizzo del mittente restituisce un {@code
   * FromHeader} che ha come valore l'indirizzo estratto dalla stringa.
   *
   * <p>La stringa data deve essere formattata nel seguente modo: Displayname, racchiuso tra
   * virgolette se formato da più di due parole, local e dominio devono essere racchiusi tra i
   * simboli {@literal <} e {@literal >} e separati dal simbolo {@literal @}. Le parti riguardanti
   * il locale e il dominio devono anche rispettare la {@code regexp ``
   * [\w\d.!$%&'*+/=?^_`{|}~-]+}}.
   *
   * <p>In particolare, se il metodo riceve la stringa: "Daniele Buondonno {@literal <}
   * danibond@gmail.com {@literal >}" Il metodo restituisce un FromHeader che ha come valore
   * l'indirizzo: Daniele Buondonno {@literal <} danibond@gmail.com {@literal >}.
   *
   * @param address L'indirizzo del mittente.
   * @return Un {@code FromHeader} con mittente l'indirizzo rappresentato dalla stringa.
   * @throws IllegalArgumentException se l'indirizzo contiene caratteri non ASCII, o se è {@code
   *     null}.
   */
  public static FromHeader from(final String address) throws IllegalArgumentException {
    if (!ASCIICharSequence.isAscii(address)) {
      throw new IllegalArgumentException(
          "La stringa data non deve contenere caratteri non ASCII e non può essere null.");
    }
    return new FromHeader(Address.from(address));
  }

  /**
   * Metodo che restituisce un {@code FromHeader} data una lista di stringhe rappresentanti le parti
   * di un indirizzo. Il {@code FromHeader} risultante avrà come valore l'indirizzo estratto dalla
   * lista.
   *
   * <p>In particolare la lista deve contenere nel seguente ordine: Displayname, Local e Domain
   * dell'indirizzo del mittente. Se il metodo riceve una lista che ha gli elementi: Daniele
   * Buondonno, danibond, gmail.com; questo restituisce un FromHeader che ha come valore l'indirizzo
   * : Daniele Buondonno {@literal <} danibond@gmail.com {@literal >}. Se l'indirizzo non ha
   * Displayname, la lista deve contenere una stringa vuota "" come primo elemento.
   *
   * @param addressParts La lista di parti dell'indirizzo del mittente.
   * @return Il {@code FromHeader} con mittente l'indirizzo formato dalla lista di parti.
   * @throws IllegalArgumentException se la lista non è formata da tre parti.
   * @throws NullPointerException se la lista è {@code null}.
   */
  public static FromHeader from(final List<String> addressParts)
      throws IllegalArgumentException, NullPointerException {
    if (addressParts.isEmpty() || addressParts.size() != 3) {
      throw new IllegalArgumentException(
          "Un indirizzo è formato deve essere formato da tre parti.");
    }
    return new FromHeader(Address.from(Objects.requireNonNull(addressParts)));
  }

  /**
   * Metodo che restituisce l'indirizzo {@link Address} del mittente.
   *
   * @return L'indirizzo del mittente.
   */
  public Address getSender() {
    return sender;
  }

  @Override
  public String getType() {
    return "From";
  }

  @Override
  public String getValue() {
    return sender.toString();
  }

  @Override
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public int compareTo(FromHeader f) {
    return sender.compareTo(f.getSender());
  }

  @Override
  public int hashCode() {
    return sender.hashCode() * 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof FromHeader)) return false;
    return sender.equals(((FromHeader) obj).getSender());
  }

  @Override
  public String toString() {
    return getType() + ": " + getValue();
  }
}
