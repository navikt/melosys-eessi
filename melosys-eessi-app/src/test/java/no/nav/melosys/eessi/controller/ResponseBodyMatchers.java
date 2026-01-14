package no.nav.melosys.eessi.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ResponseBodyMatchers {

    private final JsonMapper jsonMapper;

    private ResponseBodyMatchers(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public <T> ResultMatcher containsObjectAsJson(
        Object expectedObject,
        Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            T actualObject = jsonMapper.readValue(json, targetClass);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        };
    }

    public ResultMatcher containsError(
        String expectedFieldName,
        String expectedMessage) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            Map<String, String> errorMap = jsonMapper.readValue(json, new TypeReference<HashMap<String, String>>() {
            });
            assertThat(errorMap.get(expectedFieldName))
                .isNotNull()
                .isEqualTo(expectedMessage);
        };
    }

    static ResponseBodyMatchers responseBody(JsonMapper jsonMapper) {
        return new ResponseBodyMatchers(jsonMapper);
    }

}
