package com.azathorpe.utils.cfetcher;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Message;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CFSender {
    /**
     * The base URL for the Codeforces API. All API requests should be made to this URL.
     */
    protected static final String BASE_URL = "https://codeforces.com/api/";

    protected static HttpClient client = HttpClient.newHttpClient();

    protected static Message sendRequest(String contact) throws IOException, InterruptedException {
        String url = BASE_URL + contact;

        if(client == null){
            // init client
            client = HttpClient.newHttpClient();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return JSON.parseObject(response.body(), Message.class);
    }

    protected static Message queryProblems() throws IOException, InterruptedException {
        return sendRequest("problemset.problems");
    }

    protected static Message queryStatus(String handle) throws IOException, InterruptedException {
        return sendRequest("user.status?handle=" + handle);

    }
}
