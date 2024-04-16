package de.atruvia.ase.samman.buli.springframework;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.function.Function;

import org.springframework.http.HttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestTemplate;

public class RestTemplateMock extends RestTemplate {

	public RestTemplateMock(Function<HttpRequest, String> responseSupplier) {
		getInterceptors().add((req, body, execution) -> {
			MockClientHttpResponse response = new MockClientHttpResponse(responseSupplier.apply(req).getBytes(), OK);
			response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
			return response;
		});
	}

}
