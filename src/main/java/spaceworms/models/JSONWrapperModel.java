package spaceworms.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JSONWrapperModel<E> {
    public E readJSONValueFromStream(InputStream inputStream) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<E> typeReference = new TypeReference<E>() {};

        E parsedJSONValue;
        try {
            parsedJSONValue = objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            System.err.println("Could not parse JSON object, please see the stacktrace below for more information.");
            e.printStackTrace();

            return null;
        }

        return parsedJSONValue;
    }
}
