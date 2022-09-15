package dk.kvistborg.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentDisarmServiceImpl implements DocumentDisarmService {

    private final WebClient webClient;

    @Value("${application.path}")

    private String path;

    @Value("${application.token}")
    private String token;

    @Value("${application.properties}")
    private String properties;
    @Override
    public Mono<String> disarmDocument(Map<String, Part> filePart) {

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", filePart.get("file"));
        multipartBodyBuilder.part("properties", properties);
        return webClient.post()
                .uri(path)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .header(AUTHORIZATION, token)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(String.class);
    }
}
