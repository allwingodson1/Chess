package utility;

public class Piece implements Cloneable{
	public Coin coin;
	public Color color;
	public Piece(Coin coin,Color color) {
		this.coin = coin;
		this.color = color;
	}
	public String toString() {
		return ""+coin+color;
	}
	public Piece clone() {
		return new Piece(coin, color);
	}
}
