package servlets;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFileChooser;

import utility.Board;
import websockets.Socket;

@WebServlet(urlPatterns = {"/createGame"},loadOnStartup = 0)
public class CreateGame extends HttpServlet{
	
	public static void main(String[] args) {
		JFileChooser chose = new JFileChooser("./");
        chose.setFileSelectionMode(2);
        chose.showSaveDialog(chose);
        var file =  chose.getSelectedFile();

	}
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
		UUID uid = UUID.randomUUID();
		Socket.addGame(uid.toString(), new Board(true));
		res.getWriter().append(uid.toString());
	}
}
