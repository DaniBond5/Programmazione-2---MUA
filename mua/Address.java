package mua;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.AddressEncoding;

/**
 * Un {@code Address} è un indirizzo email composto da tre parti: Displayname, Local e Domain.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>In particolare:
 *
 * <ul>
 *   <li>Il <em>Displayname</em> è un nickname dell'indirizzo vero e proprio;
 *   <li>Il <em>Local</em> è la parte dell'indirizzo email che precede la @;
 *   <li>Il <em>Domain</em> è il dominio dell'indirizzo email, cioè la parte che segue la @;
 * </ul>
 *
 * <p>Il Displayname può non essere presente, in tal caso sarà rappresentato da una stringa vuota:
 * {@code ""}; Le parti Local e Domain dell'indirizzo non possono essere vuote, {@code null}, devono
 * rispettare la {@code regexp `` [\w\d.!$%&'*+/=?^_`{|}~-]+}} e devono essere formati da caratteri
 * ASCII.
 *
 * <p>L'uguaglianza e l'ordinamento tra {@code Address} sono basati solo sulle parti Local e Domain.
 */
public class Address implements Comparable<Address> {

  /*-
   * AF: Il Displayname, Local e Domain sono contenuti nei campi omonimi.
   * Per esempio dati:
   * <ul>
   *    <li> Displayname=Daniele Buondonno;
   *    <li> Local=danibond;
   *    <li> Domain=gmail.com;
   * </ul>
   * L'indirizzo risultante sarebbe: Daniele Buondonno <danibond@gmail.com>.
   *
   * RI:
   *    Displayname, Local, Domain != null;
   *    Local e Domain devono essere non vuoti e devono rispettare la regexp: `` [\w\d.!$%&'*+/=?^_`{|}~-]+}.
   *
   */

  /** Il display name dell'indirizzo email */
  private final String displayname;

  /** La parte locale dell'indirizzo email */
  private final String local;

  /** Il dominio dell'indirizzo email */
  private final String domain;

  /**
   * Metodo che costruisce un indirizzo, dato il Displayname, Local e Domain.
   *
   * <p>Più precisamente dati gli argomenti: "DaniBond", "danibond", "gmail.com" il metodo
   * costruisce l'indirizzo: DaniBond {@literal <} danibond@gmail.com {@literal >}.
   *
   * @param displayname il Displayname dell'indirizzo.
   * @param local il locale dell'indirizzo.
   * @param domain il dominio dell'indirizzo.
   * @throws IllegalArgumentException se Local o Domain sono {@code null}, contengono caratteri non
   *     ASCII oppure se non rispettano la {@code regexp `` [\w\d.!$%&'*+/=?^_`{|}~-]+}}.
   * @throws NullPointerException se il Displayname è {@code null}.
   */
  private Address(final String displayname, final String local, final String domain)
      throws IllegalArgumentException, NullPointerException {
    if (!AddressEncoding.isValidAddressPart(local)) {
      throw new IllegalArgumentException(
          "Il locale deve essere formattato in modo conforme agli indirizzi email");
    }
    if (!AddressEncoding.isValidAddressPart(domain)) {
      throw new IllegalArgumentException(
          "Il dominio deve essere formattato in modo conforme agli indirizzi email");
    }
    this.displayname = Objects.requireNonNull(displayname);
    this.local = Objects.requireNonNull(local);
    this.domain = Objects.requireNonNull(domain);
  }

  /**
   * Metodo che data una lista di stringhe, contenenti le tre parti di un indirizzo, restituisce
   * l'indirizzo formato dalle tre parti.
   *
   * <p>Più precisamente la lista deve contenere, nel seguente ordine: Displayname, Local e Domain.
   * Per esempio se la lista contiene:
   *
   * <ul>
   *   <li>Uno, o due;
   *   <li>unoodue;
   *   <li>mail.it;
   * </ul>
   *
   * <p>Il metodo restituisce un indirizzo con:
   *
   * <ul>
   *   <li>Displayname: "Uno, o due";
   *   <li>Local: unoodue;
   *   <li>Domain: mail.it;
   * </ul>
   *
   * <p>Se l'indirizzo non ha Displayname, la lista deve contenere una stringa vuota al suo posto.
   *
   * @param addressParts la lista di stringhe rappresentanti le parti dell'indirizzo.
   * @return L'indirizzo risultante dalla lista.
   * @throws IllegalArgumentException se la lista non è formata da tre parti.
   * @throws NullPointerException se la lista di stringhe è {@code null}
   */
  public static Address from(final List<String> addressParts)
      throws IllegalArgumentException, NullPointerException {
    if (Objects.requireNonNull(addressParts).size() != 3) {
      throw new IllegalArgumentException("Un indirizzo deve essere formato da tre parti");
    }

    String display = addressParts.get(0);
    String local = addressParts.get(1);
    String domain = addressParts.get(2);
    return new Address(display, local, domain);
  }

  /**
   * Metodo che restituisce un Indirizzo data una Stringa che lo rappresenta.
   *
   * <p>La stringa data deve essere formattata nel seguente modo: Displayname, racchiuso tra
   * virgolette se formato da più di due parole, local e dominio, racchiusi tra i simboli {@literal
   * <} e {@literal >} e separati l'un l'altro dal simbolo "@" le parti riguardanti il locale e il
   * dominio devono anche rispettare la {@code regexp `` [\w\d.!$%&'*+/=?^_`{|}~-]+}}.
   *
   * <p>Più precisamente dato l'indirizzo "Uno, o due" unoodue@mail.it: Il metodo restituisce un
   * indirizzo che ha:
   *
   * <ul>
   *   <li>Displayname: "Uno, o due";
   *   <li>Local: unoodue;
   *   <li>Domain: mail.it;
   * </ul>
   *
   * @param address La stringa che rappresenta l'indirizzo.
   * @return L'indirizzo estratto dalla stringa.
   * @throws IllegalArgumentException Se la stringa contiene caratteri non ASCII o se è {@code
   *     null}.
   */
  public static Address from(final String address) throws IllegalArgumentException {
    if (!ASCIICharSequence.isAscii(address))
      throw new IllegalArgumentException("L'email non può contenere caratteri non ASCII");

    List<String> parts = new ArrayList<>();
    parts = AddressEncoding.decode(ASCIICharSequence.of(address)).get(0);
    return new Address(parts.get(0), parts.get(1), parts.get(2));
  }

  /**
   * Metodo che restituisce il Displayname dell'indirizzo email.
   *
   * @return Il Displayname dell'indirizzo, se l'indirizzo non ha Displayname restituisce una
   *     stringa vuota:{@code ""}.
   */
  public String getDisplayname() {
    return displayname;
  }

  /**
   * Metodo che restituisce la parte locale dell'indirizzo email.
   *
   * @return La parte dell'indirizzo email corrispondente alla locale.
   */
  public String getLocal() {
    return local;
  }

  /**
   * Metodo che restituisce il dominio dell'indirizzo email.
   *
   * @return La parte dell'indirizzo email corrispondente al dominio.
   */
  public String getDomain() {
    return domain;
  }

  @Override
  public int compareTo(Address a) {
    int value = local.compareTo(a.getLocal());
    if (value == 0) {
      return domain.compareTo(a.getDomain());
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Address)) {
      return false;
    }
    return local.equals(((Address) obj).getLocal()) && domain.equals(((Address) obj).getDomain());
  }

  /**
   * Metodo che converte l'indirizzo in una {@link ASCIICharSequence} per permetterne il salvataggio
   * su disco.
   *
   * @return La {@code ASCIICharSequence} corrispondente all'indirizzo email.
   */
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public int hashCode() {
    int result = local.hashCode() * 31;
    result = domain.hashCode() * 31 + result;
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (!displayname.isEmpty()) {
      int count = 0;
      for (int i = 0; i < displayname.length(); i++) {
        if (displayname.charAt(i) == ' '
            && displayname.charAt(i + 1) != ' '
            && i != displayname.length() - 1) {
          count++;
        }
      }
      if (count >= 2) {
        sb.append("\"" + displayname + "\"");
      } else {
        sb.append(displayname);
      }
      sb.append(" <" + local + "@" + domain + ">");
    } else {
      sb.append(local + "@" + domain);
    }
    return sb.toString();
  }
}
