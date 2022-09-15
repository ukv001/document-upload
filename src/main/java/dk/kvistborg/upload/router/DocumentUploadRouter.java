package dk.kvistborg.upload.router;

import dk.kvistborg.upload.handler.DocumentUploadHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class DocumentUploadRouter {

    @Bean
    public RouterFunction<ServerResponse> rote(DocumentUploadHandler handler) {
        return RouterFunctions.route(POST("/").and(contentType(MediaType.MULTIPART_FORM_DATA)), handler::uploadDocument);
    }

}
