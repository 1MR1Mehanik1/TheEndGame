package gcapi_model_animation.api.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.IOException;

public enum FormatVersion {
    VERSION_1_12_0, VERSION_1_14_0;

    @JsonValue public String toValue() {
        return switch (this) {
            case VERSION_1_12_0 -> "1.12.0";
            case VERSION_1_14_0 -> "1.14.0";
        };
    }

    @JsonCreator public static FormatVersion forValue(String value) throws IOException {
        if ("1.12.0".equals(value)) return VERSION_1_12_0;
        if ("1.14.0".equals(value)) return VERSION_1_14_0;
        System.getLogger("gc_api").log(System.Logger.Level.WARNING, "Unknown geometry format version: {0}. Defaulting to 1.12.0.", value);
        return VERSION_1_12_0;
    }
}
