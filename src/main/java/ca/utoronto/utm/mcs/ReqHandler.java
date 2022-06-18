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
                    //this.handleGet(exchange);
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

    public int addActor(JSONObject deserialized){
        String name, actorId;
        int insert_actor_res;
        try {
            if (deserialized.has("name") && deserialized.has("actorId")) {
                //check whether the actor exists
                name = deserialized.getString("name");
                actorId = deserialized.getString("actorId");
            } else {
                return 400;
                //actor already exists
            }
        } catch (Exception e){
            //can't read the string from deserialized
            e.printStackTrace();
            return 500;
        }
        try{
            insert_actor_res = this.dao.insertActor(name, actorId);
        } catch (Exception e){
            e.printStackTrace();
            return 500;
        }
        return insert_actor_res;

    }

    //return the response to addMovie
    public int addMovie(JSONObject deserialized){
        String name, movieId;
        int insert_movie_res;
        try {
            if (deserialized.has("name") && deserialized.has("movieId")) {
                //check whether the movie exists
                name = deserialized.getString("name");
                movieId = deserialized.getString("movieId");
            } else {
                return 400;
                //movie already exists
            }
        } catch (Exception e){
            //can't read the string from deserialized
            e.printStackTrace();
            return 500;
        }
        try{
            insert_movie_res = this.dao.insertMovie(name, movieId);
        } catch (Exception e){
            e.printStackTrace();
            return 500;
        }
        return insert_movie_res;
    }

    //return the respones to addRelationship
    public int addRelationship(JSONObject deserialized){
        String actorId, movieId;
        int insert_relationship_res;
        try {
            //check whether we get required information
            if (deserialized.has("actorId") && deserialized.has("movieId")) {
                actorId = deserialized.getString("actorId");
                movieId = deserialized.getString("movieId");
            } else {
                //we don't have the required information
                return 400;
            }
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }
        //add the relationship here
        try{
            insert_relationship_res = this.dao.addRelationship(actorId, movieId);
        } catch (Exception e){
            e.printStackTrace();
            return 500;
        }
        return insert_relationship_res;

    }

    public void handlePut(HttpExchange exchange) throws IOException, JSONException {
        String body = Utils.convert(exchange.getRequestBody());
        String path = exchange.getRequestURI().getPath();
        int api_response;
        try {
            JSONObject deserialized = new JSONObject(body);

            switch(path){
                //distinguish the path
                case "/api/v1/addActor":
                   api_response = this.addActor(deserialized);
                   exchange.sendResponseHeaders(api_response, -1);
                   break;
                case "/api/v1/addMovie":
                    api_response = this.addMovie(deserialized);
                    exchange.sendResponseHeaders(api_response, -1);
                    break;
                case " /api/v1/addRelationship":
                    api_response = this.addRelationship(deserialized);
                    exchange.sendResponseHeaders(api_response, -1);
                    break;
            }

        } catch (Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }



}