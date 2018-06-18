import com.codeborne.selenide.CollectionCondition;
import com.google.common.collect.Lists;
import ee.cookbook.dao.PreparedHistoryRepository;
import ee.cookbook.dao.RatingRepository;
import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.*;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class SearchTest extends BaseSelenideTest {
  private List<String> recipeTitles = new ArrayList<>();

  @Before
  public void createSecondUser() throws Exception {
    Connection conn = DriverManager.getConnection(flywayUrl, flywayUser, flywayPassword);
    String query = "INSERT INTO `cookbooktest`.`user` (`username`, `passwordHash`, `active`) VALUES ('testAdder', '', 1);";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.execute();
    conn.close();
  }

  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private RatingRepository ratingRepository;
  @Autowired
  private PreparedHistoryRepository preparedHistoryRepository;

  @Test
  public void testSearch() {
    addRecipes();
    addRatings();
    addPreparedTime();
    openSearchPage();
    testSingleFields();
    testMultipleFields();
    testEstonianLetters();
    testPaging();
    testSortByNameDescending();
    testSortByUser();
    testSortByUserDescending();
    testSortByDateAdded();
    testSortByDateAddedDescending();
  }

  private void testSingleFields() {
    getTitleField().setValue("Title3");
    search();
    $(By.id("recipelist")).shouldHave(text("Title3"));
    refresh();
    addIngredient(0, 0, 7);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title13", "Title14", "Title15", "Title16", "Title17", "Title23", "Title24", "Title25", "Title26", "Title27", "Title3", "Title33", "Title34", "Title35", "Title36", "Title37", "Title4", "Title43", "Title44", "Title45", "Title46", "Title47", "Title5", "Title6", "Title7"));
    $(By.id("recipe16image")).shouldHave(attribute("src", baseUrl + "images/16/2RecipePicture.15"));
    $(By.id("recipe15rating")).shouldNotBe(visible);
    $(By.id("recipe15averagerating")).shouldHave(text("4.0"));
    addLine(0);
    addIngredient(0, 1, 0);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title16", "Title17", "Title26", "Title27", "Title36", "Title37", "Title46", "Title47", "Title6", "Title7"));
    refresh();
    addIngredient(0, 0, 3);
    addAltLine(0, 0);
    addAltIngredient(0, 0, 4);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title49", "Title9"));
    $(By.id("recipe20rating")).shouldHave(text("2"));
    $(By.id("recipe20averagerating")).shouldHave(text("3.5"));
    refresh();
    setCategory(0, "Category4");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title12", "Title13", "Title14", "Title17", "Title18", "Title19", "Title2", "Title22", "Title23", "Title24", "Title27", "Title28", "Title29", "Title3", "Title32", "Title33", "Title34", "Title37", "Title38", "Title39", "Title4", "Title42", "Title43", "Title44", "Title47", "Title48", "Title49", "Title7", "Title8", "Title9"));
    $(By.id("addCategory")).click();
    setCategory(1, "Category1");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title14", "Title19", "Title24", "Title29", "Title34", "Title39", "Title4", "Title44", "Title49", "Title9"));
    refresh();
    $(By.id("creator")).setValue("Selenide");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü"));
    $(By.id("recipe41preparedtime")).shouldHave(text(".1970"));
    $(By.id("recipe31preparedtime")).shouldNotBe(visible);
    refresh();
    $(By.id("source")).setValue("Source2");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title17", "Title2", "Title32", "Title47"));
    refresh();
    addLine(1);
    addIngredient(1, 1, 6);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title17", "Title18", "Title19", "Title20", "Title21", "Title27", "Title28", "Title29", "Title30", "Title31", "Title37", "Title38", "Title39", "Title40", "Title41", "Title47", "Title48", "Title49", "Title7", "Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    addIngredient(1, 0, 8);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title19", "Title20", "Title21", "Title29", "Title30", "Title31", "Title39", "Title40", "Title41", "Title49", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    refresh();
  }

  private void testMultipleFields() {
    $(By.id("creator")).setValue("Selenide");
    addIngredient(0, 0, 18);
    addAltLine(0, 0);
    addAltIngredient(0, 0, 15);
    addLine(0);
    addIngredient(0, 1, 4);
    addAltLine(0, 1);
    addAltIngredient(1, 0, 6);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title0", "Title10", "Title12", "Title14", "Title16",  "Title2", "Title20", "Title22", "Title24", "Title26", "Title30", "Title32", "Title34", "Title36", "Title4", "Title40", "Title42", "Title44", "Title46", "Title6"));
    addLine(1);
    addLine(1);
    addIngredient(1, 0, 16);
    addIngredient(1, 1, 10);
    addIngredient(1, 2, 1);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title12", "Title16", "Title2", "Title22", "Title26", "Title32", "Title36", "Title42", "Title46", "Title6"));
    setCategory(0, "Category4");
    search();
    $$(By.id("Result")).shouldHave((CollectionCondition.texts("Title12", "Title2", "Title22", "Title32", "Title42")));
    $(By.id("source")).setValue("Source2");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title2", "Title32"));
    getTitleField().setValue("Title32");
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title32"));
    refresh();
  }

  private void testEstonianLetters() {
    getTitleField().setValue("Ä");
    search();
    $(By.id("recipelist")).shouldHave(text("Ä"));
    $(By.id("recipelist")).shouldNotHave(text("A"));
    getTitleField().setValue("A");
    search();
    $(By.id("recipelist")).shouldHave(text("A"));
    $(By.id("recipelist")).shouldNotHave(text("Ä"));
    getTitleField().setValue("Ü");
    search();
    $(By.id("recipelist")).shouldHave(text("Ü"));
    $(By.id("recipelist")).shouldNotHave(text("U"));
    getTitleField().setValue("U");
    search();
    $(By.id("recipelist")).shouldHave(text("U"));
    $(By.id("recipelist")).shouldNotHave(text("Ü"));
    getTitleField().setValue("Õ");
    search();
    $(By.id("recipelist")).shouldHave(text("Õ"));
    $(By.id("recipelist")).shouldNotHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Ö"));
    getTitleField().setValue("Ö");
    search();
    $(By.id("recipelist")).shouldHave(text("Ö"));
    $(By.id("recipelist")).shouldNotHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Õ"));
    getTitleField().setValue("O");
    search();
    $(By.id("recipelist")).shouldHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Õ"));
    $(By.id("recipelist")).shouldNotHave(text("Ö"));
    refresh();
  }

  private void testPaging() {
    $(By.id("resultsPerPage")).selectOptionByValue(Integer.toString(10));
    addIngredient(0, 0, 4);
    addAltLine(0, 0);
    addAltIngredient(0, 0, 9);
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17"));
    $(By.id("page1")).shouldBe(disabled);
    $(By.id("previouspage")).shouldBe(disabled);
    $(By.id("firstpage")).shouldBe(disabled);
    $(By.id("lastpage")).click();
    $(By.id("page5")).shouldBe(disabled);
    $(By.id("lastpage")).shouldBe(disabled);
    $(By.id("nextpage")).shouldBe(disabled);
    $(By.id("page1")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7", "Title8", "Title9"));
    $(By.id("previouspage")).click();
    $(By.id("page4")).shouldBe(disabled);
    $(By.id("page5")).shouldNotBe(disabled);
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44"));
    $(By.id("page3")).click();
    $(By.id("page3")).shouldBe(disabled);
    $(By.id("page4")).shouldNotBe(disabled);
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35"));
    $(By.id("firstpage")).click();
    $(By.id("nextpage")).click();
    $(By.id("page2")).shouldBe(disabled);
    $(By.id("page3")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26"));
    refresh();
    $(By.id("resultsPerPage")).selectOptionByValue(Integer.toString(50));
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17", "Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26", "Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35", "Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7"));
    $(By.id("nextpage")).click();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    $(By.id("resultsPerPage")).selectOptionByValue(Integer.toString(0));
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17", "Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26", "Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35", "Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7", "Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
  }

  private void testSortByNameDescending() {
    $(By.id("resultsPerPage")).selectOptionByValue(Integer.toString(10));
    $(By.id("descendingorder")).click();
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Ü", "Ö", "Ä", "Õ", "U", "Title9", "Title8", "Title7", "Title6", "Title5"));
    $(By.id("page3")).click();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title4", "Title39", "Title38", "Title37", "Title36", "Title35", "Title34", "Title33", "Title32", "Title31"));
  }

  private void testSortByUser() {
    $(By.id("sort")).selectOptionByValue(Integer.toString(1));
    $(By.id("resultsPerPage")).selectOptionByValue(Integer.toString(0));
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("Title1", "Title11", "Title13", "Title15", "Title17", "Title19", "Title21", "Title23", "Title25", "Title27", "Title29", "Title3", "Title31", "Title33", "Title35", "Title37", "Title39", "Title41", "Title43", "Title45", "Title47", "Title49", "Title5", "Title7", "Title9", "A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü"));
 }

  private void testSortByUserDescending() {
    $(By.id("descendingorder")).click();
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü", "Title1", "Title11", "Title13", "Title15", "Title17", "Title19", "Title21", "Title23", "Title25", "Title27", "Title29", "Title3", "Title31", "Title33", "Title35", "Title37", "Title39", "Title41", "Title43", "Title45", "Title47", "Title49", "Title5", "Title7", "Title9"));
  }

  private void testSortByDateAdded() {
    $(By.id("sort")).selectOptionByValue(Integer.toString(2));
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts(recipeTitles));
  }

  private void testSortByDateAddedDescending() {
    $(By.id("descendingorder")).click();
    search();
    $$(By.id("Result")).shouldHave(CollectionCondition.texts(Lists.reverse(recipeTitles)));
  }

  private void addRecipes() {
    for (int i = 0; i < 50; i++) {
      Recipe recipe = new Recipe();
      recipe.name = "Title" + i;
      recipeTitles.add(recipe.name);
      recipe.added = new Timestamp(2000000000+i*100000);
      recipe.instructions = "instructions";
      recipe.source = new RecipeSource("Source" + (i % 15));
      IngredientList list = new IngredientList();
      list.name = new IngredientListName("Koostis");
      recipe.user = new User();
      if (i % 2 == 0) {
        recipe.user.id = 1;
        recipe.user.username = "Selenide";
      } else {
        recipe.user.id = 2;
        recipe.user.username = "testAdder";
      }
      if (i % 5 == 0) {
        recipe.pictureName = Integer.toString(i);
      }
      list.ingredientLines = new ArrayList<>();
      recipe.ingredientLists = new ArrayList<>();
      recipe.ingredientLists.add(list);
      recipe.ingredientLists.add(list);
      for (int n=0; n < 5; n++) {IngredientLine ingredientLine = new IngredientLine();
        ingredientLine.ingredient = new Ingredient("Ingredient" + ((i + n) % 10));
        recipe.ingredientLists.get(0).ingredientLines.add(ingredientLine);
      }
      for (int n=0; n < 5; n++) {IngredientLine ingredientLine = new IngredientLine();
        ingredientLine.ingredient = new Ingredient("Ingredient" + ((i + n) % 10 + 10));
        ingredientLine.alternateLines = new ArrayList<>();
        AlternateIngredientLine altLine = new AlternateIngredientLine();
        altLine.ingredient = new Ingredient("Ingredient" + ((((i + n) % 10) * 3 + n) % 10 + 10));
        ingredientLine.alternateLines.add(altLine);
        recipe.ingredientLists.get(1).ingredientLines.add(ingredientLine);
      }
      recipe.categories = new ArrayList<>();
      for (int n = 0; n < 3; n++) {
        recipe.categories.add(new Category("Category" + ((n+i) % 5)));
      }
      Recipe savedRecipe = recipeRepository.save(recipe);
    }
    createEstonianLetterRecipe("Ä", 0);
    createEstonianLetterRecipe("A", 1);
    createEstonianLetterRecipe("Ö", 2);
    createEstonianLetterRecipe("O", 3);
    createEstonianLetterRecipe("Õ", 4);
    createEstonianLetterRecipe("Ü", 5);
    createEstonianLetterRecipe("U", 6);
  }


  private void addIngredient(int list, int line, int ingredient) {
    String lineId="list"+list+"-line"+line;
    $(By.id(lineId+"-ingr")).setValue("Ingredient"+ingredient);
  }

  private void addAltIngredient(int line, int altLine, int ingredient) {
    String altLineId = "list0-line"+line+"-altLine"+altLine;
    $(By.id(altLineId+"-ingr")).setValue("Ingredient" + ingredient);
  }

  private void createEstonianLetterRecipe(String letter, int i) {
    Recipe recipe = new Recipe();
    recipe.name = letter;
    recipeTitles.add(recipe.name);
    recipe.added = new Timestamp(2000000000+(i+50)*100000);
    recipe.instructions = "instructions";
    recipe.source = new RecipeSource(letter);
    recipe.user = new User();
    recipe.user.id = 1;
    recipe.user.username = "Selenide";
    recipe.ingredientLists = new ArrayList<>();
    IngredientList list = new IngredientList();
    list.name = new IngredientListName("Koostis");
    list.ingredientLines = new ArrayList<>();
    IngredientLine line = new IngredientLine();
    line.ingredient = new Ingredient(letter);
    list.ingredientLines.add(line);
    recipe.ingredientLists.add(list);
    recipe.categories = new ArrayList<>();
    recipe.categories.add(new Category(letter));
    Recipe savedRecipe = recipeRepository.save(recipe);
  }

  private void addRatings() {
    for (int i = 5; i < 51; i=i+5) {
      if (i % 10 == 0) {
        Rating rating1 = new Rating();
        rating1.recipeId = new Long(i);
        rating1.userId = new Long(1);
        rating1.rating = i/10;
        ratingRepository.save(rating1);
      }
      Rating rating2 = new Rating();
      rating2.recipeId = new Long(i);
      rating2.userId = new Long(2);
      rating2.rating = i/5 % 5 + 1;
      ratingRepository.save(rating2);
    }
  }

  private void addPreparedTime() {
    for (int i = 1; i < 51; i=i+10) {
      PreparedHistory lastPrepared = new PreparedHistory();
      lastPrepared.recipeId = new Long(i);
      lastPrepared.userId = new Long(i/10 % 2 + 1);
      lastPrepared.preparedTime = new Timestamp(2100000000*i);
      preparedHistoryRepository.save(lastPrepared);
    }
  }

  private void search() {
    $(By.id("searchbutton")).click();
  }
}
