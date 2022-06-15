package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.InetSocketAddress;



public class App
{
    static int port = 8080;

    public static void main(String[] args) throws IOException
    {
        // TODO Create Your Server Context Here, There Should Only Be One Context
        ServerComponent serverComponent = DaggerServerComponent.create();
        Server server = serverComponent.buildServer();
        server.createContext("/api/v1/");
        server.start();

        System.out.printf("Server started on port %d\n", port);

        // This code is used to get the neo4j address, you must use this so that we can mark :)
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        System.out.println(addr);
    }
}
