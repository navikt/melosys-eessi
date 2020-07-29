package no.nav.melosys.eessi.models.sed;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;

public class ArbeidsgiverDeserializer extends StdDeserializer<List<Arbeidsgiver>> {

    private static final TypeReference<List<Arbeidsgiver >> LIST_TYPE_REFERENCE = new TypeReference<>(){};

    @SuppressWarnings("unused")
    public ArbeidsgiverDeserializer() {
        this(null);
    }

    private ArbeidsgiverDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public List<Arbeidsgiver> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectmapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = objectmapper.readTree(jsonParser);

        if (jsonNode.isArray()) {
            return objectmapper.readerFor(LIST_TYPE_REFERENCE).readValue(jsonNode);
        } else {
            Arbeidsgiver arbeidsgiver = objectmapper.treeToValue(jsonNode, Arbeidsgiver.class);
            return Collections.singletonList(arbeidsgiver);
        }
    }
}
