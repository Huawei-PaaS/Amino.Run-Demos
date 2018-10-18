/*
 * Created on May 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import chesspresso.move.*;
import chesspresso.position.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OpeningEntry {
	private String fen;
	private List moves;
	private Set moveText;
	public OpeningEntry(Position p) {
		fen = FEN.getFEN(p);
		moves = new ArrayList();
		moveText = new HashSet();
	}

	public void addMove(Move m) {
		moves.add(new Short(m.getShortMoveDesc()));
		moveText.add(Move.getString(m.getShortMoveDesc()));
	}
	public List getMoves() {
		return moves;
	}

	public List getMoveTexts() {
		return new ArrayList(moveText);
	}

	public String getFen() {
		return fen;
	}

	public boolean validMove(String move) {
		String m = parseMove(move);
		if (m == null) {
			return false;
		} else {
			return true;
		}
	}

	public String parseMove(String move) {
		move = move.trim();
		String alt1, alt2;
		if ((move.length() < 4) || move.startsWith("O")) {
			alt1 = move;
			alt2 = move;
		} else {
			alt1 = move.substring(0, 2) + "-" + move.substring(2);
			alt2 = move.substring(0, 2) + "x" + move.substring(2);
		}
		Iterator it = moveText.iterator();
		while (it.hasNext()) {
			String temp = (String) it.next();
			if (temp.equalsIgnoreCase(move)
				|| temp.equalsIgnoreCase(alt1)
				|| temp.equalsIgnoreCase(alt2)) {
				return temp;
			}
		}
		return null;
	}
}