package dk.kvistborg.upload.handler;

import dk.kvistborg.upload.service.DocumentDisarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentUploadHandler {

    private static final String FILE_FIELD_VALUE = "file";

    private final DocumentDisarmService documentDisarmService;
    public Mono<ServerResponse> uploadDocument(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMultipartData()).flatMap(parts -> {
            if (parts.get(FILE_FIELD_VALUE) == null) {
                return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
            }

            if (parts.get(FILE_FIELD_VALUE).size() > 1) {
                return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
            }

            Map<String, Part> partMap = parts.toSingleValueMap();
            Part filePart = partMap.get(FILE_FIELD_VALUE);
            /**It seems that if the header is not there for the part, then it will fail in the body extractor.*/
            if (!filePart.headers().get("Content-Disposition").get(0).contains("filename")) {
                     return  Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
            }
            return ok().body(documentDisarmService.disarmDocument(partMap), String.class);
        });
    }
}
