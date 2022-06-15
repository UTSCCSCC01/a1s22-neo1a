package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;

public class Server {
    private HttpServer httpServer;
    private ReqHandler reqHandler;
    // TODO Complete This Class

    @Inject
    public Server(HttpServer httpServer) {
        this.httpServer = httpServer;
        this.reqHandler = DaggerReqHandlerComponent.create().buildHandler();
    }

    public void createContext(String path) {
        this.httpServer.createContext(path, this.reqHandler);
    }

    public void start() {
        this.httpServer.start();
    }
}
