package spaceworms.services;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spaceworms.models.Board;
import spaceworms.models.JSONWrapperModel;
import spaceworms.models.Square;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class APIService {

    private static final String USER_AGENT = "Mozilla/5.0";

    @Value("${api.url}")
    private String API_URL;

    public Optional<List<Board>> getBoards(String filter) {
        Pair<InputStream, Integer> response = getInputStreamForAPIEndpoint(API_URL + "/boards" + (filter != null && filter.length() > 0 ? "?" + filter : ""));
        if (response == null) {
            return null;
        }

        JSONWrapperModel<List<Board>> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<List<Board>> optionalBoards = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), response.getValue());

        try {
            response.getKey().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return optionalBoards;
    }

    public Optional<Board> getBoard(int boardId) {
        Pair<InputStream, Integer> response = getInputStreamForAPIEndpoint(API_URL + "/boards/" + boardId);
        if (response == null) {
            return null;
        }

        JSONWrapperModel<Board> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<Board> optionalBoard = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), response.getValue());

        try {
            response.getKey().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return optionalBoard;
    }

    public Optional<Square> getSquare(int boardId, int squareNumber) {
        Pair<InputStream, Integer> response = getInputStreamForAPIEndpoint(API_URL + "/boards/" + boardId + "/" + squareNumber);
        if (response == null) {
            return null;
        }

        JSONWrapperModel<Square> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<Square> optionalSquare = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), response.getValue());

        try {
            response.getKey().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return optionalSquare;
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
}
