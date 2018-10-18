/*
 * Created on Apr 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package engine;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ChessEngine {
	public String lastMove();
	/**
	 * Set the board to it's starting position.
	 *
	 */
	public void reset();
	/**
	 * The engine makes a move.
	 * @return SAN notation, i.e. Ne3, ed, etc.
	 */
	public String go();
	/**
	 * Make a move 
	 * @param move in SAN
	 * @return is the move valid
	 */
	public String makeMove(String move);
	public char[][] getBoard(int ply);
	public int[] getBoard(char[][] ply);
	public boolean isWhiteTurn();
	public boolean isDraw();
	public boolean isMate();
	public boolean isCheck();
	public int currentPly();
	
	public String[] getAllMoves();
}
