package ca.utoronto.utm.mcs;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;


import javax.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO {
    private Driver driver;

    // TODO Complete This Class
    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }

    //Check if the actorId or movieId already exists in the database
    public boolean exists(String label, String id) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                boolean exist = false;
                if (label.equals("actor")) {
                    Result result = tx.run("MATCH (q: " + label + ") WHERE (q.id = \"" + id + "\") RETURN q");
                    if (result.hasNext()) {
                        exist = true;
                    }
                } else if (label.equals("movie")) {
                    Result result = tx.run("MATCH (q: " + label + ") WHERE (q.id = \"" + id + "\") RETURN q");
                    if (result.hasNext()) {
                        exist = true;
                    }
                }
                return exist;

            }
        }
    }

    //Check if the actor act in movie, assume the actor and movie exist
    public boolean exists_relationship(String actorId, String movieId){
        try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                boolean exist = false;
                Result result = tx.run("MATCH (a:actor), (m:movie) WHERE a.id = \"" + actorId + "\" AND m.id = \"" + movieId + "\" AND (a)-[:ACTED_IN]->(m) RETURN a");
                if (result.hasNext()){
                    exist = true;
                }
                return exist;
            }

        }

    }

    //Insert an actor
    public int insertActor(String name, String actorId) {
        if (exists("actor", actorId)) {
            return 400;
        }
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("CREATE (a:actor {Name: \"" + name + "\", id: \"" + actorId + "\"})"));
            session.close();
            return 200;
        } catch (Exception e) {
            return 500;
        }
    }

    //Insert a movie
    public int insertMovie(String name, String movieId) {
        if (exists("movie", movieId)) {
            return 400;
        }
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("CREATE (a:movie {Name: \"" + name + "\", id: \"" + movieId + "\"})"));
            session.close();
            return 200;
        } catch (Exception e) {
            return 500;
        }
    }

    //Add a relationship
    public int insertRelationship(String actorId, String movieId){
        //check if the actor and the movie exist
        if(!exists("actor", actorId) || !exists("movie", movieId)) {
            return 404;
        }
        //check whether the relationship exists
        if(exists_relationship(actorId, movieId)){
            return 400;
        }
        //add relationship
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MATCH (a:actor), (m:movie) WHERE a.id = \"" + actorId + "\" AND m.id = \"" + movieId + "\" CREATE (a)-[:ACTED_IN]->(m)"));
            session.close();
            return 200;
        } catch (Exception e){
            return 500;
        }

    }

    //Get a list of all movies the actor played in, or all actors the movie involves, depending on the first param type
    public List<String> getRelated(String type, String id) {
        List<String> list = new ArrayList<String>();
        if (type.equals("actor")) {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()){
                    Result result = tx.run("MATCH (a:actor), (m:movie) WHERE a.id = \"" + id + "\" AND (a)-[:ACTED_IN]->(m) RETURN m");
                    for(Record record: result.list()){
                        list.add(record.get(0).get("id").asString());
                    }
                    return list;
                }
            } catch (Exception e){
                return null;
            }
        }else {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()){
                    Result result = tx.run("MATCH (a:actor), (m:movie) WHERE m.id = \"" + id + "\" AND (a)-[:ACTED_IN]->(m) RETURN a");
                    for(Record record: result.list()){
                        list.add(record.get(0).get("id").asString());
                    }
                    return list;
                }
            } catch (Exception e){
                return null;
            }
        }

    }

    //Get actor info. Pack actor info along with status code in a JSONObject and return.
    public JSONObject fetchActor(String actorId) {
        int code;
        String name = new String();
        List<String> list = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        //check if the actor exists
        if(!exists("actor", actorId)) {
            code = 404;
        }else{
            code = 200;
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()){
                    Result result = tx.run("MATCH (a:actor) WHERE a.id = \"" + actorId + "\" RETURN a");
                    Record record = result.single();
                    name = record.get(0).get("Name").asString();
                    list = getRelated("actor", actorId);
                }
            } catch (Exception e){
                e.printStackTrace();
                code = 500;
            }
        }
        try {
            if (code == 200) {
                jsonObject.put("code", code);
                jsonObject.put("actorId", actorId);
                jsonObject.put("name", name);
                jsonObject.put("movies", list);
            }else{
                jsonObject.put("code", code);
            }
            return jsonObject;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    //Get movie info. Pack movie info along with status code in a JSONObject and return.
    public JSONObject fetchMovie(String movieId) {
        int code;
        String name = new String();
        List<String> list = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        //check if the movie exists
        if(!exists("movie", movieId)) {
            code = 404;
        }else{
            code = 200;
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()){
                    Result result = tx.run("MATCH (a:movie) WHERE a.id = \"" + movieId + "\" RETURN a");
                    Record record = result.single();
                    name = record.get(0).get("Name").asString();
                    list = getRelated("movie", movieId);
                }
            } catch (Exception e){
                e.printStackTrace();
                code = 500;
            }
        }
        try {
            if (code == 200) {
                jsonObject.put("code", code);
                jsonObject.put("movieId", movieId);
                jsonObject.put("name", name);
                jsonObject.put("actors", list);
            }else{
                jsonObject.put("code", code);
            }
            return jsonObject;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    //Check if a relationship exists between the two given ids. Pack the boolean value along with status in a JSONObject and return.
    public JSONObject checkRelationship (String actorId, String movieId) {
        int code;
        boolean exist = false;
        JSONObject jsonObject = new JSONObject();
        //check if the actor and the movie exist
        if(!exists("actor", actorId) || !exists("movie", movieId)) {
            code = 404;
        }else{
            code = 200;
            exist = exists_relationship(actorId, movieId);
        }
        try{
            if(code == 200){
                jsonObject.put("code", code);
                jsonObject.put("actorId", actorId);
                jsonObject.put("movieId", movieId);
                jsonObject.put("hasRelationship", exist);
            }else{
                jsonObject.put("code", code);
            }
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get the length of the shortest path from the given actor to Bacon. Pack the length along with status in a JSONObject and return.
    public JSONObject fetchBaconNumber (String actorId) {
        int code;
        int baconNumber = -1;
        JSONObject jsonObject = new JSONObject();
        if (!exists("actor", actorId) || !exists("actor", "nm0000102")) {
            code = 404;
        }else{
            if (actorId.equals("nm0000102")){
                code = 200;
                baconNumber = 0;
            }else {
                try (Session session = driver.session()){
                    try (Transaction tx = session.beginTransaction()){
                        code = 200;
                        Result result = tx.run("MATCH p=shortestPath((b:actor {id:\"nm0000102\"})-[*]-(t:actor {id:\"" + actorId + "\"})) RETURN length(p)");
                        if(result.hasNext()){
                            Record record = result.single();
                            baconNumber = record.get(0).asInt();
                            baconNumber = baconNumber / 2;
                        }else{
                            code = 404;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    code = 500;
                }
            }
        }
        try {
            if(code == 200){
                jsonObject.put("code", code);
                jsonObject.put("baconNumber", baconNumber);
            }else{
                jsonObject.put("code", code);
            }
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    //Get the shortest path from the given actor to Bacon. Pack the path as a list of Strings along with status in a JSONObject and return.
    public JSONObject fetchBaconPath (String actorId) {
        int code;
        String id = new String();
        List<String> list = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        if (!exists("actor", actorId) || !exists("actor", "nm0000102")) {
            code = 404;
        }else{
            if (actorId.equals("nm0000102")){
                code = 200;
                list.add("nm0000102");
            }else {
                try (Session session = driver.session()){
                    try (Transaction tx = session.beginTransaction()){
                        code = 200;
                        Result result = tx.run("MATCH p=shortestPath((b:actor {id:\"" + actorId + "\"})-[*]-(t:actor {id:\"nm0000102\"})) RETURN nodes(p)");
                        if(result.hasNext()){
                            Record record = result.single();
                            int i = 0;
                            while(!record.get(0).get(i).get("id").asString().equals("nm0000102")){
                                list.add(record.get(0).get(i).get("id").asString());
                                i++;
                            }
                            list.add("nm0000102");

                        }else{
                            code = 404;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    code = 500;
                }
            }
        }
        try {
            if(code == 200){
                jsonObject.put("code", code);
                jsonObject.put("baconPath", list);
            }else{
                jsonObject.put("code", code);
            }
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




}

