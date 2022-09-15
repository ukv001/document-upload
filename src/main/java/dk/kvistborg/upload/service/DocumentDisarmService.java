package dk.kvistborg.upload.service;

import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface DocumentDisarmService {

    Mono<String> disarmDocument(Map<String, Part> filePart);
}
