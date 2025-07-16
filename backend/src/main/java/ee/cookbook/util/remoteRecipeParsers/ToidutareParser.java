package ee.cookbook.util.remoteRecipeParsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import ee.cookbook.model.ParsedRemoteRecipe;

public class ToidutareParser implements RecipeParserInterface {
    public ParsedRemoteRecipe parse(String html) {
        var result = new ParsedRemoteRecipe();
        result.name = parseTitle(html);
        result.amount = parseAmount(html);
        result.prepareTime = parsePrepareTime(html);
        result.instructions = parseIngredients(html) + "\n" + parseInstructions(html);

        return result;
    }

    private static String parseTitle(String html) {
        Pattern pattern = Pattern.compile("<h1.*?>(.*?)<");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return HtmlUtils.htmlUnescape(matcher.group(1).trim());
        }
        return null;
    }

    private static String parseAmount(String html) {
        Pattern pattern = Pattern.compile("<div[^>]*class=\"recipe-portions-quantity\".*?>(.*?)<");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return HtmlUtils.htmlUnescape(matcher.group(1).trim());
        }
        return null;
    }

    private static String parsePrepareTime(String html) {
        Pattern pattern = Pattern.compile("Valmistamise aeg.*?<div>(.*?)<", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return HtmlUtils.htmlUnescape(matcher.group(1).trim());
        }
        return null;
    }

    private static String parseIngredients(String html) {
        StringBuilder result = new StringBuilder();
        Pattern listPattern = Pattern.compile("<div class=\"recipe-ingredients-container\"><div.*?>(.*?)<\\/div>(.*?)<\\/div>", Pattern.DOTALL);
        Matcher listMatcher = listPattern.matcher(html);
        while (listMatcher.find()) {
          String listName = listMatcher.group(1);
          if (listName == null || listName.isBlank()) {
            listName = "Koostis:";
          }
          listName = listName.trim();
          if (!listName.endsWith(":")) {
            listName += ":";
          }
          result.append(listName).append("\n");
          String ingredients = listMatcher.group(2);
          Pattern ingredientsPattern = Pattern.compile("<li>(.*?)<\\/li>", Pattern.DOTALL);
          Matcher ingredientsMatcher = ingredientsPattern.matcher(ingredients);
          while (ingredientsMatcher.find()) {
            String ingredientLine = ingredientsMatcher.group(1);
            ingredientLine = ingredientLine.replaceAll("<.*?>", "");
            ingredientLine = ingredientLine.replaceAll("&nbsp;", " ");
            ingredientLine = ingredientLine.replaceAll("\\s{1,}", " ");
            ingredientLine = ingredientLine.trim();
            result.append(ingredientLine).append("\n");
          }
        }

        return result.toString();
    }

    private static String parseInstructions(String html) {
        Pattern divPattern = Pattern.compile("<div class=\"recipe-steps-header\">.*?<ol>(.*?)<\\/ol>", Pattern.DOTALL);
        Matcher divMatcher = divPattern.matcher(html);
        StringBuilder result = new StringBuilder();
        if (divMatcher.find()) {
            String divContent = divMatcher.group(1);
            String instructions = divContent
                .replaceAll("</li>", "\n\n")
                .replaceAll("<.*?>", "") // Remove HTML tags
                .replaceAll("&nbsp;", " ")
                .replaceAll("[ \\r\\t\\f]+", " ")
                .trim();
            if (!instructions.isEmpty()) {
              result.append(instructions).append("\n");
            }
          
        }
        return HtmlUtils.htmlUnescape(result.toString());
    }
}
