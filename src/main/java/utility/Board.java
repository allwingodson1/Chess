package utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import websockets.Socket;

public class Board implements Cloneable{
	public List<Session> sessions = new ArrayList<>();
	public String player1;
	public String player2;
	public boolean curMove = true;
	public boolean color = true;
	public Piece[][] pieces;
	public Piece enPassant;
	public Piece[] movedBlackRook = new Piece[2];
	public Piece[] movedWhiteRook = new Piece[2];
	public boolean blackKingMoved = false;
	public boolean whiteKingMoved = false;	
	public Board(boolean color) {
		Piece blackPawn = new Piece(Coin.Pawn, Color.Black);
		Piece whitePawn = new Piece(Coin.Pawn, Color.White);
		Piece[][] temp = {
				{new Piece(Coin.Rook,Color.Black),new Piece(Coin.Knight,Color.Black),new Piece(Coin.Bishop,Color.Black),new Piece(Coin.Queen,Color.Black),new Piece(Coin.King,Color.Black),new Piece(Coin.Bishop,Color.Black),new Piece(Coin.Knight,Color.Black),new Piece(Coin.Rook,Color.Black)},
				{blackPawn.clone(),blackPawn.clone(),blackPawn.clone(),blackPawn.clone(),blackPawn.clone(),blackPawn.clone(),blackPawn.clone(),blackPawn.clone()},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{whitePawn.clone(),whitePawn.clone(),whitePawn.clone(),whitePawn.clone(),whitePawn.clone(),whitePawn.clone(),whitePawn.clone(),whitePawn.clone()},
				{new Piece(Coin.Rook,Color.White),new Piece(Coin.Knight,Color.White),new Piece(Coin.Bishop,Color.White),new Piece(Coin.Queen,Color.White),new Piece(Coin.King,Color.White),new Piece(Coin.Bishop,Color.White),new Piece(Coin.Knight,Color.White),new Piece(Coin.Rook,Color.White)}
		};
		pieces = temp;
		System.out.println(temp[0].length);
	}
	public Board(Board board) {
		this.enPassant = board.enPassant;
		this.pieces = new Piece[8][8];
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(board.pieces[i][j] == null) {
					continue;
				}
				else {
					this.pieces[i][j] = board.pieces[i][j].clone();
				}
			}
		}
	}
	public JSONObject getJSON() {
		JSONArray arr = new JSONArray();
		JSONObject json = new JSONObject();
		int k = 0;
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(pieces[i][j] != null) {
					JSONObject temp = new JSONObject();
					temp.put("piece", pieces[i][j].coin.value*pieces[i][j].color.value);
					temp.put("pos", k);
					arr.add(temp);
				}
				k++;
			}
		}
		json.put("player1", player1);
		json.put("player2", player2);
		json.put("color", color);
		json.put("pieces", arr);
		return json;
	}
	public void sendJSONtoall(JSONObject json) {
		this.sessions.forEach(n->{
			try {
				if(Socket.sessions.containsKey(n.getId())) {
					n.getBasicRemote().sendText(json.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	public Object clone() throws CloneNotSupportedException {
		Board board = new Board(this);
		return board;
	}
	public Piece getPiece(int val) {
		return this.pieces[val/8][val%8];
	}
	public void setPiece(int val, Piece piece) {
		// TODO Auto-generated method stub
		this.pieces[val/8][val%8] = piece;
	}
}
