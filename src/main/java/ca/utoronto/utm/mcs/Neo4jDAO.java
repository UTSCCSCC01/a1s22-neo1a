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


}

