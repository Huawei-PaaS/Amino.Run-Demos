/*
 * Created on Apr 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

//import java.awt.*;


/**
 * @author dkappe
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChessVisualizationTrainer {

	public final static String VERSION = "Chess Trainer 1.2.4";

	public static OpeningDB book = null;

	public ChessVisualizationTrainer() {
		

		initBook();

		

		

		
	}

	public void initBook() {
		ClassLoader cl = this.getClass().getClassLoader();
		try {
			book =
				new OpeningDB(
					cl.getResource("com/imaginot/chess/engine/book.pgn").openStream());
		} catch (Exception e) {
			book = null;
		}
	}

	public void popupinfo() {
		
	}
	public static void main(String[] args) {
		// splash screen

	}
}
