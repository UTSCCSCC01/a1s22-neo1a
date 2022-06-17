package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;


import javax.inject.Inject;

import java.io.IOException;

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
                if (label.equals("Actor")) {
                    Result result = tx.run("MATCH q:$label WHERE q.actorId = $id", parameters("label", label, "id", id));
                    if (result.hasNext()) {
                        exist = true;
                    }
                }else if (label.equals("Movie")) {
                    Result result = tx.run("MATCH q:$label WHERE q.movieId = $id", parameters("label", label, "id", id));
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
                Result result = tx.run("MATCH (a:Actor), (m:Movie) WHERE a.actorId = $actorId AND m.movieId = $movieId AND (a)-[:ACTED_IN]->(m)", parameters("actorId", actorId, "movieId", movieId));
                if (result.hasNext()){
                    exist = true;
                }
                return exist;
            }

        }

    }

    //Insert an actor
    public int insertActor(String name, String actorId) {
        if (exists("Actor", actorId)) {
            return 400;
        }
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("CREATE (a:Actor {name: $name, actorId: $actorId})", parameters("name", name, "actorId", actorId)));
            session.close();
            return 200;
        } catch (Exception e) {
            return 500;
        }
    }

    //Insert a movie
    public int insertMovie(String name, String movieId) {
        if (exists("Movie", movieId)) {
            return 400;
        }
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("CREATE (a:Movie {name: $name, movieId: $movieId})", parameters("name", name, "movieId", movieId)));
            session.close();
            return 200;
        } catch (Exception e) {
            return 500;
        }
    }

    //add relationship
    public int addRelationship(String actorId, String movieId){
        //check if the actor or movie exist.
        if(!exists("Actor", actorId)){
            return 404;
        }
        else if(!exists("Movie", movieId)){
            return 404;
        }
        //check whether the relationship exist
        if(exists_relationship(actorId, movieId)){
            return 400;
        }
        //add relationship
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MATCH (a:Actor), (m:Movie) WHERE a.actorId = $actorId AND m.movieId = $movieId CREATE (a)-[:ACTED_IN]->(m)",parameters("actorId", actorId, "movieId", movieId)));
            session.close();
            return 200;
        } catch (Exception e){
            return 500;
        }

    }

}
