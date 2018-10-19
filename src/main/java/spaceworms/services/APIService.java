package spaceworms.services;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spaceworms.models.Board;
import spaceworms.models.Error;
import spaceworms.models.JSONWrapperModel;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

@Service
public class APIService {

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
            JSONWrapperModel<List<Board>> jsonWrapperModel = new JSONWrapperModel<>();
            jsonWrapperModel.readJSONValueFromStream(response.getKey());
        } else {
            JSONWrapperModel<Error> jsonWrapperModel = new JSONWrapperModel<>();
            Error error = jsonWrapperModel.readJSONValueFromStream(response.getKey());

            // For now we simply print errors we get from the API
            printError(error);
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

    // TODO: Change this method from print to log
    private static void printError(Error error) {
        System.err.printf("%s\t Error(%d): %s\n", error.getLevel(), error.getCode(), error.getMessage());
    }

    public static void main(String[] args) {
        APIService api = new APIService();
        List<Board> boards = api.getBoards();
        boards.forEach((board) -> System.out.printf("%s[%d]: %s\n", board.getName(), board.getId(), board.getDescription()));

        System.out.println(boards);
    }
}
