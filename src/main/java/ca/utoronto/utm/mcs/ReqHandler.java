package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;

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
        /*
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
        */
    }
    /*
    public void handleGet(HttpExchange exchange) throws IOException, JSONException {
        try {
            switch (exchange.get)
        }
    }

    */

}