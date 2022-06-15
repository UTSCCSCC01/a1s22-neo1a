package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

@Module
public class ReqHandlerModule {
    // TODO Complete This Module
    private final String uriDb = "bolt://localhost:8086";
    private final String username = "neo4j";
    private final String password = "123456";

    @Provides
    public Neo4jDAO provideNeo4jDAO() {
        return new Neo4jDAO(GraphDatabase.driver(this.uriDb, AuthTokens.basic(this.username, this.password)));
    }
}
