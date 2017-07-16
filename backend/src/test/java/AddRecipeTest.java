import com.codeborne.selenide.Selectors;
import ee.cookbook.model.*;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AddRecipeTest extends BaseSelenideTest {
  private List<Recipe> recipes;
  private int test;
  private List<IngredientLine> deletedLines;
  private List<AlternateIngredientLine> deletedAltLines;
  private List<Category> deletedCategories;
  private int linesPerList;
  private int altLinesPerLine;
  private int categoriesPerRecipe;
  private Recipe addRecipeTestRecipe;
  private Recipe changeRecipeTestRecipe;

  @Test
  public void addingRecipesWorks(){
    addRecipeTest();
    changeRecipeTest();
  }

  //test methods
  private void addRecipeTest() {
    int listsDeletedFrom = 0;
    categoriesPerRecipe = 3;
    linesPerList = 4;
    altLinesPerLine = 2;
    addRecipeTestRecipe = createRecipe(2);
    test=0;
    openAddRecipePage();
    errorsTest();
    addRecipeValues(addRecipeTestRecipe);
    for(IngredientList list:addRecipeTestRecipe.ingredientLists) {
      removeAltLine(listsDeletedFrom, 2, 1, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 2, 0, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 1, 1, list.ingredientLines.get(1));
      listsDeletedFrom++;
    }
    $(By.id("save")).click();
    checkRecipe(addRecipeTestRecipe);
  }
  private void changeRecipeTest() {
    $(By.className("edit-button")).click();
    $(By.id("cancel")).click();
    test = 1;
    categoriesPerRecipe = 4;
    linesPerList = 5;
    altLinesPerLine = 3;
    checkRecipe(addRecipeTestRecipe);
    changeRecipeTestRecipe = createRecipe(3);
    $(By.className("edit-button")).click();
    errorsTest();
    addRecipeValues(changeRecipeTestRecipe);
    int listsDeletedFrom = 0;
    for(IngredientList list:changeRecipeTestRecipe.ingredientLists) {
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 2, 0, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 3, 0, list.ingredientLines.get(3));
      removeAltLine(listsDeletedFrom, 3, 0, list.ingredientLines.get(3));
      listsDeletedFrom++;
    }
    $(By.id("save")).click();
    checkRecipe(changeRecipeTestRecipe);
  }
  private void errorsTest() {
    noTitleTest();
    noInstructionsTest();
  }
  //creating recipe's information
  private Recipe createRecipe(int listAmount) {
    Recipe recipe=new Recipe();
    recipe.instructions=(test+1) + ".Instructions";
    recipe.name=(test+1) + ".Title";
    recipe.source=new RecipeSource((test+1) + ".Source");
    recipe.ingredientLists = new ArrayList<>();
    for (int lists=0; lists<listAmount; lists++) {
      recipe.ingredientLists.add(createRecipeSection(lists));
    }
    recipe.categories=createCategories();
    return recipe;
  }
  private List<Category> createCategories() {
    List<Category> categories = new ArrayList<>();
    for(int category=1; category<=categoriesPerRecipe; category++) {
      categories.add(new Category((test+1)  + ".Category" + (category)));
    }
    return categories;
  }
  private IngredientList createRecipeSection(int lists) {
    IngredientList list = new IngredientList();
    list.ingredientLines = new ArrayList<>();
    for(int lines = 0; lines < linesPerList; lines++) {
      list.ingredientLines.add(createLineValues(lists, lines));
    }
    list.name=new IngredientListName((test+1) + ".List" + (lists+1));
    return list;
  }
  private IngredientLine createLineValues(int list, int lines) {
    IngredientLine line = new IngredientLine();
    line.amount = Double.parseDouble((test+1) + "." +  (list+1) + (lines+1));
    line.unit = new IngredientUnit((test+1) + "." + "List" + (list+1) + "-Line" + (lines+1) + "Unit");
    line.ingredient = new Ingredient((test+1) + "." + "List" + (list+1) + "-Line" + (lines+1) + "Ingredient");
    line.alternateLines = new ArrayList<>();
    for(int altLine = 0; altLine < altLinesPerLine; altLine++) {
      line.alternateLines.add(createAltLineValues(list, lines, altLine));
    }
    return line;
  }
  private AlternateIngredientLine createAltLineValues(int list, int line, int altLineNumber) {
    AlternateIngredientLine altLine = new AlternateIngredientLine();
    altLine.amount= Double.parseDouble((test+1) + "." +  (list+1) + (line+1) +(altLineNumber+1));
    altLine.unit= new IngredientUnit((test+1) + ".List" + (list+1) + "-Line" + (line+1)+"-AltLine"+(altLineNumber+1) + "-Unit");
    altLine.ingredient=new Ingredient((test+1) + ".List" + (list+1) + "-Line" + (line+1)+"-AltLine"+(altLineNumber+1) + "Ingredient");
    return altLine;
  }

  //inputting the information to the addrecipe page
  private void addRecipeValues(Recipe recipe) {
    deletedLines = new ArrayList<>();
    deletedAltLines = new ArrayList<>();
    deletedCategories = new ArrayList<>();
    $(By.id("title")).setValue(recipe.name);
    $(By.id("source")).setValue(recipe.source.name);
    int listField = 0;
    for(IngredientList list:recipe.ingredientLists) {
      listNameSet(listField, list.name.name);
      int lineField = 0;
      for (IngredientLine line:list.ingredientLines) {
        addLine(listField);
        lineValues(listField, lineField, line.amount, line.unit.name, line.ingredient.name);
        int altLineField = 0;
        for(AlternateIngredientLine altLine:line.alternateLines) {
          addAltLine(listField, lineField);
          altLineValues(listField, lineField, altLineField, altLine.amount, altLine.unit.name, altLine.ingredient.name);
          altLineField++;
        }
        lineField++;
      }
      for (int timesDeleted = 0; timesDeleted <= altLinesPerLine; timesDeleted++) {
        removeLine(recipe, listField, lineField - 1);
      }
      listField++;
      addList();
    }
    selectByPlaceholder("Juhised").clear();
    selectByPlaceholder("Juhised").setValue(recipe.instructions);
    int categoryField=0;
    for(Category category:recipe.categories) {
      setCategory(categoryField, category.name);
      $(By.id("addCategory")).click();
      categoryField++;
    }
    removeCategory(recipe, 2);
    while ($$(Selectors.byAttribute("placeholder", "Jaotise nimi")).size() > recipe.ingredientLists.size()) {
      $(By.id("list" + ($$(Selectors.byAttribute("placeholder", "Jaotise nimi")).size()-1) + "-del")).click();
    }
  }

  //checking the shown information
  private void checkRecipe(Recipe recipe) {
    openRecipeList();
    $(Selectors.byText(recipe.name)).click();
    $(By.id("recipeheader")).shouldHave(exactTextCaseSensitive(createRecipeHeaderInfo(recipe)));
    $(By.id("instructions")).shouldHave(exactTextCaseSensitive(recipe.instructions));
    $(By.id("categories")).shouldHave(textCaseSensitive(createCategoryInfo(recipe)));
    $(By.id("ingredient-list")).shouldHave(exactTextCaseSensitive(createRecipeIngredientInfo(recipe)));
    for(IngredientLine deletedLine:deletedLines) {
      $(Selectors.withText(createLineInfo(deletedLine).toString())).shouldNotBe(visible);
    }
    for(AlternateIngredientLine deletedAltLine:deletedAltLines) {
      $(Selectors.withText(createAltLineInfo(deletedAltLine).toString())).shouldNotBe(visible);
    }
    for(Category deletedCategory:deletedCategories) {
      $(Selectors.withText(deletedCategory.name)).shouldNotBe(visible);
    }
  }

  private void addList() {
    $(By.id("addList")).click();
  }
  private void listNameSet(int list, String value) {
    $(By.id("list"+list+"-name")).click();
    $(By.id("list"+list+"-name")).setValue(value);
  }

  private void addLine(int list) {
    $(By.id("list"+list+"-addLine")).click();
  }
  private void removeLine(Recipe recipe, int list, int line) {
    IngredientLine ingredientLine = recipe.ingredientLists.get(list).ingredientLines.get(line);
    if (ingredientLine.alternateLines.size()>0) {
      AlternateIngredientLine altLine = ingredientLine.alternateLines.get(0);
      IngredientLine deletedLine = new IngredientLine();
      deletedLine.amount = ingredientLine.amount;
      deletedLine.unit = new IngredientUnit(ingredientLine.unit.name);
      deletedLine.ingredient = new Ingredient(ingredientLine.ingredient.name);
      deletedLine.alternateLines= new ArrayList<>();
      deletedLines.add(deletedLine);
      ingredientLine.amount = altLine.amount;
      ingredientLine.unit.name = altLine.unit.name;
      ingredientLine.ingredient.name = altLine.ingredient.name;
      ingredientLine.alternateLines.remove(0);
      $(By.id("list"+list+"-line"+line+"-del")).click();
    } else {
      deletedLines.add(ingredientLine);
      recipe.ingredientLists.get(list).ingredientLines.remove(line);
      $(By.id("list"+list+"-line"+line+"-del")).click();
    }
  }
  private void lineValues(int list, int line, double amount, String unit, String ingredient) {
    String lineId="list"+list+"-line"+line;
    $(By.id(lineId+"-amt")).click();
    $(By.id(lineId+"-amt")).setValue(Double.toString(amount));
    $(By.id(lineId+"-unit")).click();
    $(By.id(lineId+"-unit")).setValue(unit);
    $(By.id(lineId+"-ingr")).click();
    $(By.id(lineId+"-ingr")).setValue(ingredient);
  }

  private void addAltLine(int list, int line) {
    $(By.id("list"+list+"-line"+line+"-addAlt")).click();
  }
  private void removeAltLine(int list, int line, int altLine, IngredientLine ingrLine) {
    deletedAltLines.add(ingrLine.alternateLines.get(altLine));
    ingrLine.alternateLines.remove(altLine);
    $(By.id("list"+list+"-line"+line+"-altLine"+altLine+"-del")).click();
  }
  private void altLineValues(int list, int line, int altLine, double amount, String unit, String ingredient) {
    String altLineId = "list"+list+"-line"+line+"-altLine"+altLine;
    $(By.id(altLineId+"-amt")).click();
    $(By.id(altLineId+"-amt")).setValue(Double.toString(amount));
    $(By.id(altLineId+"-unit")).click();
    $(By.id(altLineId+"-unit")).setValue(unit);
    $(By.id(altLineId+"-ingr")).click();
    $(By.id(altLineId+"-ingr")).setValue(ingredient);
  }

  private void removeCategory (Recipe recipe, int category) {
    deletedCategories.add(recipe.categories.get(category));
    $(By.id("category"+category+"-del")).click();
    recipe.categories.remove(category);
  }
  private void setCategory(int field, String name) {
    $(By.id("category"+field)).setValue(name);
  }

  //creating the text expected from the recipe's page (div ingredient-list)
  private String createRecipeIngredientInfo(Recipe recipe) {
    StringBuilder recipeInfo = new StringBuilder();
    for(IngredientList list:recipe.ingredientLists) {
      recipeInfo.append(list.name.name);
      recipeInfo.append(":\n");
      for (IngredientLine line:list.ingredientLines) {
        recipeInfo.append(createLineInfo(line));
        recipeInfo.append('\n');
      }
    }
    return recipeInfo.toString();
  }
  private StringBuilder createLineInfo(IngredientLine line) {
    StringBuilder lineInfo = new StringBuilder();
    lineInfo.append(line.amount);
    lineInfo.append(" ");
    lineInfo.append(line.unit.name);
    lineInfo.append(" ");
    lineInfo.append(line.ingredient.name);
    for (AlternateIngredientLine altLine:line.alternateLines) {
      lineInfo.append(" või");
      lineInfo.append('\n');
      lineInfo.append(createAltLineInfo(altLine));
    }
    return lineInfo;
  }
  private StringBuilder createAltLineInfo(AlternateIngredientLine altLine) {
    StringBuilder altLineInfo = new StringBuilder();
    altLineInfo.append(altLine.amount);
    altLineInfo.append(" ");
    altLineInfo.append(altLine.unit.name);
    altLineInfo.append(" ");
    altLineInfo.append(altLine.ingredient.name);
    return altLineInfo;
  }

  //creating the text expected from the recipe's header (div recipeheader)
  private String createRecipeHeaderInfo(Recipe recipe) {
    StringBuilder header = new StringBuilder();
    header.append($(By.className("edit-button")).text());
    header.append(" ");
    header.append(recipe.name);
    header.append('\n');
    header.append(recipe.source.name);
    return header.toString();
  }

  //creating the text expected from the categories section (div categories)
  private String createCategoryInfo(Recipe recipe) {
    StringBuilder categories = new StringBuilder();
    categories.append("Kategooriad:\n");
    for(Category category:recipe.categories) {
      categories.append(category.name);
      categories.append(", ");
    }
    categories.delete(categories.length()-2, categories.length());
    return categories.toString();
  }







  private void noTitleTest() {
    $(By.id("title")).setValue("    ");
    $(Selectors.byText("Palun sisestage retsepti pealkiri.")).shouldBe(visible);
    $(By.id("title")).clear();
    $(Selectors.byText("Palun sisestage retsepti pealkiri.")).shouldBe(visible);
    $(By.id("title")).setValue("title");
    $(Selectors.byText("Palun sisestage retsepti pealkiri.")).shouldNotBe(visible);
    $(By.id("title")).clear();
  }
  private void noInstructionsTest() {
    selectByPlaceholder("Juhised").setValue("   ");
    $(Selectors.byText("Palun sisestage retsepti juhised.")).shouldBe(visible);
    selectByPlaceholder("Juhised").clear();
    $(Selectors.byText("Palun sisestage retsepti juhised.")).shouldBe(visible);
    selectByPlaceholder("Juhised").setValue("instructions");
    $(Selectors.byText("Palun sisestage retsepti juhised.")).shouldNotBe(visible);
  }

}
