import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selectors;
import com.google.common.collect.Lists;
import ee.cookbook.dao.PreparedHistoryRepository;
import ee.cookbook.dao.RatingRepository;
import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.*;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.Keys.DOWN;
import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.Keys.ESCAPE;


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
  public void testSearch() throws InterruptedException {
    addRecipes();
    addRatings();
    addPreparedTime();
    openSearchPage();
    $(By.id("toggleSearch")).click();
    Thread.sleep(1000);
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
    getTitleField().$(By.className("md-input")).setValue("Title3");
    search();
    $(By.id("recipelist")).shouldHave(text("Title3"));
    refreshPage();
    addIngredient(0, 0, 7);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title13", "Title14", "Title15", "Title16", "Title17", "Title23", "Title24", "Title25", "Title26", "Title27", "Title3", "Title33", "Title34", "Title35", "Title36", "Title37", "Title4", "Title43", "Title44", "Title45", "Title46", "Title47", "Title5", "Title6", "Title7"));
    $(By.id("recipe16image")).shouldHave(attribute("src", baseUrl + "images/16/2RecipePicture.15"));
    $(By.id("recipe15rating")).hover();
    $(Selectors.withText("Keskmine hinnang: 4")).should(visible);
    addLine(0);
    addIngredient(0, 1, 0);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title16", "Title17", "Title26", "Title27", "Title36", "Title37", "Title46", "Title47", "Title6", "Title7"));
    refreshPage();
    addIngredient(0, 0, 3);
    $(By.id("list0-line0-ingr")).$(By.className("md-input")).sendKeys(ESCAPE);
    addSearchAltLine(0, 0);
    addAltIngredient(0, 0, 4);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title49", "Title9"));
    $(By.id("recipe20rating")).hover();
    $(Selectors.withText("Minu hinnang: 2")).should(exist);
    $(Selectors.withText("Keskmine hinnang: 3.5")).should(exist);
    refreshPage();
    addSearchCategory(0, "Category4");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title12", "Title13", "Title14", "Title17", "Title18", "Title19", "Title2", "Title22", "Title23", "Title24", "Title27", "Title28", "Title29", "Title3", "Title32", "Title33", "Title34", "Title37", "Title38", "Title39", "Title4", "Title42", "Title43", "Title44", "Title47", "Title48", "Title49", "Title7", "Title8", "Title9"));
    $(By.id("addCategory")).click();
    addSearchCategory(1, "Category1");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title14", "Title19", "Title24", "Title29", "Title34", "Title39", "Title4", "Title44", "Title49", "Title9"));
    refreshPage();
    $(By.id("creator")).$(By.className("md-input")).setValue("Selenide");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü"));
    $(By.id("recipe41preparedtime")).shouldHave(text(".1970"));
    $(By.id("recipe31preparedtime")).shouldNotBe(visible);
    refreshPage();
    $(By.id("source")).$(By.className("md-input")).setValue("Source2");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title17", "Title2", "Title32", "Title47"));
    refreshPage();
    addLine(1);
    addIngredient(1, 1, 6);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title17", "Title18", "Title19", "Title20", "Title21", "Title27", "Title28", "Title29", "Title30", "Title31", "Title37", "Title38", "Title39", "Title40", "Title41", "Title47", "Title48", "Title49", "Title7", "Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    addIngredient(1, 0, 8);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title19", "Title20", "Title21", "Title29", "Title30", "Title31", "Title39", "Title40", "Title41", "Title49", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    refreshPage();
  }

  private void testMultipleFields() {
    $(By.id("creator")).$(By.className("md-input")).setValue("Selenide");
    addIngredient(0, 0, 18);
    addSearchAltLine(0, 0);
    addAltIngredient(0, 0, 15);
    addLine(0);
    addIngredient(0, 1, 4);
    addSearchAltLine(0, 1);
    addAltIngredient(1, 0, 6);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title0", "Title10", "Title12", "Title14", "Title16",  "Title2", "Title20", "Title22", "Title24", "Title26", "Title30", "Title32", "Title34", "Title36", "Title4", "Title40", "Title42", "Title44", "Title46", "Title6"));
    addLine(1);
    addLine(1);
    addIngredient(1, 0, 16);
    addIngredient(1, 1, 10);
    addIngredient(1, 2, 1);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title12", "Title16", "Title2", "Title22", "Title26", "Title32", "Title36", "Title42", "Title46", "Title6"));
    addSearchCategory(0, "Category4");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave((CollectionCondition.texts("Title12", "Title2", "Title22", "Title32", "Title42")));
    $(By.id("source")).$(By.className("md-input")).setValue("Source2");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title2", "Title32"));
    getTitleField().$(By.className("md-input")).setValue("Title32");
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title32"));
    refreshPage();
  }

  private void testEstonianLetters() {
    getTitleField().$(By.className("md-input")).setValue("Ä");
    search();
    $(By.id("recipelist")).shouldHave(text("Ä"));
    assert(!$(By.id("recipelist")).$(By.className("md-title")).getText().equals("A"));
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHaveSize(1);
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("A");
    search();
    $(By.id("recipelist")).shouldHave(text("A"));
    $(By.id("recipelist")).shouldNotHave(text("Ä"));
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("Ü");
    search();
    $(By.id("recipelist")).shouldHave(text("Ü"));
    assert(!$(By.id("recipelist")).$(By.className("md-title")).getText().equals("U"));
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHaveSize(1);
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("U");
    search();
    $(By.id("recipelist")).shouldHave(text("U"));
    $(By.id("recipelist")).shouldNotHave(text("Ü"));
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("Õ");
    search();
    $(By.id("recipelist")).shouldHave(text("Õ"));
    $(By.id("recipelist")).shouldNotHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Ö"));
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("Ö");
    search();
    $(By.id("recipelist")).shouldHave(text("Ö"));
    $(By.id("recipelist")).shouldNotHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Õ"));
    getTitleField().$(By.className("md-input")).click();
    getTitleField().$(By.className("md-input")).setValue("O");
    search();
    $(By.id("recipelist")).shouldHave(text("O"));
    $(By.id("recipelist")).shouldNotHave(text("Õ"));
    $(By.id("recipelist")).shouldNotHave(text("Ö"));
    refreshPage();
  }

  private void testPaging() {
    $(By.id("resultsPerPage")).click();
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(ENTER);
    addIngredient(0, 0, 4);
    addSearchAltLine(0, 0);
    addAltIngredient(0, 0, 9);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17"));
    $(By.id("previouspage")).shouldBe(disabled);
    $(By.id("firstpage")).shouldBe(disabled);
    $(By.id("lastpage")).click();
    $(By.id("lastpage")).shouldBe(disabled);
    $(By.id("nextpage")).shouldBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7", "Title8", "Title9"));
    $(By.id("previouspage")).click();
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44"));
    $(By.id("previouspage")).click();
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35"));
    $(By.id("firstpage")).click();
    $(By.id("nextpage")).click();
    $(By.id("firstpage")).shouldNotBe(disabled);
    $(By.id("lastpage")).shouldNotBe(disabled);
    $(By.id("nextpage")).shouldNotBe(disabled);
    $(By.id("previouspage")).shouldNotBe(disabled);
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26"));
    refreshPage();
    $(By.id("resultsPerPage")).click();
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(ENTER);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17", "Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26", "Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35", "Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7"));
    $(By.id("nextpage")).click();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
    $(By.id("toggleSearch")).click();
    $(By.id("resultsPerPage")).click();
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(ENTER);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title1", "Title10", "Title11", "Title12", "Title13", "Title14", "Title15", "Title16", "Title17", "Title18", "Title19", "Title2", "Title20", "Title21", "Title22", "Title23", "Title24", "Title25", "Title26", "Title27", "Title28", "Title29", "Title3", "Title30", "Title31", "Title32", "Title33", "Title34", "Title35", "Title36", "Title37", "Title38", "Title39", "Title4", "Title40", "Title41", "Title42", "Title43", "Title44", "Title45", "Title46", "Title47", "Title48", "Title49", "Title5", "Title6", "Title7", "Title8", "Title9", "U", "Õ", "Ä", "Ö", "Ü"));
  }

  private void testSortByNameDescending() throws InterruptedException {
    $(By.id("resultsPerPage")).click();
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(ENTER);
    $(Selectors.withText("Kahanevalt")).click();
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Ü", "Ö", "Ä", "Õ", "U", "Title9", "Title8", "Title7", "Title6", "Title5"));
    $(By.id("nextpage")).click();
    Thread.sleep(200);
    $(By.id("nextpage")).click();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title4", "Title39", "Title38", "Title37", "Title36", "Title35", "Title34", "Title33", "Title32", "Title31"));
  }

  private void testSortByUser() {
    $(By.id("toggleSearch")).click();
    $(Selectors.withText("Kahanevalt")).click();
    $(By.id("sort")).click();
    $(By.id("sort")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("sort")).sendKeys(ENTER);
    $(By.id("resultsPerPage")).click();
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(DOWN);
    $(By.id("resultsPerPage")).sendKeys(ENTER);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü", "Title1", "Title11", "Title13", "Title15", "Title17", "Title19", "Title21", "Title23", "Title25", "Title27", "Title29", "Title3", "Title31", "Title33", "Title35", "Title37", "Title39", "Title41", "Title43", "Title45", "Title47", "Title49", "Title5", "Title7", "Title9"));
  }

  private void testSortByUserDescending() {
    $(Selectors.withText("Kahanevalt")).click();
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts("Title1", "Title11", "Title13", "Title15", "Title17", "Title19", "Title21", "Title23", "Title25", "Title27", "Title29", "Title3", "Title31", "Title33", "Title35", "Title37", "Title39", "Title41", "Title43", "Title45", "Title47", "Title49", "Title5", "Title7", "Title9", "A", "O", "Title0", "Title10", "Title12", "Title14", "Title16", "Title18", "Title2", "Title20", "Title22", "Title24", "Title26", "Title28", "Title30", "Title32", "Title34", "Title36", "Title38", "Title4", "Title40", "Title42", "Title44", "Title46", "Title48", "Title6", "Title8", "U", "Õ", "Ä", "Ö", "Ü"));
  }

  private void testSortByDateAdded() {
    $(Selectors.withText("Kahanevalt")).click();
    $(By.id("sort")).click();
    $(By.id("sort")).sendKeys(DOWN);
    $(By.id("sort")).sendKeys(ENTER);
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts(recipeTitles));
  }

  private void testSortByDateAddedDescending() {
    $(Selectors.withText("Kahanevalt")).click();
    search();
    $(By.id("recipelist")).$$(By.className("md-title")).shouldHave(CollectionCondition.texts(Lists.reverse(recipeTitles)));
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
      } else {
        recipe.pictureName = "";
      }
      list.ingredientLines = new ArrayList<>();
      recipe.ingredientLists = new ArrayList<>();
      recipe.ingredientLists.add(list);
      recipe.ingredientLists.add(list);
      for (int n=0; n < 5; n++) {IngredientLine ingredientLine = new IngredientLine();
        ingredientLine.ingredient = "Ingredient" + ((i + n) % 10);
        ingredientLine.searchIngredient = "Ingredient" + ((i+n)%10);
        recipe.ingredientLists.get(0).ingredientLines.add(ingredientLine);
      }
      for (int n=0; n < 5; n++) {IngredientLine ingredientLine = new IngredientLine();
        ingredientLine.ingredient = "Ingredient" + ((i + n) % 10 + 11);
        ingredientLine.searchIngredient = "Ingredient" + ((i + n) % 10 + 10);
        ingredientLine.alternateLines = new ArrayList<>();
        AlternateIngredientLine altLine = new AlternateIngredientLine();
        altLine.ingredient = "Ingredient" + ((((i + n) % 10) * 3 + n) % 10 + 10);
        altLine.searchIngredient = "Ingredient" + ((((i + n) % 10) * 3 + n) % 10 + 10);
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
    String lineId="list" + list + "-line" + line;
    $(By.id(lineId + "-ingr")).$(By.className("md-input")).setValue("Ingredient" + ingredient);
  }

  private void addAltIngredient(int line, int altLine, int ingredient) {
    String altLineId = "list0-line" + line + "-altLine" + altLine;
    $(By.id(altLineId + "-ingr")).$(By.className("md-input")).setValue("Ingredient" + ingredient);
  }

  private void addSearchCategory(int field, String name) {
    $(By.id("category" + field)).$(By.className("md-input")).click();
    $(By.id("category" + field)).$(By.className("md-input")).setValue(name);
  }

  public void addSearchAltLine(int list, int line) {
    $(By.id("list" + list + "-line" + line + "-ingr")).$(By.className("md-input")).sendKeys(ESCAPE);
    $(By.id("list" + list + "-line" + line + "-addAlt")).click();
  }

  private void createEstonianLetterRecipe(String letter, int i) {
    Recipe recipe = new Recipe();
    recipe.name = letter;
    recipeTitles.add(recipe.name);
    recipe.pictureName = "";
    recipe.added = new Timestamp(2000000000+(i+50)*100000);
    recipe.pictureName = "";
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
    line.ingredient = letter;
    line.searchIngredient = letter;
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
    $(By.id("toggleSearch")).click();
  }

  private void refreshPage() {
    refresh();
    $(By.id("toggleSearch")).click();
  }
}