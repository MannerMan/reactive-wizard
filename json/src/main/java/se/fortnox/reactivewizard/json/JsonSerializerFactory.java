package se.fortnox.reactivewizard.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Creates instances of JSON serializers.
 */
public class JsonSerializerFactory {
    private final ObjectMapper mapper;

    @Inject
    public JsonSerializerFactory(ObjectMapper mapper) {
        this.mapper = mapper.setSerializerFactory(mapper.getSerializerFactory()
            .withSerializerModifier(new LambdaSerializerModifier()));
    }

    public JsonSerializerFactory() {
        this(JsonMapper.builder()
            .findAndAddModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .defaultDateFormat(new StdDateFormat().withColonInTimeZone(false)) // Preserve behavior prior to Jackson 2.11
            .build());
    }

    public <T> Function<T, String> createStringSerializer(TypeReference<T> paramType) {
        return createStringSerializer(mapper.writerFor(paramType));
    }

    public <T> Function<T, String> createStringSerializer(Class<T> paramType) {
        return createStringSerializer(mapper.writerFor(paramType));
    }

    public <T> Function<T, String> createStringSerializer(Type type) {
        return createStringSerializer(Types.toReference(type));
    }

    private <T> Function<T, String> createStringSerializer(ObjectWriter writer) {
        return object -> {
            if (object == null) {
                return null;
            }
            try {
                return writer.writeValueAsString(object);
            } catch (Exception e) {
                throw new InvalidJsonException(e);
            }
        };
    }

    public <T> Function<T, byte[]> createByteSerializer(TypeReference<T> paramType) {
        return createByteSerializer(mapper.writerFor(paramType));
    }

    public <T> Function<T, byte[]> createByteSerializer(Class<T> paramType) {
        return createByteSerializer(mapper.writerFor(paramType));
    }

    private <T> Function<T, byte[]> createByteSerializer(ObjectWriter writer) {
        return object -> {
            if (object == null) {
                return null;
            }
            try {
                return writer.writeValueAsBytes(object);
            } catch (Exception e) {
                throw new InvalidJsonException(e);
            }
        };
    }

}
