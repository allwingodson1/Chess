package servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/myGame/*")
public class Game extends HttpServlet{
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
		res.setContentType("text/html");
		String path = req.getPathInfo().substring(1);
		res.getWriter().write("\n"
				+ "				<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "<meta charset=\"UTF-8\">\n"
				+ "<title>Insert title here</title>\n"
				+ "<link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css\">\n"
				+ "<script src=\"//code.jquery.com/jquery-1.12.4.js\"></script>\n"
				+ "<script src=\"//code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>  "
				+ "</head>\n"
				+ "<style>\n"
				+ "    html,body{\n"
				+ "        width: 100%;\n"
				+ "        height: 100%;\n"
				+ "        display: flex;\n"
				+ "        align-items: center;\n"
				+ "        justify-content: center;\n"
				+ "			overflow:hidden;"
				+ "    }\n"
				+ "    .black,.black .piece{\n"
				+ "        transform: rotate(90deg);\n"
				+ "    }\n"
				+ "		.piece_holder{"
				+ "			    display: flex;\n"
				+ "    align-items: center;\n"
				+ "    justify-content: center;"
				+ "		}"
				+ "		.piece{"
				+ "			width:100px"
				+ "		}"
				+ "    #board{\n"
				+ "        width: 900px;\n"
				+ "        height: 900px;\n"
				+ "        display: grid;\n"
				+ "        grid-template-rows: repeat(8,1fr);\n"
				+ "        grid-template-columns: repeat(8,1fr);\n"
				+ "    }\n"
				+ "</style>\n"
				+ "<body>\n"
				+ "	<div id = 'board'>\n"
				+ "\n"
				+ "    </div>\n"
				+ "    <script src = '../script.js'></script>\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "				");
	}
}