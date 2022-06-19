package ca.utoronto.utm.mcs;


import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;

import java.io.IOException;
import java.net.InetSocketAddress;


@Module
public class ServerModule {
    // TODO Complete This Module
    private final String uriDb;
    private final int port;

    public ServerModule(String uriDb, int port) {
        this.uriDb = uriDb;
        this.port = port;
    }

    @Provides
    public HttpServer provideHttpServer() {
        try {
            return HttpServer.create(new InetSocketAddress(port), 0);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Provides
    public String provideString() {
        return this.uriDb;
    }
}
