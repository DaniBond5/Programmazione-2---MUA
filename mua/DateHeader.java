package mua;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.DateEncoding;

/**
 * Un {@code DateHeader} è una intestazione {@link Header} che specifica la data di un messaggio.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>Più precisamente questa intestazione ha come tipo "Date" e come valore la data del messaggio.
 *
 * <p>L'ordinamento e l'uguaglianza si basano sulla data.
 */
public class DateHeader implements Header, Comparable<DateHeader> {

  /**
   * AF: La data è contenuta nel campo date, e il tipo è restituito dal metodo getType ereditato da
   * {@link Header}. Per esempio avendo la data: Thu, 3 Dec 2020 00:00:00 +0100, il campo date la
   * conterrà.
   *
   * <p>RI: date!=null.
   */

  /** La data del messaggio */
  private final ZonedDateTime date;

  /**
   * Costruttore che, data una data in {@link ZonedDateTime} costruisce una {@code DateHeader} che
   * ha come valore la data.
   *
   * @param date la data.
   * @throws NullPointerException se la data è {@code null}.
   */
  private DateHeader(final ZonedDateTime date) throws NullPointerException {
    this.date = Objects.requireNonNull(date);
  }

  /**
   * Metodo che data una sequenza {@link ASCIICharSequence}, contenente una data, restituisce un
   * {@code DateHeader} che ha come valore la data estratta dalla sequenza.
   *
   * @param sequence la sequenza.
   * @return Un {@code DateHeader} che ha come valore la data estratta.
   * @throws IllegalArgumentException se dalla sequenza non è possibile estrarre una data valida.
   * @throws NullPointerException se la sequenza è {@code null}.
   */
  public static DateHeader from(final ASCIICharSequence sequence)
      throws IllegalArgumentException, NullPointerException {
    ZonedDateTime date;
    try {
      date = DateEncoding.decode(Objects.requireNonNull(sequence));
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("La sequenza data non contiene una data valida");
    }
    return new DateHeader(date);
  }

  /**
   * Metodo che data una data {@link ZonedDateTime}, restituisce un {@code DateHeader} che ha essa
   * come valore.
   *
   * @param date la data.
   * @return Un {@code DateHeader} avente come valore la data.
   * @throws NullPointerException se la data è {@code null}.
   */
  public static DateHeader from(final ZonedDateTime date) throws NullPointerException {
    return new DateHeader(Objects.requireNonNull(date));
  }

  /**
   * Metodo che, data una Stringa che rappresenta la data del messaggio, restituisce un {@code
   * DateHeader} che ha come valore la data estratta da essa.
   *
   * <p>Più precisamente se il metodo riceve una Stringa: Thu, 3 Dec 2020 00:00:00 +0100 allora il
   * metodo restituisce un {@code DateHeader} che ha la data specificata.
   *
   * @param dateString la data.
   * @return Un {@code DateHeader} che ha come valore la data estratta.
   * @throws IllegalArgumentException se la data è vuota o contiene caratteri non ASCII oppure se
   *     non contiene una data valida.
   * @throws NullPointerException se la data è {@code null}.
   */
  public static DateHeader from(final String dateString)
      throws IllegalArgumentException, NullPointerException {
    if (!ASCIICharSequence.isAscii(Objects.requireNonNull(dateString)) || dateString.isEmpty()) {
      throw new IllegalArgumentException(
          "La data deve essere formata da caratteri ASCII e non deve essere vuota");
    }
    ZonedDateTime date;
    try {
      date = DateEncoding.decode(ASCIICharSequence.of(Objects.requireNonNull(dateString)));
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("La sequenza deve contenere una data");
    }
    return new DateHeader(date);
  }

  /**
   * Metodo che restituisce la data contenuta nell'intestazione.
   *
   * @return la data.
   */
  public ZonedDateTime getDate() {
    return date;
  }

  @Override
  public String getType() {
    return "Date";
  }

  @Override
  public String getValue() {
    return date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  @Override
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public int compareTo(DateHeader d) {
    return date.compareTo(d.getDate());
  }

  @Override
  public int hashCode() {
    return date.hashCode() * 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DateHeader)) {
      return false;
    }
    return date.equals(((DateHeader) obj).getDate());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Date: " + date.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    return sb.toString();
  }
}
