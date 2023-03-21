package servlets;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.Tools;

@WebFilter(urlPatterns = {"/createGame","/myGame/*"})
public class CheckCookie extends HttpFilter{
	public void doFilter(ServletRequest request,ServletResponse res,FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		if(Tools.checkCookie(req.getCookies())) {
			Cookie cookie = new Cookie("SessionId",UUID.randomUUID().toString());
			cookie.setPath("/");
			((HttpServletResponse) res).addCookie(cookie);
		}
		chain.doFilter(req, res);
	}
}
