package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.AddressEncoding;
import utils.Base64Encoding;
import utils.EntryEncoding;
import utils.Fragment;

/**
 * Classe che rappresenta un messaggio email.
 *
 * <p>Gli oggetti di questa classe sono immutabili.
 *
 * <p>Ogni messaggio è formato da un insieme di intestazioni {@link Header}, che ne specificano
 * alcuni dettagli, e da un insieme di parti {@code Part}, che a loro volta sono composti da un
 * corpo e da un insieme di intestazioni {@link Header} che ne specificano i dettagli.
 *
 * <p>L'insieme di intestazioni del messaggio sono intestazioni che specificano dettagli su di esso,
 * alcuni esempi sono le intestazioni "To", "From", "Subject" e "Date" e altre possibili
 * intestazioni.
 *
 * <p>Ogni messaggio deve almeno contenere le intestazioni principali che devono essere sempre
 * presenti "To", "From", "Subject" e "Date".
 *
 * <p>Le parti del messaggio invece, hanno un loro insieme di intestazioni, che riguardano il loro
 * corpo, un esempio di tale intestazione è "Content-Type". Oltre all'insieme di intestazioni sono
 * formate anche dal loro corpo.
 *
 * <p>L'uguaglianza si basa sull'insieme di intestazioni e sull'insieme di parti del messaggio.
 *
 * <p>Per quanto riguarda le implementazioni dei toString, non sarebbero sufficienti se un utente
 * aggiungesse altre intestazioni, ma ho deciso di mantenerle tali per perseguire gli scopi del
 * progetto e per mantenerli semplici.
 */
public class Message {

  /**
   * AF: Le intestazioni riguardanti il messaggio sono contenute in headers, e le parti del
   * messaggio sono contenute in parts.
   *
   * <p>RI: headers!=null && containsEssentialHeaders(headers)==true Per ogni elemento in parts,
   * RI_Part()==true.
   */

  /** L'insieme delle intestazioni riguardanti il messaggio. */
  private final Map<String, Header> headers;

  /** L'insieme delle parti del messaggio. */
  private final List<Part> parts;

  /**
   * Inner Class statica atta a rappresentare le parti di un messaggio.
   *
   * <p>Gli oggetti di questa classe sono immutabili.
   *
   * <p>Una parte di un messaggio contiene un corpo, e un insieme di intestazioni riguardanti il
   * corpo del messaggio. Questo insieme di intestazioni deve contenere almeno l'intestazione
   * "Content-Type", utilizzata per indicare la codifica del corpo.
   *
   * <p>L'uguaglianza tra parti si basa sull'insieme di intestazioni e sul corpo.
   */
  static class Part {

    /**
     * AF: L'insieme delle intestazioni è contenuto nel campo partHeaders, e il corpo della parte è
     * contenuto in body.
     *
     * <p>RI: partHeaders!=null; partHeaders.containsKey("Content-Type")==true (cioè nell'insieme di
     * intestazioni deve esserci l'intestazione "Content-Type"); partHeaders.isEmpty()==false;
     */

    /** L'insieme delle intestazioni riguardanti la parte. */
    private final Map<String, Header> partHeaders;

    /** Il corpo della parte. */
    private final String body;

    /**
     * Metodo che, data una lista di intestazioni, e un corpo, crea una parte avente come
     * intestazioni quelle contenute nella lista e come corpo quello dato.
     *
     * <p>Nella lista ci deve essere almeno un'intestazione di tipo "Content-Type".
     *
     * @param partHeaders la lista di intestazioni della parte.
     * @param body il corpo della parte.
     * @throws IllegalArgumentException se la lista è vuota o non contiene l'intestazione
     *     "Content-Type".
     * @throws NullPointerException se la lista data o il corpo dato sono {@code null}.
     */
    private Part(List<Header> partHeaders, String body)
        throws IllegalArgumentException, NullPointerException {
      if (Objects.requireNonNull(partHeaders).isEmpty())
        throw new IllegalArgumentException("Le parti devono contenere delle intestazioni");

      HashMap<String, Header> headers = new HashMap<>();
      for (Header h : partHeaders) {
        headers.put(h.getType(), h);
      }
      if (!headers.containsKey("Content-Type") || headers.isEmpty())
        throw new IllegalArgumentException(
            "L'intestazione Content-Type deve essere presente in ogni parte");

      this.partHeaders = headers;
      this.body = Objects.requireNonNull(body);
    }

    /**
     * Metodo che restituisce una mappa non modificabile rappresentante l'insieme delle intestazioni
     * della parte.
     *
     * @return una mappa non modificabile contenente le intestazioni presenti nella parte.
     */
    public Map<String, Header> getPartHeaders() {
      return Collections.unmodifiableMap(partHeaders);
    }

    /**
     * Metodo che restituisce l'intestazione {@code ContentHeader} della parte.
     *
     * @return l'intestazione {@code ContentHeader} della parte.
     * @throws IllegalStateException se l'intestazione non è presente tra le intestazioni della
     *     parte.
     */
    public ContentHeader getContentHeader() throws IllegalStateException {
      if (!partHeaders.containsKey("Content-Type"))
        throw new IllegalStateException(
            "L'intestazione essenziale per le parti: Content-Type non è presente");
      return (ContentHeader) partHeaders.get("Content-Type");
    }

    /**
     * Metodo che restituisce il corpo della parte.
     *
     * @return il corpo della parte.
     */
    public String getBody() {
      return body;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(partHeaders.get("Content-Type") + "\n\n");
      if (!ASCIICharSequence.isAscii(body)
          || getContentHeader().getContentType().equals("text/html")) {
        sb.append(Base64Encoding.encode(body));
      } else {
        sb.append(body);
      }
      return sb.toString();
    }

    @Override
    public int hashCode() {
      int result = partHeaders.hashCode() * 31;
      result = body.hashCode() * 31 + result;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (!(obj instanceof Part)) return false;
      return partHeaders.equals(((Part) obj).getPartHeaders())
          && body.equals(((Part) obj).getBody());
    }
  }

  /**
   * Costruttore che, data una mappa rappresentante l'insieme di intestazioni riguardanti il
   * messaggio, e una lista di parti, costruisce un messaggio formato dall'insieme di intestazioni e
   * dall'insieme di parti dato.
   *
   * @param headers l'insieme delle intestazioni del messaggio.
   * @param parts l'insieme delle parti del messaggio.
   * @throws IllegalArgumentException se le intestazioni necessarie per il messaggio o per le parti
   *     non sono presenti.
   * @throws NullPointerException se la mappa di intestazioni, o la lista di parti è {@code null}.
   */
  private Message(Map<String, Header> headers, List<Part> parts)
      throws IllegalArgumentException, NullPointerException {
    if (headers.isEmpty() || !Message.containsEssentialHeaders(headers)) {
      throw new IllegalArgumentException(
          "In un messaggio devono essere presenti le intestazioni principali Date, To, From e Subject.");
    }
    this.headers = Objects.requireNonNull(headers);
    for (Part part : parts) {
      if (!part.getPartHeaders().containsKey("Content-Type")) {
        throw new IllegalArgumentException("Le parti devono avere l'intestazione Content-Type.");
      }
    }
    this.parts = Objects.requireNonNull(parts);
  }

  /**
   * Metodo che, data una sequenza {@link ASCIICharSequence} restituisce un messaggio estratto da
   * essa.
   *
   * <p>Questo metodo è utile quando si vuole estrarre un messaggio da una {@code Storage.Box} su
   * disco.
   *
   * @param sequence la sequenza da cui estrarre il messaggio.
   * @return il messaggio estratto dalla sequenza.
   * @throws NullPointerException se la sequenza data è {@code null}.
   */
  public static Message from(ASCIICharSequence sequence) throws NullPointerException {
    HashMap<String, Header> headers = new HashMap<>();
    ArrayList<Part> parts = new ArrayList<>();

    List<Fragment> fragments = EntryEncoding.decode(Objects.requireNonNull(sequence));
    for (Fragment fragment : fragments) {
      String body = fragment.rawBody().toString();

      ArrayList<Header> partHeaders = new ArrayList<>();
      for (List<ASCIICharSequence> rawHeader : fragment.rawHeaders()) {
        switch (rawHeader.get(0).toString()) {
          case "from":
            headers.put("From", FromHeader.from(rawHeader.get(1).toString()));
            break;

          case "to":
            headers.put("To", RecipientHeader.from(AddressEncoding.decode(rawHeader.get(1))));
            break;

          case "subject":
            headers.put("Subject", SubjectHeader.from(rawHeader.get(1)));
            break;

          case "date":
            headers.put("Date", DateHeader.from(rawHeader.get(1)));
            break;

          case "content-type":
            String[] elements = rawHeader.get(1).toString().split(";");
            String contentType = elements[0];

            if (contentType.equals("multipart/alternative")) {
              partHeaders.add(ContentHeader.from(contentType, ""));
              break;
            } else {
              String charset = "";
              if (elements[1].contains("utf-8")) {
                charset = "utf-8";
                body = Base64Encoding.decode(fragment.rawBody());
              } else {
                charset = "us-ascii";
              }
              partHeaders.add(ContentHeader.from(contentType, charset));
            }
            break;

          default:
            continue;
        }
      }
      parts.add(new Part(partHeaders, body));
    }
    return new Message(headers, parts);
  }

  /**
   * Metodo che, data una lista di intestazioni del messaggio, una lista contenente un insieme di
   * intestazioni per ogni parte, e una lista di corpi, restituisce il messaggio formato da esse.
   *
   * <p>Questo metodo viene utilizzato quando viene creato un nuovo messaggio (in fase di compose).
   *
   * @param messageHeaders l'insieme di intestazioni riguardanti il messaggio.
   * @param partsHeaders la lista contenente i vari insiemi di intestazioni per ogni parte del
   *     messaggio.
   * @param bodies una lista contenente i corpi delle varie parti del messaggio.
   * @return il messaggio composto da tali insiemi.
   * @throws IllegalArgumentException se le intestazioni principali per il messaggio e le parti non
   *     sono presenti.
   * @throws NullPointerException se la lista di intestazioni del messaggio, la lista di
   *     intestazioni delle parti o la lista dei corpi è {@code null}.
   */
  public static Message from(
      List<Header> messageHeaders, List<List<Header>> partsHeaders, List<String> bodies)
      throws IllegalArgumentException, NullPointerException {
    HashMap<String, Header> headers = new HashMap<>();
    for (Header h : Objects.requireNonNull(messageHeaders)) {
      headers.put(h.getType(), h);
    }
    if (!containsEssentialHeaders(headers) || messageHeaders.isEmpty()) {
      throw new IllegalArgumentException(
          "I messaggi devono contenere le intestazioni essenziali \"From, To, Subject e Date\"");
    }

    ArrayList<Part> parts = new ArrayList<>();
    String body1 = bodies.get(0);
    String body2 = bodies.get(1);
    if (body2.isEmpty()) {
      parts.add(new Part(partsHeaders.get(0), body1));
    } else {
      if (!body1.isEmpty())
        parts.add(
            new Part(partsHeaders.get(0), "This is a message with multiple parts in MIME format."));
      if (!body1.isEmpty()) {
        parts.add(new Part(partsHeaders.get(1), body1));
      }
      parts.add(new Part(partsHeaders.get(2), body2));
    }
    return new Message(headers, parts);
  }

  /**
   * Metodo che restituisce una lista di {@code Fragment} rappresentanti vari frammenti di un
   * messaggio, cioè le sue intestazioni indicandone valori e tipi, le sue parti con le loro
   * intestazioni, e con il corpo delle varie parti.
   *
   * @return una lista di frammenti del messaggio.
   */
  public List<Fragment> getFragments() {
    return EntryEncoding.decode(toSequence());
  }

  /**
   * Metodo che controlla se nella mappa rappresentante le intestazioni riguardanti il messaggio
   * sono contenute le intestazioni principali "From", "To", "Subject" e "Date".
   *
   * @param headers la mappa rappresentante un insieme di intestazioni riguardanti un messaggio.
   * @return {@code true} se tutte le intestazioni principali sono presenti, {@code false}
   *     altrimenti.
   */
  private static boolean containsEssentialHeaders(Map<String, Header> headers) {
    return headers.containsKey("From")
        && headers.containsKey("To")
        && headers.containsKey("Subject")
        && headers.containsKey("Date");
  }

  /**
   * Metodo che converte il messaggio in una sequenza {@link ASCIICharSequence} per permetterne il
   * salvataggio su disco.
   *
   * @return la sequenza {@link ASCIICharSequence} rappresentante il messaggio.
   */
  public ASCIICharSequence toSequence() {
    return ASCIICharSequence.of(toString());
  }

  /**
   * Metodo che restituisce l'intestazione di tipo "From" {@link FromHeader} del messaggio.
   *
   * @return l'intestazione "From" del messaggio.
   * @throws IllegalStateException se l'intestazione "From" non è presente.
   */
  public FromHeader getFromHeader() throws IllegalStateException {
    if (!headers.containsKey("From"))
      throw new IllegalStateException("L'intestazione essenziale From non è presente");
    return (FromHeader) headers.get("From");
  }

  /**
   * Metodo che restituisce l'intestazione di tipo "To" {@link RecipientHeader} del messaggio.
   *
   * @return l'intestazione "To" del messaggio.
   * @throws IllegalStateException se l'intestazione "To" non è presente.
   */
  public RecipientHeader getToHeader() throws IllegalStateException {
    if (!headers.containsKey("To"))
      throw new IllegalStateException("L'intestazione essenziale To non è presente");
    return (RecipientHeader) headers.get("To");
  }

  /**
   * Metodo che restituisce l'intestazione di tipo "Subject" {@link SubjectHeader} del messaggio.
   *
   * @return l'intestazione "Subject" del messaggio.
   * @throws IllegalStateException se l'intestazione "Subject" non è presente.
   */
  public SubjectHeader getSubjectHeader() throws IllegalStateException {
    if (!headers.containsKey("Subject"))
      throw new IllegalStateException("L'intestazione essenziale Subject non è presente");
    return (SubjectHeader) headers.get("Subject");
  }

  /**
   * Metodo che restituisce l'intestazione di tipo "Date" {@link DateHeader} del messaggio.
   *
   * @return l'intestazione "Date" del messaggio.
   * @throws IllegalStateException se l'intestazione "Date" non è presente.
   */
  public DateHeader getDateHeader() throws IllegalStateException {
    if (!headers.containsKey("Date"))
      throw new IllegalStateException("L'intestazione essenziale Date non è presente");
    return (DateHeader) headers.get("Date");
  }

  /**
   * Metodo che restituisce una mappa non modificabile rappresentante l'insieme delle intestazioni
   * riguardanti il messaggio.
   *
   * @return una mappa non modificabile contenente le intestazioni del messaggio.
   */
  public Map<String, Header> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }

  /**
   * Metodo che restituice una vista non modificabile della lista di parti del messaggio.
   *
   * @return una vista non modificabile della lista di parti del messaggio.
   */
  public List<Part> getParts() {
    return Collections.unmodifiableList(parts);
  }

  @Override
  public int hashCode() {
    int result = headers.hashCode() * 31;
    result = parts.hashCode() * 31 + result;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof Message)) return false;
    Message m = (Message) obj;
    return headers.equals(m.getHeaders()) && parts.equals(m.getParts());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getFromHeader() + "\n");
    sb.append(getToHeader() + "\n");
    sb.append(getSubjectHeader() + "\n");
    sb.append(getDateHeader() + "\n");
    if (parts.size() == 1) {
      sb.append(parts.get(0).toString());
    } else {
      for (int i = 0; i < parts.size(); i++) {
        if (i == parts.size() - 1) {
          sb.append(parts.get(i).toString() + "\n--frontier--");
        } else {
          sb.append(parts.get(i).toString() + "\n--frontier\n");
        }
      }
    }
    return sb.toString();
  }
}
