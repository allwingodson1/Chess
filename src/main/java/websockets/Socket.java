package websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.Cookie;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utility.Board;
import utility.Color;
import utility.MyEndpointConfigurator;
import utility.Piece;
import utility.Tools;
import utility.Validator;

@ServerEndpoint(value = "/mygame/{gameId}", configurator=MyEndpointConfigurator.class)
public class Socket {
	private static Map<String,Board> games = new HashMap<>();
	public static Map<String,String> sessions = new HashMap<>();
	public synchronized static boolean addGame(String session,Board game) {
		if(!games.containsKey(session)) {
			games.put(session, game);
		}
		return !games.containsKey(session);
	}
	@OnOpen
	public void open(Session session,@PathParam("gameId") String gameId) throws IOException {
		Cookie cookie = Tools.getCookie(""+session.getUserProperties().get("cookie"), "SessionId");
		for(String i:sessions.keySet()) {
			if(i.equals(session.getId())) {
				return;
			}
		}
		System.out.println(sessions.keySet());
		if(games.containsKey(gameId)) {
			sessions.put(session.getId(), gameId);
			Board board = games.get(gameId);
			if(board.player1 == null) {
				board.player1 = cookie.getValue();
				board.sessions.add(session);
			}
			else if(board.player2 == null&&!(board.player1.equals(cookie.getValue()))) {
				board.sessions.add(session);
				board.player2 = cookie.getValue();
				JSONObject json = board.getJSON();
				json.put("phase", "start");
				board.sendJSONtoall(json);
			}
			else if(board.player1.equals(cookie.getValue())||board.player2.equals(cookie.getValue())) {
				board.sessions.add(session);
				JSONObject json = board.getJSON();
				json.put("phase", "start");
				session.getBasicRemote().sendText(json.toString()); 
			}
		}
	}
	@OnClose
	public void close(Session session) {
		if(sessions.containsKey(session.getId())) {
			String chatId = sessions.get(session.getId());
			sessions.remove(session.getId());
			Board board = games.get(chatId);
			AtomicInteger i = new AtomicInteger();
			board.sessions.forEach(n->{
				if(n.getId().equals(session.getId())) {
					return;
				}
				i.incrementAndGet();
			});
			synchronized (board.sessions) {
				board.sessions.remove(i);
			}
		}
	}
	/**
	 * @param message
	 * @param session
	 * @throws ParseException
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings("unchecked")
	@OnMessage
	public void message(String message,Session session) throws ParseException, CloneNotSupportedException {
		JSONObject json = (JSONObject) new JSONParser().parse(message);
		JSONObject jsonRes = new JSONObject();
		if(((String)(json.get("phase"))).equals("move")) {
			Board board = games.get(sessions.get(session.getId()));
			jsonRes.put("phase", "move");
			jsonRes.put("moves", new JSONArray());
			jsonRes.put("actions", new JSONArray());
			int start = Integer.parseInt(""+json.get("start"));
			int end = Integer.parseInt(""+json.get("end"));
			AtomicInteger king = new AtomicInteger();
			List<Integer> list = new ArrayList<>();
			boolean cond1 = (""+json.get("color")).equals("white") == board.curMove && ((""+json.get("color")).equals("white") == board.getPiece(start).color.equals(Color.White));
			if(cond1&&Validator.validMove(board,start, end,null)) {
				JSONObject jsonTemp = new JSONObject();
				jsonTemp.put("start", start);
				jsonTemp.put("end", end);
				JSONArray arr = (JSONArray)jsonRes.get("moves");
				JSONArray actions = (JSONArray)jsonRes.get("actions");
				(arr).add(jsonTemp);
				Board cloneBoard = (Board)board.clone();
				arr.forEach(n->{
					Piece piece = cloneBoard.getPiece(Integer.parseInt(""+((JSONObject)n).get("start")));
					cloneBoard.setPiece(Integer.parseInt(""+((JSONObject)n).get("end")),piece);
					cloneBoard.setPiece(Integer.parseInt(""+((JSONObject)n).get("start")), null);
				});
				actions.forEach(n->{
					JSONObject action = (JSONObject)n;
					if(action.get("actionName").equals("remove")) {
						board.setPiece(Integer.parseInt(""+action.get("pos")), null);
					}
				});
				if(!Validator.inCheck(cloneBoard, board.curMove?Color.White:Color.Black,null,null,null)) {
					synchronized(board) {
						arr.forEach(n->{
							Piece piece = board.getPiece(Integer.parseInt(""+((JSONObject)n).get("start")));
							board.setPiece(Integer.parseInt(""+((JSONObject)n).get("end")),piece);
							board.setPiece(Integer.parseInt(""+((JSONObject)n).get("start")), null);
						});
						actions.forEach(n->{
							JSONObject action = (JSONObject)n;
							if(action.get("actionName").equals("remove")) {
								board.setPiece(Integer.parseInt(""+action.get("pos")), null);
							}
						});
						board.curMove = !board.curMove;
					}
					board.sendJSONtoall(jsonRes);
					AtomicInteger checkPos = new AtomicInteger();
					boolean check = (Validator.inCheck(board, board.curMove?Color.White:Color.Black,king,list,checkPos));
					boolean kingMovable = Validator.kingMovable(board, king.intValue(), list,board.curMove?Color.White:Color.Black);
					System.out.println(check+"\t"+kingMovable+"\t"+list);
					if(check&&!kingMovable) {
						System.out.println(Validator.checkForCheckMate(board,king.intValue(),checkPos.intValue()));
						if(Validator.checkForCheckMate(board,king.intValue(),checkPos.intValue())) {
							JSONObject jsonResponse = new JSONObject(); 
							jsonResponse.put("phase", "end");
							jsonResponse.put("code", (board.curMove?"Black":"White")+" won the Match");
							board.sendJSONtoall(jsonResponse);
						}
					}
					else if(!kingMovable && !check) {
						Validator.checkForStaleMate(board,board.curMove?Color.White:Color.Black);
					}
				}
			}
		}
	}
}
