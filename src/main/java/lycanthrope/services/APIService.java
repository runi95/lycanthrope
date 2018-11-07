package lycanthrope.services;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lycanthrope.models.Board;
import lycanthrope.models.JSONWrapperModel;
import lycanthrope.models.Square;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

@Service
public class APIService {

    private static final String USER_AGENT = "Mozilla/5.0";

    @Value("${api.url}")
    private String API_URL;

    public Optional<List<Board>> getBoards(String filter) {
        Pair<InputStream, Integer> response = getInputStreamForAPIEndpoint(API_URL + "/boards" + (filter != null && filter.length() > 0 ? "?" + filter : ""));
        if (response == null) {
            return Optional.empty();
        }

        TypeReference<List<Board>> typeReference = new TypeReference<List<Board>>() {};
        JSONWrapperModel<List<Board>> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<List<Board>> optionalBoards = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), typeReference, response.getValue());

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
            return Optional.empty();
        }

        TypeReference<Board> typeReference = new TypeReference<Board>() {};
        JSONWrapperModel<Board> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<Board> optionalBoard = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), typeReference, response.getValue());

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
            return Optional.empty();
        }

        TypeReference<Square> typeReference = new TypeReference<Square>() {};
        JSONWrapperModel<Square> jsonWrapperModel = new JSONWrapperModel<>();
        Optional<Square> optionalSquare = jsonWrapperModel.readJSONValueFromStreamAndHandleErrors(response.getKey(), typeReference, response.getValue());

        try {
            response.getKey().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return optionalSquare;
    }

    public Optional<Board> loadBoardAndSquares(int boardId) {
        Optional<Board> optionalBoard = getBoard(boardId);
        if (!optionalBoard.isPresent()) {
            return optionalBoard;
        }

        List<Square> squares = new ArrayList<>();

        for (int i = 0; i < optionalBoard.get().getDimY(); i++) {
            for(int j = 1; j <= optionalBoard.get().getDimX(); j++) {
                Optional<Square> optionalSquare = getSquare(optionalBoard.get().getId(), i * optionalBoard.get().getDimX() + j);

                if (optionalSquare.isPresent()) {
                    if (optionalSquare.get().getWormhole() > 0) {
                        optionalSquare.get().setIsWormhole(true);
                    }

                    optionalSquare.get().setBoard(optionalBoard.get());
                    squares.add(optionalSquare.get());
                }
            }
        }

        optionalBoard.get().setSquares(squares);

        return optionalBoard;
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

            return null;
        }

        int responseCode = 0;
        try {
            responseCode = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        InputStream inputStream;
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

        return new Pair<>(inputStream, responseCode);
    }
}
