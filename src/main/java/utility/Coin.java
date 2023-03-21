package utility;

public enum Coin {
	King(10),
	Queen(9),
	Knight(3),
	Bishop(2),
	Rook(5),
	Pawn(1);
	int value;
	Coin(int i){
		this.value = i;
	}
}
