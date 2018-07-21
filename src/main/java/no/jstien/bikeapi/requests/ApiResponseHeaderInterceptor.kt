package no.jstien.bikeapi.requests

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ApiResponseHeaderInterceptor : ResponseBodyAdvice<Any> {
    override fun supports(methodParameter: MethodParameter, aClass: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?,
                                 methodParameter: MethodParameter,
                                 mediaType: MediaType,
                                 aClass: Class<out HttpMessageConverter<*>>,
                                 serverHttpRequest: ServerHttpRequest,
                                 serverHttpResponse: ServerHttpResponse): Any? {
        // Allows calls to all of our APIs directly from Ajax
        serverHttpResponse.headers.add("Access-Control-Allow-Origin", "*")
        return body
    }
}
