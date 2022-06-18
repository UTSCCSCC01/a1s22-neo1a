package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;

public class Server {
    private final String uriDb;
    private final HttpServer httpServer;
    private final ReqHandler reqHandler;
    // TODO Complete This Class

    @Inject
    public Server(HttpServer httpServer, String uriDb) {
        this.uriDb = uriDb;
        this.httpServer = httpServer;
        this.reqHandler = DaggerReqHandlerComponent.builder().reqHandlerModule(new ReqHandlerModule(this.uriDb)).build().buildHandler();
    }

    public void createContext(String path) {
        this.httpServer.createContext(path, this.reqHandler);
    }

    public void start() {
        this.httpServer.start();
    }
}
