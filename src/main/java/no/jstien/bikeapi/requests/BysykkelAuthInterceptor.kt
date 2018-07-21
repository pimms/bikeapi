package no.jstien.bikeapi.requests

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

import java.io.IOException

class BysykkelAuthInterceptor(private val clientIdentifier: String) : ClientHttpRequestInterceptor {

    @Throws(IOException::class)
    override fun intercept(request: HttpRequest,
                           body: ByteArray,
                           execution: ClientHttpRequestExecution): ClientHttpResponse {
        val host = request.uri.host
        if (host == "oslobysykkel.no") {
            val headers = request.headers
            headers.add("Client-Identifier", clientIdentifier)
            LOG.info("Intercepting HTTP request: {}", headers)
        }
        return execution.execute(request, body)
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}
