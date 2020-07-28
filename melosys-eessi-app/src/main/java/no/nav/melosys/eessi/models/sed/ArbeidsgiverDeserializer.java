package no.nav.melosys.eessi.models.sed;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;

public class ArbeidsgiverDeserializer extends JsonDeserializer<List<Arbeidsgiver>> {

    private static final TypeReference<List<Arbeidsgiver >> listTypeReference = new TypeReference<>(){};

    @Override
    public List<Arbeidsgiver> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectmapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = objectmapper.readTree(jsonParser);

        if (jsonNode.isArray()) {
            return objectmapper.readerFor(listTypeReference).readValue(jsonNode);
        } else {
            Arbeidsgiver arbeidsgiver = objectmapper.treeToValue(jsonNode, Arbeidsgiver.class);
            return Collections.singletonList(arbeidsgiver);
        }
    }
}
