// To use this code, add the following Maven dependency to your project:
//
//
//     com.fasterxml.jackson.core     : jackson-databind          : 2.9.0
//     com.fasterxml.jackson.datatype : jackson-datatype-jsr310   : 2.9.0
//
// Import this package:
//
//     import software.bernie.geckolib.file.geo.Converter;
//
// Then you can deserialize a JSON string with
//
//     GeoModel data = Converter.fromJsonString(jsonString);

package gcapi_model_animation.api.geo.raw.pojo;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class Converter {
	public static RawGeoModel fromJsonString(String json) throws IOException { return getObjectReader().readValue(json); }
	public static String toJsonString(RawGeoModel obj) throws IOException { return getObjectWriter().writeValueAsString(obj); }

	private static ObjectReader reader;
	private static ObjectWriter writer;

	private static void instantiateMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Disabling unknown variables
		reader = mapper.readerFor(RawGeoModel.class);
		writer = mapper.writerFor(RawGeoModel.class);
	}

	private static ObjectReader getObjectReader() {
		if (reader == null) instantiateMapper();
		return reader;
	}

	private static ObjectWriter getObjectWriter() {
		if (writer == null) instantiateMapper();
		return writer;
	}
}
