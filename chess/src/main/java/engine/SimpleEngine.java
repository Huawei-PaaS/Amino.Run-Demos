/*
 * Created on Apr 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import chesspresso.*;
import chesspresso.move.*;
import chesspresso.position.*;
import sapphire.app.SapphireObject;
import sapphire.policy.mobility.explicitmigration.ExplicitMigrationPolicy;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleEngine implements ChessEngine, SapphireObject<ExplicitMigrationPolicy> {
	private Position position;
	private boolean computerIsWhite;
	private int ply;

	// Moved the following members to private as Sapphire Object does not allow to have public member variables
	//public final static int MATE = 50000;
	//public final static int INF = 100000;
	private final static int MATE = 50000;
	private final static int INF = 100000;
	private long nodes;
	private Map hashTable;
	private int bscore;
	private OpeningDB book;
	private short killers[];
	private static int cnt = 0;

	public synchronized String lastMove() {
		return Move.getString(position.getLastShortMove());
	}
	public SimpleEngine() {
System.out.println("Inside the Simple Engine");

		killers = new short[200];
//		reset();
		position = Position.createInitialPosition();
		ply = 0;
		// load the book
	book = ChessVisualizationTrainer.book;

	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#reset()
	 */
	public synchronized void reset() {
	//	position = Position.createInitialPosition();
		ply = 0;
	}

	public void migrateObject(InetSocketAddress serverInfo) throws Exception {
		System.out.println("Inside the migrateObject of app directly with count as " + cnt++);
		System.out.println("Object has been migrated to kernal server with  "+serverInfo.getHostName()+" : "+serverInfo.getPort());
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#go()
	 */
	public synchronized String go() {
		System.out.println("---***---***PROCESSING BACKEND OF AUTOMATED MOVE - " + cnt++ + "***---***---");
		//System.out.println("Inside the go() of SimpleEngine");
		if (isDraw()) {
			return "DRAW";
		} else if (isMate()) {
			return "CHECKMATE";
		} else {
			// check the opening book first
			Short mv = null;
			if (book != null) {
				mv = book.getMove(position);
			}
			if (mv != null) {
				short move = mv.shortValue();
				try {
					position.doMove(move);
				} catch (Exception e) {
					return "ERROR: " + e;
				}
				ply++;
				return Move.getString(move) + " (book)";
			} else {
				// use this to reward certain positional characteristics for the
				// computer
				computerIsWhite = (position.getToPlay() == Chess.WHITE);
				//System.err.println("Computer white = " + computerIsWhite);
				for (int i = 0; i < killers.length; i++) {
					killers[i] = Move.NO_MOVE;
				}
				short move = bestMove();
				try {
					position.doMove(move);
				} catch (Exception e) {
					return "ERROR: " + e;
				}
				ply++;
				if ((position.getToPlay() == Chess.WHITE)) {
					// so it means it was blacks turn to play before
					// so negate the score
					bscore = -bscore;
				}
				String retval = Move.getString(move);
				if (isDraw()) {
					retval = retval + " DRAW";
				} else if (isMate()) {
					retval = retval + " MATE";
				}
				return retval
						+ " ("
						+ nodes
						+ " nodes, eval="
						+ bscore
						+ ")";
			}
		}
	}

	private void sortMoves(short a[]) {
		if ((a == null) || (a.length < 2)) {
			return;
		}

		// insertion sort
		int i, j, e;
		short v;
		// first, evaluate all the positions with a shallow search
		int eval[] = new int[a.length];

		for (i = 1; i < a.length; i++) {
			try {
				position.doMove(a[i]);
			} catch (Exception ignore) {
				continue;
			}
			eval[i] = -AlphaBeta(4, -INF, INF, 4);
			position.undoMove();
		}
		/* Initially, the first item is considered 'sorted' */
		/* i divides a into a sorted region, x<i, and an
		   unsorted one, x >= i */
		for (i = 1; i < a.length; i++) {
			/* Select the item at the beginning of the
			   as yet unsorted section */
			v = a[i];
			e = eval[i];
			/* Work backwards through the array, finding where v
			   should go */
			j = i;

			/* If this element is greater than v,
				  move it up one */
			while (e > eval[j - 1]) {
				eval[j] = eval[j - 1];
				a[j] = a[j - 1];
				j = j - 1;
				if (j <= 0)
					break;
			}
			/* Stopped when a[j-1] better than v, so put v at position j */
			a[j] = v;
			eval[j] = e;
		}
	}

	private void sortMoves2(short a[], int depth) {
		final int[] pval = { 0, 3, 3, 5, 9, 1, 11 }; // piece vals
		if ((a == null) || (a.length < 2)) {
			return;
		}

		// insertion sort
		int i, j, e;
		short v;
		// first, evaluate all the positions with a shallow search
		int eval[] = new int[a.length];

		for (i = 1; i < a.length; i++) {
			if (a[i] == killers[depth]) {
				eval[i] = INF;
				continue;
			}
			/*try {
				position.doMove(a[i]);
			} catch (Exception ignore) {
				continue;
			}
			eval[i] = -qsearch(-INF, INF);
			position.undoMove();*/

			if (Move.isCapturing(a[i])) {
				int victim1 = position.getPiece(Move.getToSqi(a[i]));
				int att1 = position.getPiece(Move.getFromSqi(a[i]));
				eval[i] = (1000 * pval[victim1]) + (100 * pval[att1]);
			} else {
				eval[i] = -1;
			}

		}
		/* Initially, the first item is considered 'sorted' */
		/* i divides a into a sorted region, x<i, and an
		   unsorted one, x >= i */
		for (i = 1; i < a.length; i++) {
			/* Select the item at the beginning of the
			   as yet unsorted section */
			v = a[i];
			e = eval[i];
			/* Work backwards through the array, finding where v
			   should go */
			j = i;

			/* If this element is greater than v,
				  move it up one */
			while (e > eval[j - 1]) {
				eval[j] = eval[j - 1];
				a[j] = a[j - 1];
				j = j - 1;
				if (j <= 0)
					break;
			}
			/* Stopped when a[j-1] better than v, so put v at position j */
			a[j] = v;
			eval[j] = e;
		}
	}

	private void sortCaptures(short a[]) {
		if ((a == null) || (a.length < 2)) {
			return;
		}
		// insertion sort
		int i, j;
		short v;
		/* Initially, the first item is considered 'sorted' */
		/* i divides a into a sorted region, x<i, and an
		   unsorted one, x >= i */
		for (i = 1; i < a.length; i++) {
			/* Select the item at the beginning of the
			   as yet unsorted section */
			v = a[i];
			/* Work backwards through the array, finding where v
			   should go */
			j = i;

			/* If this element is greater than v,
				  move it up one */
			while (betterCapture(v, a[j - 1])) {
				a[j] = a[j - 1];
				j = j - 1;
				if (j <= 0)
					break;
			}
			/* Stopped when a[j-1] better than v, so put v at position j */
			a[j] = v;
		}
	}

	private boolean betterCapture(short m1, short m2) {
		final int[] pval = { 0, 3, 3, 5, 9, 1, 11 }; // piece vals
		int victim1 = position.getPiece(Move.getToSqi(m1));
		int victim2 = position.getPiece(Move.getToSqi(m2));
		if (pval[victim1] > pval[victim2]) { // better piece
			return true;
		}

		// otherwise, test the attacker
		int att1 = position.getPiece(Move.getFromSqi(m1));
		int att2 = position.getPiece(Move.getFromSqi(m2));
		if (pval[att1] < pval[att2]) {
			return true;
		}
		return false;
	}

	// Alpha Beta window
	//public final int ABWIN = 30;
	//public final int SEARCH_DEPTH = 10; // 4 = 1 ply
	//public final int MAX_PLY = 6;

	private int ABWIN = 30;
	private int SEARCH_DEPTH = 10; // 4 = 1 ply
	private int MAX_PLY = 6;

	private short bestMove() {
		short best = Move.ILLEGAL_MOVE;
		int score = -INF - 10;
		long lastNodes;
		nodes = 0;
		hashTable = new HashMap(16*1024);
		int alpha, beta;
		alpha = -INF;
		beta = INF;

		short moves[];

		moves = position.getAllMoves();
		sortMoves(moves);

		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
			} catch (Exception e) {
				continue;
			}
			lastNodes = nodes;
			int val = -AlphaBeta(SEARCH_DEPTH, -beta, -alpha, 0);

			/*if (val <= alpha) {
				// redo the search
				System.err.println("Alpha Research");
				alpha = -INF;
				val = -AlphaBeta(SEARCH_DEPTH, -beta, -alpha, 0);
			}*/
			if (val >= beta) {
				// redo the search
				//System.err.println("Beta Research");
				beta = INF;
				val = -AlphaBeta(SEARCH_DEPTH, -beta, -alpha, 0);
			}
			if (val > score) {
				score = val;
				best = moves[i];
				bscore = score;
			}
			alpha = score;
			beta = score + ABWIN;
			position.undoMove();

		}

		//System.err.println("Total Nodes " + nodes);
		return best;
	}

	private int AlphaBeta(int depth, int alpha, int beta, int howDeep) {
		nodes++;


		if (position.isStaleMate()) {
			return 0;
		}
		if (position.isMate()) {
			return -MATE+(howDeep*10);
		}

		int hashf = HashEntry.hashfALPHA;
		int val;
		if ((val = ProbeHash(howDeep, alpha, beta)) != HashEntry.valUNKNOWN)
			return val;

		if ((depth < 4) || (howDeep > MAX_PLY)) {
			val = qsearch(alpha, beta,howDeep);
			RecordHash(howDeep, val, HashEntry.hashfEXACT);
			return val;
		}

		short moves[] = position.getAllMoves();
		sortMoves2(moves, howDeep);

		Move lastMove = position.getLastMove();

		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
			} catch (Exception e) {
				continue;
			}

			int piece = position.getPiece(Move.getFromSqi(moves[i]));
			int toRow = Move.getToSqi(moves[i])/8;

			if (position.isCheck()) {
				val = -AlphaBeta(depth, -beta, -alpha, howDeep + 1);
			} else if (moves.length < 2) { // forcing move
				val = -AlphaBeta(depth, -beta, -alpha, howDeep + 1);
			} else if (Move.isPromotion(moves[i])) { // pawn promotion
				val = -AlphaBeta(depth, -beta, -alpha, howDeep + 1);
			} else if ((piece == Chess.PAWN) &&
					((toRow == 1) || (toRow == 6))) { // pawn to the seventh
				val = -AlphaBeta(depth, -beta, -alpha, howDeep + 1);
			} else if (moves.length < 3) { // almost forcing
				val = -AlphaBeta(depth - 2, -beta, -alpha, howDeep + 1);
			} else if (position.getLastMove().isCapturing()) {
				val = -AlphaBeta(depth - 2, -beta, -alpha, howDeep + 1);
			} else {
				val = -AlphaBeta(depth - 4, -beta, -alpha, howDeep + 1);
			}

			position.undoMove();

			if (val >= beta) {
				//System.err.println("Cutoff at depth " + howDeep);
				killers[howDeep] = moves[i];
				RecordHash(howDeep, beta, HashEntry.hashfBETA);
				return beta;
			}

			if (val > alpha) {
				alpha = val;
				hashf = HashEntry.hashfEXACT;
			}

		}
		RecordHash(howDeep, alpha, hashf);
		return alpha;

	}

	private int qsearch(int alpha, int beta, int howDeep) {
		nodes++;

		if (position.isStaleMate()) {
			return 0;
		}
		if (position.isMate()) {
			return -MATE+(10*howDeep);
		}
		int val;
		val = evaluate(howDeep+1, alpha, beta);

		if (val >= beta)
			return beta;

		if (val > alpha)
			alpha = val;

		short moves[] = position.getAllCapturingMoves();
		sortCaptures(moves);

		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
			} catch (Exception e) {
				continue;
			}

			val = -qsearch(-beta, -alpha,howDeep+1);

			position.undoMove();

			if (val >= beta)
				return beta;

			if (val > alpha)
				alpha = val;
		}

		return alpha;
	}

	private int evaluate(int howDeep, int alpha, int beta) {
		if (position.isMate()) {
			return -MATE+(10*howDeep);
		}
		if (position.isStaleMate()) {
			return 0;
		}
		int material = position.getMaterial();

		// lazy eval
		if (material > (beta+300)) {
			return material;
		}

		int kingScore = 0;

		if (position.getPlyNumber() < 40) {
			if (position.getStone(1) == Chess.WHITE_KING) {
				kingScore = 70;
				kingScore += (position.getStone(7) == Chess.WHITE_PAWN)?10:0;
				kingScore += (position.getStone(8) == Chess.WHITE_PAWN)?20:0;
				kingScore += (position.getStone(9) == Chess.WHITE_PAWN)?10:0;
			} else if (position.getStone(6) == Chess.WHITE_KING) {
				kingScore = 70;
				kingScore += (position.getStone(13) == Chess.WHITE_PAWN)?10:0;
				kingScore += (position.getStone(14) == Chess.WHITE_PAWN)?20:0;
				kingScore += (position.getStone(15) == Chess.WHITE_PAWN)?10:0;
			}
			if (position.getStone(62) == Chess.BLACK_KING) {
				kingScore -= 70;
				kingScore -= (position.getStone(53) == Chess.BLACK_PAWN)?10:0;
				kingScore -= (position.getStone(54) == Chess.BLACK_PAWN)?20:0;
				kingScore -= (position.getStone(55) == Chess.BLACK_PAWN)?10:0;
			} else if (position.getStone(57) == Chess.BLACK_KING) {
				kingScore -= 70;
				kingScore -= (position.getStone(48) == Chess.BLACK_PAWN)?10:0;
				kingScore -= (position.getStone(49) == Chess.BLACK_PAWN)?20:0;
				kingScore -= (position.getStone(50) == Chess.BLACK_PAWN)?10:0;
			}

		}
		kingScore += positionalEval();
		kingScore = (position.getToPlay() == Chess.WHITE) ? kingScore : -kingScore;

		return material
				+ ((int) Math.round(position.getDomination()))
				+ kingScore
				+ ((int) Math.round(8 * Math.random()));
	}

	private int bking, wking; // just local to avoid an extra call

	private int positionalEval() {
		int score = 0;
		int wbish = 0;
		int bbish = 0;
		int wfiles = 0;
		int bfiles = 0;
		int wpcount[] = new int[10];
		int bpcount[] = new int[10];
		int wpback[] = new int[10];
		int bpback[] = new int[10];
		int locked[] = new int[10]; // pawns on the file are locked

		bking = position.getBlackKingSquare();
		wking = position.getWhiteKingSquare();
		// get those pawns off the original squares
		score -= (position.getStone(Chess.E2) == Chess.WHITE_PAWN) ? 30 : 0;
		score -= (position.getStone(Chess.D2) == Chess.WHITE_PAWN) ? 30 : 0;
		score += (position.getStone(Chess.E7) == Chess.BLACK_PAWN) ? 30 : 0;
		score += (position.getStone(Chess.D7) == Chess.BLACK_PAWN) ? 30 : 0;

		for (int i = 0; i < 10; i++) {
			wpcount[i] = 0; // no pawns
			bpcount[i] = 0;
			locked[i] = 0; // now locked pawns
			wpback[i] = 9; // all the way forward
			bpback[i] = -1; // all the way forward
		}
		int wrook1 = -1, wrook2 = -1, brook1 = -1, brook2 = -1;

		for (int sqi = 0; sqi < Chess.NUM_OF_SQUARES; sqi++) {
			//			Added in king tropism - DJK
			int stone = position.getStone(sqi);
			if ((stone != Chess.NO_STONE)
					&& (stone != Chess.WHITE_PAWN)
					&& (stone != Chess.BLACK_PAWN)
					&& (stone != Chess.WHITE_BISHOP)
					&& (stone != Chess.BLACK_BISHOP)) {
				score += getKingTropism(sqi, stone);
			}

			if (stone == Chess.WHITE_ROOK) {
				if (wrook1 == -1) {
					wrook1 = sqi;
				} else {
					wrook2 = sqi;
				}
			} else if (stone == Chess.BLACK_ROOK) {
				if (brook1 == -1) {
					brook1 = sqi;
				} else {
					brook2 = sqi;
				}
			}

			if (stone == Chess.WHITE_PAWN) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				wpcount[file]++;
				if (wpback[file] > row) {
					wpback[file] = row;
				}
				int stone2 = position.getStone(sqi+8); // square in front
				if (stone2 == Chess.BLACK_PAWN) { // locked pawns
					locked[file]++;
				}
			} else if (stone == Chess.BLACK_PAWN) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				bpcount[file]++;
				if (bpback[file] < row) {
					bpback[file] = row;
				}
			} else if (stone == Chess.BLACK_BISHOP) {
				bbish++;
			} else if (stone == Chess.WHITE_BISHOP) {
				wbish++;
			}
		}
		if (wbish > 1) {
			score += 25;
		}
		if (bbish > 1) {
			score -= 25;
		}
		// doubled rooks
		score += connected(wrook1, wrook2);
		score -= connected(brook1, brook2);

		// double and isolated
		for (int i = 1; i < 9; i++) {
			if (wpcount[i] > 1) {
				score -= (wpcount[i] - 1) * 10;
				if ((wpcount[i - 1] == 0) && (wpcount[i + 1] == 0)) {
					score -= 10;
				}
				wfiles++;
			}
			if (bpcount[i] > 1) {
				score += (bpcount[i] - 1) * 5;
				if ((bpcount[i - 1] == 0) && (bpcount[i + 1] == 0)) {
					score += 10;
				}
				bfiles++;
			}
		}

		// gotta have at least one open file
		if (wfiles == 8) {
			score -= 25;
		}
		if (bfiles == 8) {
			score += 25;
		}

		for (int sqi = 0; sqi < Chess.NUM_OF_SQUARES; sqi++) {
			int stone = position.getStone(sqi);
			if (stone == Chess.WHITE_PAWN) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				// is it passed
				if ((bpback[file] < row)
						&& (bpback[file - 1] <= row)
						&& (bpback[file + 1] <= row)) {
					score += (row + 1) * 8 + ((row == 6) ? 30 : 0);
				}
			} else if (stone == Chess.BLACK_PAWN) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				// is it passed
				if ((wpback[file] > row)
						&& (wpback[file - 1] >= row)
						&& (wpback[file + 1] >= row)) {
					score -= (8 - row) * 8 + ((row == 1) ? 30 : 0);
				}
			} else if ((stone == Chess.WHITE_ROOK) || (stone == Chess.WHITE_QUEEN)) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				if (wpcount[file] == 0) { // half open
					score += 20;
					if (bpcount[file] == 0) { // full open
						score += 20;
					}
				}
				if (row == 6) { // seventh rank
					score += 30;
				}
			} else if ((stone == Chess.BLACK_ROOK) || (stone == Chess.BLACK_QUEEN)) {
				int file = Chess.sqiToCol(sqi) + 1;
				int row = Chess.sqiToRow(sqi);
				if (bpcount[file] == 0) { // half open
					score -= 20;
					if (wpcount[file] == 0) { // full open
						score -= 20;
					}
				}
				if (row == 1) { // seventh rank
					score -= 30;
				}
			}
		}
		// compute the locked score
		int lockScore = locked[3]+locked[4]+locked[5]+locked[6];
		if (lockScore < 2) {
			lockScore = 0;
		} else {
			lockScore *= 10;
		}
		lockScore += (locked[1]+locked[2]+locked[7]+locked[8])*5;
		// now determine if it is good or bad, according to whether the computer
		// is white or black
		if (computerIsWhite) {
			// bad for white
			score -= lockScore;
		} else {
			// bad for black
			score += lockScore;
		}
		return score;
	}

	private int connected(int sq1, int sq2) {
		if (sq1 > sq2) {
			int temp;
			temp = sq1;
			sq1 = sq2;
			sq2 = temp;
		}

		if ((sq1 < 0) || (sq1 > 63) || (sq2 < 0) || (sq2 > 63)) {
			return 0;
		}

		int file1 = sq1%8;
		int file2 = sq2%8;
		int rank1 = sq1/8;
		int rank2 = sq2/8;

		if (file1 == file2) {
			for (int i = sq1+8; i < sq2; i+=8) {
				if (position.getStone(i) != Chess.NO_STONE) {
					return 0;
				}
			}
			return 20;
		} else if (rank1 == rank2) {
			for (int i = sq1+1; i < sq2; i++) {
				if (position.getStone(i) != Chess.NO_STONE) {
					return 0;
				}
			}
			return 20;
		} else {
			return 0;
		}
	}

	private int getKingTropism(int sqi, int stone) {
		int score = 0;
		int dcol, drow;


		switch (stone) {
			case Chess.WHITE_ROOK :
			case Chess.WHITE_KNIGHT :
				dcol = Math.abs(Chess.deltaCol(sqi, bking));
				drow = Math.abs(Chess.deltaRow(sqi, bking));
				score = 16 - ((dcol+1) * (drow+1))/4;
				break;
			case Chess.WHITE_QUEEN :
				dcol = Math.abs(Chess.deltaCol(sqi, bking));
				drow = Math.abs(Chess.deltaRow(sqi, bking));
				score = 32 - ((dcol+1) * (drow+1))/2;
				break;

			case Chess.BLACK_ROOK :
			case Chess.BLACK_KNIGHT :
				dcol = Math.abs(Chess.deltaCol(sqi, wking));
				drow = Math.abs(Chess.deltaRow(sqi, wking));
				score = ((dcol+1) * (drow+1))/4 - 16;
				break;
			case Chess.BLACK_QUEEN :
				dcol = Math.abs(Chess.deltaCol(sqi, wking));
				drow = Math.abs(Chess.deltaRow(sqi, wking));
				score = ((dcol+1) * (drow+1))/2 - 32;
				break;

		}
		return score;
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#makeMove(java.lang.String)
	 */
	public synchronized String makeMove(String move) {
		short m = parseMove(move);
		if (m == Move.ILLEGAL_MOVE) {
			return "ERROR: illegal move";
		}
		try {

			position.doMove(m);

		} catch (Exception e) {
			return "ERROR: " + e;
		}
		ply++;
		return Move.getString(m);
	}

	public synchronized short parseMove(String move) {
		move = move.trim();
		String alt1, alt2;
		if ((move.length() < 4) || move.startsWith("O")) {
			alt1 = move;
			alt2 = move;
		} else {
			alt1 = move.substring(0, 2) + "-" + move.substring(2);
			alt2 = move.substring(0, 2) + "x" + move.substring(2);
		}
		short moves[] = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			String temp = Move.getString(moves[i]);
			if (temp.equalsIgnoreCase(move)
					|| temp.equalsIgnoreCase(alt1)
					|| temp.equalsIgnoreCase(alt2)) {
				return moves[i];
			}
		}
		return Move.ILLEGAL_MOVE;
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isWhiteTurn()
	 */
	public synchronized boolean isWhiteTurn() {
		//return Position.isWhiteToPlay(position.getHashCode());
		//return (position.getPlyNumber()%2)==0;
		return (position.getToPlay() == Chess.WHITE);
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isDraw()
	 */
	public synchronized boolean isDraw() {
		return position.isStaleMate();
	}

	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#isMate()
	 */
	public synchronized boolean isMate() {
		return position.isMate();
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#getBoard(int)
	 */
	public synchronized char[][] getBoard(int ply) {

		for (int k = 0; k < ply; k++) {
			position.undoMove();
		}
		char[][] retval = new char[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int piece = position.getStone((i * 8) + j);
				switch (piece) {
					case Chess.BLACK_BISHOP :
						retval[i][j] = 'b';
						break;
					case Chess.WHITE_BISHOP :
						retval[i][j] = 'B';
						break;
					case Chess.BLACK_KNIGHT :
						retval[i][j] = 'n';
						break;
					case Chess.WHITE_KNIGHT :
						retval[i][j] = 'N';
						break;
					case Chess.BLACK_ROOK :
						retval[i][j] = 'r';
						break;
					case Chess.WHITE_ROOK :
						retval[i][j] = 'R';
						break;
					case Chess.BLACK_QUEEN :
						retval[i][j] = 'q';
						break;
					case Chess.WHITE_QUEEN :
						retval[i][j] = 'Q';
						break;
					case Chess.BLACK_KING :
						retval[i][j] = 'k';
						break;
					case Chess.WHITE_KING :
						retval[i][j] = 'K';
						break;
					case Chess.BLACK_PAWN :
						retval[i][j] = 'p';
						break;
					case Chess.WHITE_PAWN :
						retval[i][j] = 'P';
						break;
					default :
						retval[i][j] = ' ';
				}
			}
		}
		for (int k = 0; k < ply; k++) {
			position.redoMove();
		}
		return retval;
	}
	/* (non-Javadoc)
	 * @see com.imaginot.chess.engine.ChessEngine#currentPly()
	 */
	public int currentPly() {
		return ply;
	}

	/*
	 * 
	 * @author Dietrich Kappe
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	public class HashEntry {
		public final static int hashfEXACT = 0;
		public final static int hashfALPHA = 1;
		public final static int hashfBETA = 2;
		public final static int valUNKNOWN = 3;
		public int depth;
		public int flags;
		public int value;
		//public short best;
	}

	int ProbeHash(int depth, int alpha, int beta) {
		Long hash = new Long(position.getHashCode());
		HashEntry he = (HashEntry)hashTable.get(hash);
		if (he != null) {

			if (he.depth <= depth) {
				if (he.flags == he.hashfEXACT)
					return he.value;
				if ((he.flags == he.hashfALPHA) && (he.value <= alpha))
					return alpha;
				if ((he.flags == he.hashfBETA) && (he.value >= beta))
					return beta;
			}
			//RememberBestMove();
		}

		return HashEntry.valUNKNOWN;

	}



	void RecordHash(int depth, int value, int hashf) {
		Long hash = new Long(position.getHashCode());
		HashEntry he = (HashEntry)hashTable.get(hash);
		if (he != null) {
			he.value = value;
			he.flags = hashf;
			he.depth = depth;
		}
	}
	public int[] getBoard(char[][] ply) {
		int[] res = new int[9];
		return res;
	}
	public boolean isCheck() {
		return position.isCheck();
	}
	/**
	 * returns all possible moves
	 * @return
	 */
	public String[] getAllMoves()
	{
		Vector<String> vector = new Vector<String>();
		short[] moves = position.getAllMoves();

		for (int i = 0; i < moves.length; i++)
		{
			vector.add(Move.getString(moves[i]));
		}

		return (String[]) vector.toArray();
	}
}