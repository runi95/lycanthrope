package spaceworms.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;

public class JSONWrapperModel<E> {

    Logger logger = LoggerFactory.getLogger(JSONWrapperModel.class);

    public Optional<E> readJSONValueFromStreamAndHandleErrors(InputStream inputStream, TypeReference<E> typeReference, int responseCode) {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return this.readJSONValueFromStream(inputStream, typeReference);
        } else {
            JSONWrapperModel<Error> jsonWrapperModel = new JSONWrapperModel<>();
            Optional<Error> optionalError = jsonWrapperModel.readJSONValueFromStream(inputStream, new TypeReference<Error>() {});

            // For now we simply print errors we get from the API
            if (optionalError.isPresent()) {
                logError(optionalError.get());
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

    private void logError(Error error) {
        logger.error(error.getMessage());
    }
}
