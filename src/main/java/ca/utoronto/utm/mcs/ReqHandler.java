package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    public Neo4jDAO dao;
    // TODO Complete This Class
    @Inject
    public ReqHandler(Neo4jDAO dao) {
        this.dao = dao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.handleGet(exchange);
                    break;
                case "PUT":
                    this.handlePut(exchange);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleGet(HttpExchange exchange) throws IOException, JSONException {
        String body = Utils.convert(exchange.getRequestBody());
        String path = exchange.getRequestURI().getPath();
        try {
            JSONObject deserialized = new JSONObject(body);

            String name, movieId, actorId,
            switch(path){
                //distinguish the path
                case "/api/v1/addActor":
                    if(deserialized.has("name") && deserialized.has("actorId")){
                        name = deserialized.getString("name");
                        actorId = deserialized.getString("actorId");
                    } else{
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                    try{
                        this.dao.insertActor(name, actorId);
                        break;
                    } catch (Exception e){
                        exchange.sendResponseHeaders(500, -1);
                        e.printStackTrace();
                        return;
                    }
                    exchange.sendResponseHeaders(200, -1);
                    break;

            }

        } catch (Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }



}