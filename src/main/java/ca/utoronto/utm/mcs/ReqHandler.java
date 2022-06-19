package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;
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

    //return the response to addRelationship
    public int addRelationship(JSONObject deserialized){
        String actorId, movieId;
        int insert_relationship_res;
        try {
            //check whether we get received information
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
            insert_relationship_res = this.dao.insertRelationship(actorId, movieId);
        } catch (Exception e){
            e.printStackTrace();
            return 500;
        }
        return insert_relationship_res;

    }

    //return the response extracted from JSONObject
    public int get_Actor_Res(JSONObject deserialized){
        String Id;
        int get_res;
        try{
            if(deserialized.has("actorId")){
                Id = deserialized.getString("actorId");
            } else{
                //we don't have the required information
                return 400;
            }
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }
        //get the JSONObject from Neo4jDAO.java
        JSONObject new_data = this.dao.fetchActor(Id);
        try{
            if(new_data.has("code")){
                get_res = new_data.getInt("code");
                //return the code in the JSONObject
                return get_res;
            }
            return 500;
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }

    }

    //return the response extracted from JSONObject
    public int get_Movie_Res(JSONObject deserialized){
        String Id;
        int get_res;
        try{
            if(deserialized.has("movieId")){
                Id = deserialized.getString("movieId");
            } else{
                //we don't have the required information
                return 400;
            }
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }
        //get the JSONObject from Neo4jDAO.java
        JSONObject new_data = this.dao.fetchMovie(Id);
        try{
            if(new_data.has("code")){
                get_res = new_data.getInt("code");
                //return the code in the JSONObject
                return get_res;
            }
            return 500;
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }

    }

    //return the response extracted from JSONObject
    public int get_Rel_Res(JSONObject deserialized){
        String aid, mid;
        int get_res;
        try{
            if(deserialized.has("movieId") && deserialized.has("actorId")){
                mid = deserialized.getString("movieId");
                aid = deserialized.getString("actorId");
            } else{
                //we don't have the required information
                return 400;
            }
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }
        //get the JSONObject from Neo4jDAO.java
        JSONObject new_data = this.dao.checkRelationship(aid, mid);
        try{
            if(new_data.has("code")){
                get_res = new_data.getInt("code");
                //return the code in the JSONObject
                return get_res;
            }
            return 500;
        } catch (Exception e){
            //can't read the required string from deserialized
            e.printStackTrace();
            return 500;
        }

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
                case "/api/v1/addRelationship":
                    api_response = this.addRelationship(deserialized);
                    exchange.sendResponseHeaders(api_response, -1);
                    break;

            }

        } catch (Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException, JSONException{
        String body = Utils.convert(exchange.getRequestBody());
        String path = exchange.getRequestURI().getPath();
        int api_response;
        try {
            JSONObject deserialized = new JSONObject(body);

            switch(path){
                //distinguish the path
                case "/api/v1/getActor":
                    api_response = this.get_Actor_Res(deserialized);
                    if(api_response == 200) {
                        JSONObject actor_data = this.dao.fetchActor(deserialized.getString("actorId"));
                        actor_data.remove("code");
                        //send back the JSONObject
                        exchange.sendResponseHeaders(api_response, actor_data.toString().length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(actor_data.toString().getBytes());
                        os.close();
                    } else{
                        exchange.sendResponseHeaders(api_response, -1);
                    }
                    break;
                case "/api/v1/getMovie":
                    api_response = this.get_Movie_Res(deserialized);
                    if(api_response == 200){
                        JSONObject movie_data = this.dao.fetchMovie(deserialized.getString("movieId"));
                        movie_data.remove("code");
                        //send back the JSONObject
                        exchange.sendResponseHeaders(api_response, movie_data.toString().length());
                        OutputStream os= exchange.getResponseBody();
                        os.write(movie_data.toString().getBytes());
                        os.close();
                    } else{
                        exchange.sendResponseHeaders(api_response, -1);
                    }
                    break;
                case "/api/v1/hasRelationship":
                    api_response = this.get_Rel_Res(deserialized);
                    if(api_response == 200){
                        JSONObject relationship_data = this.dao.checkRelationship(deserialized.getString("actorId"),deserialized.getString("movieId"));
                        relationship_data.remove("code");
                        //send back the JSONObject
                        exchange.sendResponseHeaders(api_response, relationship_data.toString().length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(relationship_data.toString().getBytes());
                        os.close();
                    } else{
                        exchange.sendResponseHeaders(api_response, -1);
                    }
                    break;
                case "/api/v1/computeBaconNumber":
                    try{
                        if(deserialized.has("actorId")){
                            JSONObject req_data = this.dao.fetchBaconNumber(deserialized.getString("actorId"));
                            api_response = req_data.getInt("code");
                            if(api_response == 200){
                                req_data.remove("code");
                                //send back the JSONObject
                                exchange.sendResponseHeaders(200, req_data.toString().length());
                                OutputStream os = exchange.getResponseBody();
                                os.write(req_data.toString().getBytes());
                                os.close();
                            } else{
                                exchange.sendResponseHeaders(api_response,-1);
                            }
                        } else{
                            exchange.sendResponseHeaders(400,-1);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500,-1);
                    }
                case "/api/v1/computeBaconPath":
                    try{
                        if(deserialized.has("actorId")){
                            JSONObject req_data = this.dao.fetchBaconPath(deserialized.getString("actorId"));
                            api_response = req_data.getInt("code");
                            if(api_response == 200){
                                req_data.remove("code");
                                //send back the JSONObject
                                exchange.sendResponseHeaders(200, req_data.toString().length());
                                OutputStream os = exchange.getResponseBody();
                                os.write(req_data.toString().getBytes());
                                os.close();
                            } else{
                                exchange.sendResponseHeaders(api_response,-1);
                            }
                        } else{
                            exchange.sendResponseHeaders(500,-1);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500,-1);
                    }
            }

        } catch (Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }

    }

}