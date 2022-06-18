package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.*;


import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    private static final int port = 8086;


    //Start the backend server. Run at the beginning of every single test method.
    @BeforeAll
    public static void startServer() {

        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");



        ServerComponent serverComponent = DaggerServerComponent.builder().serverModule(new ServerModule(addr, port)).build();
        Server server = serverComponent.buildServer();
        server.createContext("/api/v1/");
        server.start();

        System.out.printf("Server started on port %d\n", port);

        System.out.println(addr);
    }

    //Send a http request and get the http response.
    public HttpResponse httpRequest(String method, String endpoint, String body) {
        try {
            URI uri = new URI("http://127.0.0.1:8086" + endpoint);
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).method(method, HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return httpResponse;


        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Test
    @Order(1)
    void addActorPass() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Alexander Hamilton");
            jsonObject1.put("actorId", "nm10010110");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addActor", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("name", "George Washington");
            jsonObject2.put("actorId", "nm10010111");
            HttpResponse<String> httpResponse2 = httpRequest("PUT", "/api/v1/addActor", jsonObject2.toString());
            assertEquals(200, httpResponse2.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(2)
    void addActorFail() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Jeremy Clarkson");
            jsonObject1.put("actorId", "nm10010111");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addActor", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(3)
    void addMoviePass() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Tenki No Ko");
            jsonObject1.put("movieId", "nm1111111");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addMovie", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("name", "Kimo No Namae");
            jsonObject2.put("movieId", "nm1111110");
            HttpResponse<String> httpResponse2 = httpRequest("PUT", "/api/v1/addMovie", jsonObject2.toString());
            assertEquals(200, httpResponse2.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(4)
    void addMovieFail() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Kimo No Namae");
            jsonObject1.put("movieId", "nm1111110");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addMovie", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(5)
    void addRelationshipPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("actorId", "nm10010110");
            jsonObject1.put("movieId", "nm1111111");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addRelationship", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(6)
    void addRelationshipFail() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("actorId", "nm10010110");
            jsonObject1.put("movieId", "nm1111111");
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/api/v1/addRelationship", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("actorId", "nm10010110");
            jsonObject2.put("movieId", "nm8888888");
            HttpResponse<String> httpResponse2 = httpRequest("PUT", "/api/v1/addRelationship", jsonObject2.toString());
            assertEquals(404, httpResponse2.statusCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }










}
