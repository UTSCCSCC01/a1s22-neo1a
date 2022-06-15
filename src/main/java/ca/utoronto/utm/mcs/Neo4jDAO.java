package ca.utoronto.utm.mcs;

import org.neo4j.driver.Driver;

import javax.inject.Inject;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO {
    public Driver driver;

    // TODO Complete This Class
    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }
}
