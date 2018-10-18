/*
 * Created on Apr 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chesspresso.game.*;
import chesspresso.move.*;
import chesspresso.pgn.*;
import chesspresso.position.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OpeningDB {
	private PGNReader pgn;
	private Map db = new HashMap();
	private List entries = new ArrayList();
	private int nextEntry = 0;
	
	/**
	 * 
	 */
	public OpeningDB(InputStream is) {
		pgn = new PGNReader(is, "dummy");
		fillDb();
	}
	
	private void fillDb() {
		Game g;
		boolean done = false;
		int count = 0;
		while (!done) {
			try {
				g = pgn.parseGame();
				if (g == null) {
					//System.err.println("Done after " + count);
					done = true;
					continue;
				}
			} catch (Exception e) {
				//System.err.println("Error " + e);
				//e.printStackTrace(System.err);
				done = true;
				continue;
			}
			count++;
			process(g);
		}
	}

	private void process(Game g) {
		g.goBackToMainLine();
		g.goBackToLineBegin();
		
		Move[] moves = g.getMainLine();
		//System.err.println("Number of moves in main line = " + moves.length);
		Position p = Position.createInitialPosition();
		Long hash;
		OpeningEntry s;
		for (int i = 0; i < moves.length; i++) {
			if (i > 23) {
				break; // no more than 12 moves
			}
			hash = new Long(p.getHashCode());
			if (db.containsKey(hash)) {
				s = (OpeningEntry)db.get(hash);
			} else {
				s = new OpeningEntry(p);
				db.put(hash,s);
				entries.add(s);
				//System.err.println("Adding position.");
			}
			s.addMove(moves[i]);
			
			try {
				p.doMove(moves[i]);
			} catch (Exception e) {
				//System.err.println("Error " + e);
				//e.printStackTrace(System.err);
				break; // skip the rest if there is an error in the game
			}
		}
	}
	
	public Short getMove(Position p) {
		OpeningEntry oe = (OpeningEntry)db.get(new Long(p.getHashCode()));
		if (oe == null) {
			return null;
		}
		List moves = oe.getMoves();
		
		int size = moves.size();
		
		if (size < 1) {
			return null;
		}
		int choice = (new Long(Math.round(Math.random() * 11111003)%size)).intValue();
		return (Short)(moves.toArray())[choice];
	}
	
	public OpeningEntry getRandomEntry() {
		Set keys = db.keySet();
		int size = keys.size();
		int choice = (new Long(Math.round(Math.random() * 11111003)%size)).intValue();
		Long hash = (Long)keys.toArray()[choice];
		OpeningEntry oe = (OpeningEntry)db.get(hash);
		return oe;
	}
	
	public OpeningEntry getNextEntry() {
		int size = entries.size();

		OpeningEntry oe = (OpeningEntry)entries.get(nextEntry);
		nextEntry = (nextEntry + 1)%size;
		return oe;
	}
	
	public void dump() {
		System.out.println("Number of positions: " + db.size());
	}
	
	public static void main(String args[]) {
		try {
			InputStream in = new FileInputStream("C:/tmp/eco/book.pgn");
			OpeningDB odb = new OpeningDB(in);
			odb.dump();
		} catch (Exception e) {
			System.err.println("Error " + e);
			e.printStackTrace(System.err);
		}
	}
}
