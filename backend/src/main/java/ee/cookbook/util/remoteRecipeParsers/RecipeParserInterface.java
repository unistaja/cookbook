package ee.cookbook.util.remoteRecipeParsers;

import ee.cookbook.model.ParsedRemoteRecipe;

public interface RecipeParserInterface {
    ParsedRemoteRecipe parse(String html);
}
