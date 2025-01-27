package mua;

import utils.ASCIICharSequence;

/**
 * Le intestazioni sono degli elementi dei messaggi, formate da un tipo ed un "valore", e vengono
 * utilizzate per specificare, o indicare alcuni dettagli dei messaggi.
 *
 * <p>Un esempio di intestazione è quello che indica il mittente di un messaggio: Questa
 * intestazione ha tipo "From" e ha come valore l'indirizzo del mittente.
 *
 * <p>Il tipo è unico e ogni intestazione ha il suo. Le classi che implementano questa interfaccia
 * devono restituire il loro tipo tramite il metodo getType().
 *
 * <p>Per esempio la classe che rappresenta l'intestazione per il mittente {@code FromHeader}, ha
 * come tipo "From", e lo restituisce tramite il metodo ereditato.
 *
 * <p>Ogni intestazione deve poter essere convertita in {@link ASCIICharSequence}, per permettere il
 * salvataggio su disco di un messaggio, che è formato da un insieme di intestazioni.
 *
 * <p>Ogni intestazione, essendo formata da un valore, deve poterlo restituire.
 *
 * <p>Ho deciso di optare per un metodo che restituisce una stringa per semplicità, ogni classe che
 * implementa questa interfaccia può dare dei getters che permettono di utilizzare i valori veri e
 * propri, ma deve almeno restituire il valore sotto forma di stringa.
 */
public interface Header {

  /**
   * AF: Il tipo di un'intestazione viene restituito dal metodo getType(), ogni tipo è unico per
   * ogni intestazione. Il valore di un'intestazione viene restituito dal metodo getValue(). Il suo
   * valore invece deve essere rappresentato dalle classi che implementano l'interfaccia, a seconda
   * dell'intestazione che vogliono rappresentare.
   */

  /**
   * Metodo che restituisce il tipo dell'intestazione.
   *
   * <p>Alcuni esempi sono "Date", "Subject", "From" e "To".
   *
   * @return il tipo dell'intestazione.
   */
  public String getType();

  /**
   * Metodo che converte in {@link ASCIICharSequence} l'intestazione, compresa di tipo e valore, per
   * permetterne il salvataggio su disco.
   *
   * @return la sequenza estratta dall'intestazione.
   */
  public ASCIICharSequence toSequence();

  /**
   * Metodo che restituisce il valore contenuto nell'intestazione sotto forma di stringa.
   *
   * <p>Ho deciso di restituire una stringa per mantenere le implementazioni semplici, e perchè ho
   * inteso l'utilizzo di questo metodo per scopi di stampa, ogni classe ovviamente ha dei getters
   * che permette ai Client o eventuali Utenti di utilizzare il contenuto vero e proprio delle
   * intestazioni.
   *
   * @return Una stringa rappresentante il valore contenuto nell'intestazione.
   */
  public String getValue();
}
