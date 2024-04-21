package de.atruvia.ase.samman.buli.springframework;

import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

import org.springframework.http.HttpRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
public class ResponseFromResourcesSupplier implements Function<HttpRequest, String> {

	private final Function<String[], String> function;

	public static ResponseFromResourcesSupplier responseFromResources(Function<String[], String> function) {
		return new ResponseFromResourcesSupplier(function);
	}

	@Override
	public String apply(HttpRequest request) {
		try {
			return readString(new File(resource(request).toURI()).toPath());
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private URL resource(HttpRequest request) {
		String name = getName(request);
		return requireNonNull(getClass().getClassLoader().getResource(name), () -> "No response captured for " + name);
	}

	private String getName(HttpRequest request) {
		String uri = request.getURI().toASCIIString();
		return requireNonNull(function.apply(uri.split("/")), () -> "Could not resolve" + uri + " to file");
	}

}
