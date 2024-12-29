package ee.cookbook.util.remoteRecipeParsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import ee.cookbook.model.ParsedRemoteRecipe;

public class NamiNamiParser {
  public static ParsedRemoteRecipe parse(String html) {
    var result = new ParsedRemoteRecipe();
    result.name = parseTitle(html);
    result.amount = parseAmount(html);
    result.prepareTime = parsePrepareTime(html);
    result.instructions = parseIngredients(html) + "\n" + parseInstructions(html);

    return result;
  }

  private static String parseTitle(String html) {
    Pattern pattern = Pattern.compile("<h1.*?>(.*)<\\/h1>");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private static String parseAmount(String html) {
    Pattern pattern = Pattern.compile("val-serv.*?>(.*)<\\/div>");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private static String parsePrepareTime(String html) {
    Pattern pattern = Pattern.compile("val-time.*?>(.*)<\\/div>");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private static String parseIngredients(String html) {
    String result = "";
    Pattern ingredientsSectionPattern = Pattern.compile("<section class=\"block text-center\">(.*?)<\\/section>", Pattern.DOTALL);
    Matcher ingredientsSectionMatcher = ingredientsSectionPattern.matcher(html);
    if (ingredientsSectionMatcher.find()) {
      String section = ingredientsSectionMatcher.group(1);
      Pattern listPattern = Pattern.compile("<u>(.*?)<\\/u><\\/strong>(.*?)(?:<strong|$)", Pattern.DOTALL);
      Matcher listMatcher = listPattern.matcher(section);
      while (listMatcher.find()) {
        String listName = listMatcher.group(1);
        if (listName == null || listName.isBlank()) {
          listName = "Koostis:";
        }
        listName = listName.trim();
        if (!listName.endsWith(":")) {
          listName += ":";
        }
        result += listName + "\n";
        String ingredients = listMatcher.group(2);
        Pattern ingredientsPattern = Pattern.compile("<p>\\s*(.*?)\\s*<\\/p>", Pattern.DOTALL);
        Matcher ingredientsMatcher = ingredientsPattern.matcher(ingredients);
        while (ingredientsMatcher.find()) {
          String ingredientLine = ingredientsMatcher.group(1);
          ingredientLine = ingredientLine.replaceAll("<a.*?>", "");
          ingredientLine = ingredientLine.replaceAll("<\\/a>", "");
          ingredientLine = ingredientLine.replaceAll("\s{1,}", " ");
          ingredientLine = ingredientLine.trim();
          result += ingredientLine + "\n";
        }
      }

    }
    return result;
  }

  private static String parseInstructions(String html) {
    Pattern pattern = Pattern.compile("<section class=\"block\">\\s*(.*?)<div", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      String instructions = matcher.group(1);
      instructions = instructions.replaceAll("\\t<li>", "- ");
      instructions = instructions.replaceAll("<ul>\n", "");
      instructions = instructions.replaceAll("<.*?>", "");
      instructions = instructions.replaceAll("\r", "");
      instructions = HtmlUtils.htmlUnescape(instructions);
      return instructions;
    }
    return "";
  }


  
}
