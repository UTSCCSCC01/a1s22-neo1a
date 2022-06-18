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

    public ServerModule(String uriDb) {
        this.uriDb = uriDb;
    }

    @Provides
    public HttpServer provideHttpServer() {
        try {
            return HttpServer.create(new InetSocketAddress(App.port), 0);
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
