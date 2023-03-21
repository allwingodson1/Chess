package utility;

import java.sql.Connection;
import java.util.UUID;

import javax.servlet.http.Cookie;

public class Tools {
	public static Cookie getCookie(String cookies,String cookie) {
		String[] cookieArr = cookies.split("; ");
		for(String curCookie:cookieArr) {
			String[] curCookieArr = curCookie.split("=");
			String name = curCookieArr[0];
			String value = curCookieArr[1];
			if(name.equals(cookie)) {
				return new Cookie(name,value);
			}
		}
		return null;
	}
	public static boolean checkCookie(Cookie[] cookies) {
		boolean flag = true;
		if(cookies != null){
			for(Cookie cookie:cookies) {
				if(cookie.getName().equals("SessionId")) {
					return false;
				}
			}
		}
		return true;
	}
}
