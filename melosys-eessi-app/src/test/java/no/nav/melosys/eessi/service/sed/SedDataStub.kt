package no.nav.melosys.eessi.service.sed;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.melosys.eessi.controller.dto.SedDataDto;

public class SedDataStub {

    public static SedDataDto getStub() throws IOException, URISyntaxException {
        URI søknadURI = Objects.requireNonNull(SedDataStub.class.getClassLoader().getResource("mock/sedDataDtoStub.json")).toURI();
        String json = Files.readString(Paths.get(søknadURI));
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, SedDataDto.class);
    }
}
