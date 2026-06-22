package id.web.saka.fountation.authorization.util.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;

@ReadingConverter
public class JsonNodeReadConverter implements Converter<Object, JsonNode> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JsonNode convert(Object source) {
        try {
            // Menangani pembacaan dari DB baik berupa String, byte[], atau Json
            if (source instanceof String) {
                return objectMapper.readTree((String) source);
            } else if (source instanceof byte[]) {
                return objectMapper.readTree((byte[]) source);
            } else if (source instanceof Json json) {
                return objectMapper.readTree(json.asString());
            }
            return objectMapper.valueToTree(source);
        } catch (IOException e) {
            throw new IllegalArgumentException("Gagal mengonversi DB value ke JsonNode", e);
        }
    }

}
