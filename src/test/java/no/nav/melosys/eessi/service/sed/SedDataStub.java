package no.nav.melosys.eessi.service.sed;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.SedDataDto;

public class SedDataStub {

    public static SedDataDto getStub() throws IOException, URISyntaxException {
        URI søknadURI = (SedDataStub.class.getClassLoader().getResource("mock/sedDataDtoStub.json")).toURI();
        String json = new String(Files.readAllBytes(Paths.get(søknadURI)));
        return new ObjectMapper().readValue(json, SedDataDto.class);
    }
}
