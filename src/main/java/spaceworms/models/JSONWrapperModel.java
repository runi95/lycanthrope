package spaceworms.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;

public class JSONWrapperModel<E> {

    public Optional<E> readJSONValueFromStreamAndHandleErrors(InputStream inputStream, int responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return this.readJSONValueFromStream(inputStream);
        } else {
            JSONWrapperModel<Error> jsonWrapperModel = new JSONWrapperModel<>();
            Optional<Error> optionalError = jsonWrapperModel.readJSONValueFromStream(inputStream);

            // For now we simply print errors we get from the API
            if (optionalError.isPresent()) {
                printError(optionalError.get());
            }

            return Optional.empty();
        }
    }

    public Optional<E> readJSONValueFromStream(InputStream inputStream) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<E> typeReference = new TypeReference<E>() {};

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
