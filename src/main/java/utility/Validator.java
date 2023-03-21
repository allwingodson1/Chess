package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Validator {
	public static boolean validMove(Board board,int start,int end, JSONObject jsonRes) {
		Piece piece = board.getPiece(start);
		if(piece.coin.equals(Coin.Pawn)) {
			boolean enPassantFlag = true;
			boolean twoMoveFlag = (piece.color.equals(Color.White)&&(start/8)==6)||(piece.color.equals(Color.Black)&&(start/8)==1);
			if((start%8)==end%8) {
				int diff = ((start/8)-(end/8))/piece.color.value;
				if(board.getPiece(end)==null) {
					if((diff == 1)) {
						
					}
					else if((twoMoveFlag&&diff==2)){
						enPassantFlag = false;
					}
					else return false;
				}
				else return false;
			}
			else {
				boolean cond1 = ((Math.abs((start%8)-(end%8)) == 1)&&((start/8)-(end/8) == piece.color.value));
				boolean cond2 = cond1&&(board.getPiece(end)!=null&&!board.getPiece(end).color.equals(piece.color));
				boolean cond3 = cond1&&(board.getPiece(end+(8*piece.color.value)) != null && board.getPiece(end+(8*piece.color.value)) == board.enPassant);
				if(jsonRes!=null)System.out.println("hii"+cond1+cond2+cond3);
				if(!(cond1&&(cond2||cond3))) {
					return false;
				}
				else {
					if(cond3) {
						JSONObject json = new JSONObject();
						json.put("actionName","remove");
						json.put("pos",end+(8*piece.color.value));
						if(jsonRes!=null) {
							((JSONArray) jsonRes.get("actions")).add(json);
						}
					}
				}
			}
			if(enPassantFlag) {
				board.enPassant = null;
			}
			else {
				board.enPassant = piece;
			}
		}
		else if(piece.coin.equals(Coin.Rook)) {
			if(rookCheck(start, end, board, piece)) {
				board.enPassant = null;
				if(piece.color.equals(Color.Black)) {
					boolean contains = board.movedBlackRook[0] == piece||board.movedBlackRook[1] == piece;
					if(!contains) {
						board.movedBlackRook[board.movedBlackRook[0] == null?0:1] = piece;
					}
				}
				else {
					boolean contains = board.movedWhiteRook[0] == piece||board.movedWhiteRook[1] == piece;
					if(!contains) {
						board.movedWhiteRook[board.movedWhiteRook[0] == null?0:1] = piece;
					}
				}
				return true;
			}
			return false;
		}
		else if(piece.coin.equals(Coin.Knight)) {
			int diff1 = Math.abs((start%8)-(end%8));
			int diff2 = Math.abs((start/8)-(end/8));
			return (diff1!=0&&diff2!=0&&diff1+diff2 == 3&&(board.getPiece(end)==null||!(board.getPiece(end).color.equals(piece.color))));
		}
		else if(piece.coin.equals(Coin.Bishop)) {
			return bishopCheck(start, end, board, piece);
		}
		else if(piece.coin.equals(Coin.Queen)) {
			return rookCheck(start, end, board, piece)||bishopCheck(start, end, board, piece);
		}
		else if(piece.coin.equals(Coin.King)) {
			int diff1 = Math.abs((start%8)-(end%8));
			int diff2 = Math.abs((start/8)-(end/8));
			if(diff1<2&&diff2<2&&start!=end&&(board.getPiece(end)==null||!board.getPiece(end).color.equals(piece.color))) {
				if(piece.color.equals(Color.Black)) {
					board.blackKingMoved = true;
				}
				else {
					board.whiteKingMoved = true;
				}
				return true;
			}
			else if(jsonRes!=null){
				if(piece.color.equals(Color.White)?!board.whiteKingMoved:!board.blackKingMoved && diff1 == 2) {
					int temp =end+((start>end)?-2:1);
					int myStart = start;
					Piece rook = (board.getPiece(temp));
					if(!rook.coin.equals(Coin.Rook)) {
						return false;
					}
					if((piece.color.equals(Color.White)&&(board.movedWhiteRook[0] != rook && board.movedWhiteRook[1] != rook))||(piece.color.equals(Color.Black)&&(board.movedBlackRook[0] != rook && board.movedBlackRook[1] != rook))) {
						 while(true) {
							 if(myStart == end) {
								 break;
							 }
							 if(myStart>end) {
								 myStart--;
							 }
							 else myStart++;
							 System.out.println(board.getPiece(myStart));
							 if(board.getPiece(myStart)!=null) {
								 return false;
							 }
							 for(int i=0;i<8;i++) {
								 for(int k=0;k<8;k++) {
									 Piece j = board.pieces[i][k];
									 if(j == null||j.color.equals(piece.color)||(j.coin.equals(Coin.King))&&!(j.color.equals(Color.Black)?board.blackKingMoved:board.whiteKingMoved)) {
										 continue;
									 }
									 else {
										 if(validMove(board, (i*8)+(k), myStart, null)) {
											 System.out.println(j+""+((i*8)+k)+""+myStart);
											 return false;
										 }
									 }
								 }
							 }
						 }
						 JSONObject json = new JSONObject();
						 int check = piece.color.value;
						 json.put("start", temp);
						 json.put("end", start>end?end+1:end-1);
						 if(jsonRes!=null) {
							 ((JSONArray)jsonRes.get("moves")).add(json);
						 }
					}
				}
			}
			else return false;
		}
		return true;
	}
	public static boolean bishopCheck(int start,int end,Board board,Piece piece) {
		int diff1 = Math.abs((start%8)-(end%8));
		int diff2 = Math.abs((start/8)-(end/8));
		int start1 = start/8;
		int end1 = end/8;
		int start2 = start%8;
		int end2 = end%8;
		if (diff1!=0&&diff1==diff2&&(board.getPiece(end)==null||!(board.getPiece(end).color.equals(piece.color)))) {
			while(true) {
				if(start1>end1) {
					start1--;
				}
				else {
					start1++;
				}
				if(start2>end2) {
					start2--;
				}
				else {
					start2++;
				}
				if(start1 == end1) {
					break;
				}
				if(board.pieces[start1][start2]!=null) {
					return false;
				}
			}
			return true;
		}
		else return false;
	
	}
	public static boolean rookCheck(int start,int end,Board board,Piece piece) {
		if((start%8 == end%8||start/8 == end/8) && (board.getPiece(end)==null||!(board.getPiece(end).color.equals(piece.color)))) {
			int start1 = start/8;
			int end1 = end/8;
			int start2 = start%8;
			int end2 = end%8;
			while(true) {
				if(start1 == end1) {
					start2 += start2>end2?-1:1;
				}
				else {
					start1 += start1>end1?-1:1;
				}
				if(start1 == end1&&start2 == end2) {
					break;
				}
				if(board.pieces[start1][start2]!=null) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	public static boolean inCheck(Board board,Color color, AtomicInteger king2,List<Integer> list, AtomicInteger checkPos) {
		AtomicInteger king = new AtomicInteger();
		if(list == null) {
			list = new ArrayList<>();
		}
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(board.pieces[i][j] == null) {
					continue;
				}
				if(color.equals(board.pieces[i][j].color)&&board.pieces[i][j].coin.equals(Coin.King)) {
					king.set(i*8+j);
					if(king2!=null) {
						king2.set(i*8+j);
					}
				}
				else if(!color.equals(board.pieces[i][j].color)&&!(board.pieces[i][j].coin.equals(Coin.King))){
					list.add(i*8+j);
				}
			}
		}
		AtomicBoolean cond = new AtomicBoolean(false);
		list.forEach(n->{
			if(validMove(board, n, king.intValue(), null)){
				cond.set(true);
				if(checkPos!=null) {
					checkPos.set(n);
				}
				return;
			}
		});
		return cond.get();
	}
	public static boolean isSquareFreeForKing(Board board, int king, int square, List<Integer> pieces,Color color) {
		Piece squarePiece = board.getPiece(square);
		System.out.println("inmovecheck"+board.getPiece(king));
		board.setPiece(square, board.getPiece(king));
		board.setPiece(king, squarePiece);
		boolean result = !inCheck(board, color, null, null, null);
		board.setPiece(king, board.getPiece(square));
		board.setPiece(square, squarePiece);
		return result;
	}
	public static boolean kingMovable(Board board,int val,List<Integer> pieces,Color color) throws CloneNotSupportedException {
		int[][] moves = {{-1,-1},{1,1},{-1,1},{1,-1},{0,1},{1,0},{0,-1},{-1,0}};
		int one = val/8;
		int two = val%8;
		Board cloneBoard = (Board)board.clone();
		for(int i=0;i<8;i++) {
			int oneTemp = one+moves[i][0];
			int twoTemp = two+moves[i][1];
			if(oneTemp>=0&&oneTemp<8&&twoTemp>=0&&twoTemp<8) {
				if(validMove(board, val, oneTemp*8+twoTemp, null)) {
					if(isSquareFreeForKing(cloneBoard,val,oneTemp*8+twoTemp,pieces, color)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean checkForCheckMate(Board board, int king, int checkCoin) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Color color = board.getPiece(king).color;
		Piece check = board.getPiece(checkCoin);
		if(check.coin.equals(Coin.Pawn)||check.coin.equals(Coin.Knight)) {
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					if(board.pieces[i][j]!=null&&board.pieces[i][j].color.equals(color)&&validMove(board, i*8+j, checkCoin, null)) {
						Board cloneBoard = (Board)board.clone();
						cloneBoard.setPiece(checkCoin, cloneBoard.getPiece(i*8+j));
						cloneBoard.setPiece(i*8+j, null);
						return inCheck(cloneBoard, color, null, null, null);
					}
				}
			}
		}
		else if(check.coin.equals(Coin.Bishop)) {
			return bishopCheck(board, king, checkCoin, color);
		}
		else if(check.coin.equals(Coin.Rook)) {
			return rookCheck(board, king, checkCoin, color);
		}
		else if(check.coin.equals(Coin.Queen)) {
			return bishopCheck(board, king, checkCoin, color)&&rookCheck(board, king, checkCoin, color);
		}
		return true;
	}
	public static boolean bishopCheck(Board board,int king,int checkCoin,Color color) throws CloneNotSupportedException {
		int start1 = checkCoin/8;
		int end1 = king/8;
		int start2 = checkCoin%8;
		int end2 = king%8;
		while(true) {
			if(start1 == end1) {
				break;
			}
			int pos = start1*8+end1;
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					if(board.pieces[i][j]!=null&&board.pieces[i][j].color.equals(color)&&validMove(board, i*8+j, pos, null)) {
						Board cloneBoard = (Board)board.clone();
						cloneBoard.setPiece(pos, cloneBoard.getPiece(i*8+j));
						cloneBoard.setPiece(i*8+j, null);
						return inCheck(cloneBoard, color, null, null, null);
					}
				}
			}
			if(start1>end1) {
				start1--;
			}
			else {
				start1++;
			}
			if(start2>end2) {
				start2--;
			}
			else {
				start2++;
			}
		}
		return true;
	}
	public static boolean rookCheck(Board board,int king,int checkCoin,Color color) throws CloneNotSupportedException {
		int start1 = checkCoin/8;
		int end1 = king/8;
		int start2 = checkCoin%8;
		int end2 = king%8;
		while(true) {
			if(start1 == end1&&start2 == end2) {
				break;
			}
			int pos = start1*8+end1;
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					if(board.pieces[i][j]!=null&&board.pieces[i][j].color.equals(color)&&validMove(board, i*8+j, pos, null)) {
						Board cloneBoard = (Board)board.clone();
						cloneBoard.setPiece(pos, cloneBoard.getPiece(i*8+j));
						cloneBoard.setPiece(i*8+j, null);
						return inCheck(cloneBoard, color, null, null, null);
					}
				}
			}
			if(start1 == end1) {
				start2 += start2>end2?-1:1;
			}
			else {
				start1 += start1>end1?-1:1;
			}
		}
		return true;
	}
	public static boolean checkForStaleMate(Board board,Color color) {
		return false;
	}
}
