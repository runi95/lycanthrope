package spaceworms.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spaceworms.models.Board;
import spaceworms.models.Error;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

@Service
public class API {

    private static final String USER_AGENT = "Mozilla/5.0";

    @Value("${api.url}")
    private String API_URL;

    public List<Board> getBoards() {
        Pair<InputStream, Integer> response = getInputStreamForAPIEndpoint(API_URL + "/boards");
        if (response == null) {
            return null;
        }

        List<Board> boards = null;
        if (response.getValue() == HttpURLConnection.HTTP_OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Board>> typeReference = new TypeReference<List<Board>>() {};

            try {
                boards = objectMapper.readValue(response.getKey(), typeReference);
            } catch (IOException e) {
                System.err.println("Could not parse JSON object, please see the stacktrace below for more information.");
                e.printStackTrace();

                return null;
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Error> typeReference = new TypeReference<Error>() {};

            try {
                Error error = objectMapper.readValue(response.getKey(), typeReference);
                printError(error);
            } catch (IOException e) {
                System.err.println("Could not parse JSON object, please see the stacktrace below for more information.");
                e.printStackTrace();

                return null;
            }
        }

        try {
            response.getKey().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boards;
    }

    private static Pair<InputStream, Integer> getInputStreamForAPIEndpoint(String apiEndpointURL) {
        URL url;
        try {
            url = new URL(apiEndpointURL);
        } catch (MalformedURLException e) {
            System.err.println("API_URL is malformed, please check that your application.properties contains the field api.url and that the value is a proper API URL.");

            return null;
        }

        HttpURLConnection httpURLConnection;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.err.printf("Could not open a connection stream to '%s', please see the stacktrace below for more information.\n", apiEndpointURL);
            e.printStackTrace();

            return null;
        }

        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        try {
            httpURLConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        int responseCode = 0;
        try {
            responseCode = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(inputStream, responseCode);
    }

    private static void printError(Error error) {
        System.err.printf("%s\t Error(%d): %s\n", error.getLevel(), error.getCode(), error.getMessage());
    }

    public static void main(String[] args) {
        API api = new API();
        List<Board> boards = api.getBoards();
        boards.forEach((board) -> System.out.printf("%s[%d]: %s\n", board.getName(), board.getId(), board.getDescription()));

        System.out.println(boards);
    }
}
