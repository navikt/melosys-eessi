package no.nav.melosys.eessi.config;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

//Trengs for å mappe binære typer ved multipart/form-data
@Component
public class MultipartJackson2HttpMessageConverter extends JacksonJsonHttpMessageConverter {


    public MultipartJackson2HttpMessageConverter(JsonMapper jsonMapper) {
        super(jsonMapper);
        setSupportedMediaTypes(List.of(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}
