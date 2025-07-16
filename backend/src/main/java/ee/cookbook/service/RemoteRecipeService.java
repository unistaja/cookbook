package ee.cookbook.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ee.cookbook.model.ParsedRemoteRecipe;
import ee.cookbook.util.remoteRecipeParsers.NamiNamiParser;
import ee.cookbook.util.remoteRecipeParsers.RecipeParserInterface;
import ee.cookbook.util.remoteRecipeParsers.ToidutareParser;

@Service
public class RemoteRecipeService {
  private final static Logger logger = LoggerFactory.getLogger(RemoteRecipeService.class);

  public ParsedRemoteRecipe parseRecipeFromUrl(String url) {
    if (url == null || url.isBlank()) {
      logger.warn("Received empty URL for remote recipe parsing");
      return null;
    }
    RecipeParserInterface parser;
    if (url.startsWith("https://nami-nami.ee/")) {
      parser = new NamiNamiParser();
    } else if (url.startsWith("https://www.ohtuleht.ee/toidutare/")) {
      parser = new ToidutareParser();
    } else {
      return null;
    }
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();

    try {
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
      var responseBody = response.body();
      return parser.parse(responseBody);
    } catch (Exception e) {
      logger.error("Failed to fetch remote recipe", e);
    }
    return null;
  }
  
}
