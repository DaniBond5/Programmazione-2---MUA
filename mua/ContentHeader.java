package mua;

import java.util.Objects;
import utils.ASCIICharSequence;

/**
 * Un {@code ContentHeader} è una intestazione {@link Header} che specifica come codificare il
 * Content-Type e il Charset del corpo di un messaggio. Content-Type e Charset indicano il tipo del
 * contenuto del corpo e la sua codifica.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>I Content-Type possibili sono: "multipart/alternative", "text/plain", "text/html". Mentre i
 * Charset possibili sono: "us-ascii" e "uff-8". Se il Content-Type è "multiparts/alternative",
 * l'intestazione specifica un valore vuoto ("") come Charset.
 *
 * <p>L'uguaglianza si basa sul Content-Type e Charset specificati.
 */
public class ContentHeader implements Header {

  /**
   * AF: Il Content-Type è contenuto nel campo contentType, mentre il Charset nel campo charset. I
   * Content-Type possibili sono: "multipart/alternative", "text/plain", "text/html". Mentre i
   * Charset possibili sono: "us-ascii" e "uff-8". Se il Content-Type è "multiparts/alternative",
   * l'intestazione specifica un valore vuoto ("") come Charset.
   *
   * <p>RI: contentType deve essere uguale ad uno tra: "multipart/alternative", "text/plain",
   * "text/html". charset deve essere uguale ad uno tra: "us-ascii", "utf-8". se il contentType è
   * "multiparts/alternative", allora il charset deve essere una stringa vuota "".
   */

  /** Il Content-Type del corpo. */
  private String contentType;

  /** Il charset del corpo. */
  private String charset;

  /**
   * Costruttore che dati Content-Type e Charset crea un {@code ContentHeader} che specifica tali
   * come proprio Content-Type e Charset.
   *
   * @param content il Content-Type.
   * @param chars il Charset.
   * @throws NullPointerException se charset o content-type sono {@code null}.
   */
  private ContentHeader(String content, String chars) throws NullPointerException {
    contentType = Objects.requireNonNull(content);
    charset = Objects.requireNonNull(chars);
  }

  /**
   * Metodo che data una stringa rappresentante il content type e una stringa rappresentante il
   * charset di un corpo, restituisce un {@code ContentHeader} che specifica tali Content-Type e
   * Charset.
   *
   * <p>Il Content-Type dato può essere uno tra: "multipart/alternative", "text/plain", "text/html".
   *
   * <p>Il Charset dato può essere uno tra: "us-ascii", "utf-8" e "" nel caso il Content-Type sia
   * "multipart/alternative".
   *
   * <p>Se il Content-Type è "multipart/alternative", il Charset viene rappresentato da una stringa
   * vuota.
   *
   * @param content il Content-Type.
   * @param chars il Charset.
   * @return un {@code ContentHeader} che specifica come content-type e charset quelli dati.
   * @throws IllegalArgumentException se il Content-Type o il Charset dati non corrispondono ad uno
   *     di quelli ammessi.
   * @throws NullPointerException se Content-Type o Charset dati sono {@code null}.
   */
  public static ContentHeader from(String content, String chars)
      throws IllegalArgumentException, NullPointerException {
    if (Objects.requireNonNull(content).isEmpty()
        || (!content.equals("multipart/alternative")
            && !content.equals("text/plain")
            && !content.equals("text/html"))) {
      throw new IllegalArgumentException("Il content type dato non è valido");
    }
    if (!Objects.requireNonNull(chars).equals("utf-8")
        && !chars.equals("us-ascii")
        && !(content.equals("multipart/alternative") && chars.isEmpty())) {
      throw new IllegalArgumentException("Il charset dato non è valido");
    }
    return new ContentHeader(content, chars);
  }

  /**
   * Metodo che restituisce il Content-Type.
   *
   * @return il Content-Type specificato dall'intestazione.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Metodo che restituisce il Charset.
   *
   * @return il Charset specificato dall'intestazione.
   */
  public String getCharset() {
    return charset;
  }

  @Override
  public String getType() {
    return "Content-Type";
  }

  @Override
  public String getValue() {
    if (contentType.equals("multipart/alternative")) {
      return contentType;
    }
    return contentType + " " + charset;
  }

  @Override
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  @Override
  public int hashCode() {
    int result = contentType.hashCode() * 31;
    result = charset.hashCode() * 31 + result;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ContentHeader)) {
      return false;
    }
    return contentType.equals(((ContentHeader) obj).getContentType())
        && charset.equals(((ContentHeader) obj).getCharset());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (contentType == "multipart/alternative") {
      sb.append("MIME-Version: 1.0\n");
      sb.append("Content-Type: " + contentType + "; " + "boundary=frontier");
      return sb.toString();
    } else {
      sb.append("Content-Type: " + contentType + "; " + "charset=\"" + charset + "\"");
      if (charset != "us-ascii") {
        sb.append("\nContent-Transfer-Encoding: base64");
      }
      return sb.toString();
    }
  }
}
