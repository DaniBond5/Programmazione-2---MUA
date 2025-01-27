package mua;

import java.util.Objects;
import utils.ASCIICharSequence;
import utils.Base64Encoding;

/**
 * Un {@code SubjectHeader} è una intestazione {@link Header} che specifica l'oggetto di un
 * messaggio.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>Questa intestazione ha come valore l'oggetto del messaggio e come tipo "Subject".
 *
 * <p>L'ordinamento e l'uguaglianza sono basati sul campo subject, che rappresenta l'oggetto.
 */
public class SubjectHeader implements Header, Comparable<SubjectHeader> {

  /**
   * AF: l'oggetto è contenuto nel campo subject, il tipo viene restituito dal metodo getType
   * ereditato da {@link Header}.
   *
   * <p>Un esempio di oggetto può essere: "Iniziativa ergonomica bi-direzionale".
   *
   * <p>RI: subject!=null.
   */

  /** L'oggetto del messaggio */
  private final String subject;

  /**
   * Costruttore che, data l'oggetto del messaggio, crea una {@code SubjectHeader} che ha come tipo
   * "Subject" e come valore l'oggetto specificato.
   *
   * <p>L'oggetto contenuto nell'{@code SubjectHeader} è sempre decodificato.
   *
   * @param subject l'oggetto del messaggio.
   * @throws NullPointerException se l'oggetto è {@code null}.
   */
  private SubjectHeader(final String subject) throws NullPointerException {
    this.subject = Objects.requireNonNull(subject);
  }

  /**
   * Metodo che data una Stringa, che rappresenta l'oggetto di un messaggio, restituisce un {@code
   * SubjectHeader} che ha come valore l'oggetto specificato.
   *
   * <p>Se la Stringa ricevuta è codificata in UTF-8, viene restituito un {@code SubjectHeader} che
   * ha come oggetto la sua decodifica.
   *
   * @param subject l'oggetto, già decodificato o codificato in UTF-8.
   * @return Un {@code SubjectHeader} che ha come oggetto l'oggetto estratto dalla stringa.
   * @throws IllegalArgumentException se l'oggetto è vuoto.
   * @throws NullPointerException se l'oggetto è {@code null}.
   */
  public static SubjectHeader from(final String subject)
      throws IllegalArgumentException, NullPointerException {
    if (subject.isEmpty()) {
      throw new IllegalArgumentException("L'oggetto non può essere vuoto");
    }
    if (subject.startsWith("=?utf-8?B?"))
      return new SubjectHeader(Base64Encoding.decodeWord(ASCIICharSequence.of(subject)));
    return new SubjectHeader(subject);
  }

  /**
   * Metodo che data una sequenza {@link ASCIICharSequence}, restituisce un {@code SubjectHeader}
   * che ha come valore l'oggetto estratto dalla sequenza.
   *
   * @param sequence la sequenza.
   * @return Un {@code SubjectHeader} che ha come oggetto l'oggetto estratto dalla sequenza.
   * @throws IllegalArgumentException se la sequenza è vuota.
   * @throws NullPointerException se la sequenza è {@code null}.
   */
  public static SubjectHeader from(final ASCIICharSequence sequence)
      throws IllegalArgumentException, NullPointerException {
    if (Objects.requireNonNull(sequence).isEmpty()) {
      throw new IllegalArgumentException("L'oggetto non può essere vuoto");
    }
    if (sequence.toString().startsWith("=?utf-8?B?")) {
      return new SubjectHeader(Base64Encoding.decodeWord(sequence));
    }
    return new SubjectHeader(sequence.toString());
  }

  /**
   * Metodo che restituisce l'oggetto del messaggio.
   *
   * <p>Se l'oggetto contiene caratteri non ASCII, viene restituita una codifica in UTF-8
   * dell'oggetto.
   *
   * @return L'oggetto, o una sua codifica in UTF-8 se contiene caratteri non ASCII.
   */
  public String getSubject() {
    if (!ASCIICharSequence.isAscii(subject)) {
      return Base64Encoding.encodeWord(subject).toString();
    }
    return subject;
  }

  @Override
  public String getValue() {
    return subject;
  }

  @Override
  public String getType() {
    return "Subject";
  }

  @Override
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public int compareTo(SubjectHeader s) {
    return subject.compareTo(s.getValue());
  }

  @Override
  public int hashCode() {
    return subject.hashCode() * 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof SubjectHeader)) return false;
    return subject.equals(((SubjectHeader) obj).getSubject());
  }

  @Override
  public String toString() {
    return "Subject: " + getSubject();
  }
}
