package spaceworms.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;

public class JSONWrapperModel<E> {

    // TODO: Figure out why Jackson didn't like it when I created the typeReference inside this method
    public Optional<E> readJSONValueFromStreamAndHandleErrors(InputStream inputStream, TypeReference<E> typeReference, int responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return this.readJSONValueFromStream(inputStream, typeReference);
        } else {
            JSONWrapperModel<Error> jsonWrapperModel = new JSONWrapperModel<>();
            Optional<Error> optionalError = jsonWrapperModel.readJSONValueFromStream(inputStream, new TypeReference<Error>() {});

            // For now we simply print errors we get from the API
            if (optionalError.isPresent()) {
                printError(optionalError.get());
            }

            return Optional.empty();
        }
    }

    public Optional<E> readJSONValueFromStream(InputStream inputStream, TypeReference<E> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();

        E parsedJSONValue;
        try {
            parsedJSONValue = objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            System.err.println("Could not parse JSON object, please see the stacktrace below for more information.");
            e.printStackTrace();

            return Optional.empty();
        }

        return Optional.of(parsedJSONValue);
    }

    // TODO: Change this method from print to log
    private static void printError(Error error) {
        System.err.printf("%s\t Error(%d): %s\n", error.getLevel(), error.getCode(), error.getMessage());
    }
}
