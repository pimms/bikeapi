package no.jstien.bikeapi.requests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HeaderInterceptor implements ClientHttpRequestInterceptor {
    private static Logger LOG = LogManager.getLogger();

    private String clientIdentifier;

    public HeaderInterceptor(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add("Client-Identifier", clientIdentifier);
        LOG.info("Intercepting HTTP request: {}", headers);
        return execution.execute(request, body);
    }
}
