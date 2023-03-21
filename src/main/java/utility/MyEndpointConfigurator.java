package utility;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class MyEndpointConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, 
                                HandshakeRequest request, 
                                HandshakeResponse response){
        Map<String,List<String>> headers = request.getHeaders();
        List<String> cookies = headers.get("cookie");
        config.getUserProperties().put("cookie", cookies.get(0));
        cookies.forEach(System.out::println);
    }
}