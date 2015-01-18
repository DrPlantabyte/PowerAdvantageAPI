package cyano.poweradvantage.math;

/**
 * This class is used to store 2D integer coordinates
 * @author DrCyano
 *
 */
public final class Integer2D {
	/**
	 * X-coordinate of X,Y coordinate pair
	 */
	public final int X;
	/**
	 * Y-coordinate of X,Y coordinate pair
	 */
	public final int Y;
	/**
	 * Creates an integer pair to be used as 2D coordinates
	 * @param x X-coordinate of X,Y coordinate pair
	 * @param y Y-coordinate of X,Y coordinate pair
	 */
	public Integer2D(int x, int y){
		this.X = x;
		this.Y = y;
	}
}
