import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ee.cookbook.model.*;
import org.junit.Test;
import org.openqa.selenium.By;
import java.io.File;
import java.util.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AddRecipeTest extends BaseSelenideTest {
  private int currentTest = 0;
  private List<IngredientLine> deletedLines;
  private List<AlternateIngredientLine> deletedAltLines;
  private List<Category> deletedCategories;
  private int linesPerList;
  private int altLinesPerLine;
  private int categoriesPerRecipe;
  private Recipe addRecipeTestRecipe;
  private final String emptyListError = "Siestage jaotisele vähemalt 1 koostisosa.";
  private final String invalidLineError = "Palun sisestage koostisosa nimi.";
  private final String missingSearchIngredientError = "Palun sisestage otsingukoostisosa nimi.";
  private final String invalidAmountFormatError = "Palun sisestage koostisosa kogus sobivas vormingus.";
  private final String invalidPreparedTimeError = "Valmistamiskorda ei saa lisada tulevikku.";


  @Test
  public void testAddingRecipes() throws InterruptedException {
    testRecipeCreating();
    testEditingRecipes();
  }

  //test methods
  private void testRecipeCreating() throws InterruptedException {
    int listsDeletedFrom = 0;
    categoriesPerRecipe = 3;
    linesPerList = 4;
    altLinesPerLine = 2;
    currentTest++;
    addRecipeTestRecipe = createRecipe(2);
    openAddRecipePage();
    $(By.id("helpbutton")).click();
    $(By.id("helpbutton")).click();
    $(Selectors.byText("Otsingukoostisosa lahtrisse käib selle rea koostisosa ainsuse nimetavas käändes.")).shouldBe(visible);
    $(By.id("closemodal")).click();
    $(Selectors.byText("Otsingukoostisosa lahtrisse käib selle rea koostisosa ainsuse nimetavas käändes.")).shouldNotBe(visible);
    testErrors();
    addRecipeValues(addRecipeTestRecipe);
    for(IngredientList list : addRecipeTestRecipe.ingredientLists) {
      removeAltLine(listsDeletedFrom, 2, 1, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 2, 0, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 1, 1, list.ingredientLines.get(1));
      listsDeletedFrom++;
    }
    addImage();
    assert($(By.id("recipeimage")).getAttribute("src").matches(baseUrl + "images/temp/1-(.*).jpeg"));
    assert(checkTemporaryImagesExist());
    deleteImage();
    $(By.id("recipeimage")).shouldNotBe(visible);
    Thread.sleep(1000);
    assert(!checkTemporaryImagesExist());
    addImage();
    $(By.id("save")).click();
    Thread.sleep(1000);
    $(By.id("save")).shouldNot(exist);
    checkRecipe(addRecipeTestRecipe);
  }

  private void testEditingRecipes() throws InterruptedException {
    $(By.className("edit-button")).click();
    $(By.id("cancel")).click();
    currentTest++;
    categoriesPerRecipe = 4;
    linesPerList = 5;
    altLinesPerLine = 3;
    checkRecipe(addRecipeTestRecipe);
    Recipe changeRecipeTestRecipe = createRecipe(3);
    $(By.className("edit-button")).click();
    testErrors();
    addRecipeValues(changeRecipeTestRecipe);
    int listsDeletedFrom = 0;
    for(IngredientList list : changeRecipeTestRecipe.ingredientLists) {
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 0, 0, list.ingredientLines.get(0));
      removeAltLine(listsDeletedFrom, 2, 0, list.ingredientLines.get(2));
      removeAltLine(listsDeletedFrom, 3, 0, list.ingredientLines.get(3));
      removeAltLine(listsDeletedFrom, 3, 0, list.ingredientLines.get(3));
      listsDeletedFrom++;
    }
    deleteImage();
    dismiss();
    assert(checkSavedImagesExist());
    addImage();
    assert($(By.id("recipeimage")).getAttribute("src").matches(baseUrl + "images/temp/1-(.*).jpeg"));
    assert(checkTemporaryImagesExist());
    deleteImage();
    assert(checkSavedImagesExist());
    deleteImage();
    confirm();
    Thread.sleep(1000);
    assert(!checkSavedImagesExist());
    $(By.id("recipeimage")).shouldNotBe(visible);
    Thread.sleep(1000);
    assert(!checkTemporaryImagesExist());
    addImage();
   // $(By.id("save")).click();
    $(By.id("save")).click();
    Thread.sleep(1000);
    currentTest++;
    checkRecipe(changeRecipeTestRecipe);
  }

  private void testErrors() {
    testTitleErrors();
    testInstructionsErrors();
    testIngredientlistErrors();
}

  //creating recipe's information
  private Recipe createRecipe(int listAmount) {
    Recipe recipe = new Recipe();
    recipe.instructions = currentTest + ".Instructions";
    recipe.name = currentTest + ".Title";
    recipe.source = new RecipeSource(currentTest + ".Source");
    recipe.ingredientLists = new ArrayList<>();
    for (int lists = 1; lists <= listAmount; lists++) {
      recipe.ingredientLists.add(createRecipeSection(lists));
    }
    recipe.categories = createCategories();
    return recipe;
  }

  private List<Category> createCategories() {
    List<Category> categories = new ArrayList<>();
    for(int category = 1; category <= categoriesPerRecipe; category++) {
      categories.add(new Category(currentTest  + ".Category" + (category)));
    }
    return categories;
  }

  private IngredientList createRecipeSection(int lists) {
    IngredientList list = new IngredientList();
    list.ingredientLines = new ArrayList<>();
    for(int lines = 1; lines <= linesPerList; lines++) {
      list.ingredientLines.add(createLineValues(lists, lines));
    }
    list.name = new IngredientListName(currentTest + ".List" + lists);
    return list;
  }

  private IngredientLine createLineValues(int list, int lines) {
    IngredientLine line = new IngredientLine();
    line.amount = currentTest + "." +  list + lines;
    line.unit = new IngredientUnit(currentTest + "." + "List" + list + "-Line" + lines + "Unit");
    line.ingredient = new Ingredient(currentTest + "." + "List" + list + "-Line" + lines + "Ingredient");
    line.searchIngredient = new Ingredient(currentTest + "." + "List" + list + "-Line" + lines + "-Searchingredient");
    line.alternateLines = new ArrayList<>();
    for(int altLine = 1; altLine <= altLinesPerLine; altLine++) {
      line.alternateLines.add(createAltLineValues(list, lines, altLine));
    }
    return line;
  }

  private AlternateIngredientLine createAltLineValues(int list, int line, int altLineNumber) {
    AlternateIngredientLine altLine = new AlternateIngredientLine();
    altLine.amount = currentTest + "." +  list + line + altLineNumber;
    altLine.unit = new IngredientUnit(currentTest + ".List" + list + "-Line" + line + "-AltLine" + altLineNumber + "-Unit");
    altLine.ingredient = new Ingredient(currentTest + ".List" + list + "-Line" + line + "-AltLine" + altLineNumber + "Ingredient");
    altLine.searchIngredient = new Ingredient(currentTest + ".List" + list + "-Line" + line + "-AltLine" + altLineNumber + "-Searchingredient");
    return altLine;
  }

  //inputting the information to the AddRecipe page
  private void addRecipeValues(Recipe recipe) throws InterruptedException {
    deletedLines = new ArrayList<>();
    deletedAltLines = new ArrayList<>();
    deletedCategories = new ArrayList<>();
    getTitleField().setValue("");
    getTitleField().setValue(recipe.name);
    $(By.id("source")).click();
    $(By.id("source")).setValue(recipe.source.name);
    int listField = 0;
    for(IngredientList list : recipe.ingredientLists) {
      setListName(listField, list.name.name);
      int lineField = 0;
      for (IngredientLine line : list.ingredientLines) {
        addLine(listField);
        addLineValues(listField, lineField, line.amount, line.unit.name, line.ingredient.name, line.searchIngredient.name);
        int altLineField = 0;
        for(AlternateIngredientLine altLine : line.alternateLines) {
          addAltLine(listField, lineField);
          addAltLineValues(listField, lineField, altLineField, altLine.amount, altLine.unit.name, altLine.ingredient.name, altLine.searchIngredient.name);
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
    getInstructionsField().clear();
    getInstructionsField().setValue(recipe.instructions);
    int categoryField = 0;
    for(Category category : recipe.categories) {
      setCategory(categoryField, category.name);
      $(By.id("addCategory")).click();
      categoryField++;
    }
    removeCategory(recipe, 2);
    while (getNumberOfLists() > recipe.ingredientLists.size()) {
      getListDeleteButton(getNumberOfLists() - 1).scrollTo();
      getListDeleteButton(getNumberOfLists() - 1).click();
    }
  }

  //checking the shown information
  private void checkRecipe(Recipe recipe) throws InterruptedException {
    openSearchPage();
    $(By.id("recipe1")).click();
    $(By.id("recipeheader")).shouldHave(exactTextCaseSensitive(createRecipeHeaderInfo(recipe)));
    $(By.id("instructions")).shouldHave(exactTextCaseSensitive(recipe.instructions));
    $(By.id("categories")).shouldHave(textCaseSensitive(createCategoryInfo(recipe)));
    $(By.id("ingredient-list")).shouldHave(exactTextCaseSensitive(createRecipeIngredientInfo(recipe)));
    $(Selectors.withText("SearchIngredient")).shouldNot(exist);
    for(IngredientLine deletedLine : deletedLines) {
      $(Selectors.withText(createLineInfo(deletedLine).toString())).shouldNotBe(visible);
    }
    for(AlternateIngredientLine deletedAltLine : deletedAltLines) {
      $(Selectors.withText(createAltLineInfo(deletedAltLine).toString())).shouldNotBe(visible);
    }
    for(Category deletedCategory : deletedCategories) {
      $(Selectors.withText(deletedCategory.name)).shouldNotBe(visible);
    }
    $(By.id("image1")).shouldHave(attribute("src", baseUrl + "images/1/1RecipePicture.jpeg"));
    $(By.id("image2")).shouldNotBe(visible);
    $(By.id("image1")).click();
    $(By.id("image2")).shouldHave(attribute("src", baseUrl + "images/1/3RecipePicture.jpeg"));
    $(By.className("close")).click();
    $(By.id("image2")).shouldNotBe(visible);
    assert(checkSavedImagesExist());
    for (int i = 1; i < 6; i++) {
      $(By.id("rating"+i)).click();
      $(Selectors.byText("Hinnang salvestatud.")).shouldBe(visible);
      refresh();
      $(By.id("ratinginput"+i)).shouldBe(checked);
      $(By.id("averageRating")).shouldHave(exactTextCaseSensitive("Keskmine hinnang: " + Integer.toString(i) + ".0"));
    }
    for (int i = 1; i < 3; i++) {
      $(By.id("newpreparedtime0")).sendKeys("0" + i + "022018");
      $(By.id("savedate")).click();
      $(Selectors.byText("Valmistamiskord salvestatud.")).shouldBe(visible);
      refresh();
      $(By.id("prepared"+(i+(currentTest-1)*2))).shouldHave(text("0" + i + ".02.2018"));
    }
    testPreparedTimeError();
    $(By.id("newpreparedtime"+(2+(currentTest-1)*2))).shouldNotBe(visible);
    $(By.id("changebutton"+(2+(currentTest-1)*2))).click();
    $(By.id("newpreparedtime"+(2+(currentTest-1)*2))).sendKeys("04052018");
    $(By.id("savepreparedtime"+(2+(currentTest-1)*2))).click();
    refresh();
    $(By.id("prepared"+(2+(currentTest-1)*2))).shouldHave(text("04.05.2018"));
  }

  private int getNumberOfLists() {
    return $$(Selectors.byAttribute("placeholder", "Jaotise nimi")).size();
  }

  private SelenideElement getListDeleteButton(int list) {
    return $(By.id("list" + list + "-del"));
  }

  private SelenideElement getInstructionsField() {
    return selectByPlaceholder("Juhised");
  }

  private void addList() {
    $(By.id("addList")).click();
  }

  private void setListName(int list, String value) {
    $(By.id("list"+list+"-name")).click();
    $(By.id("list"+list+"-name")).setValue(value);
  }


  private void removeLine(Recipe recipe, int list, int line) {
    IngredientLine ingredientLine = recipe.ingredientLists.get(list).ingredientLines.get(line);
    if (ingredientLine.alternateLines.size()>0) {
      AlternateIngredientLine altLine = ingredientLine.alternateLines.get(0);
      IngredientLine deletedLine = new IngredientLine();
      deletedLine.amount = ingredientLine.amount;
      deletedLine.unit = new IngredientUnit(ingredientLine.unit.name);
      deletedLine.ingredient = new Ingredient(ingredientLine.ingredient.name);
      deletedLine.alternateLines = new ArrayList<>();
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

  private void addLineValues(int list, int line, String amount, String unit, String ingredient, String searchIngredient) {
    String lineId = "list"+list+"-line"+line;
    $(By.id(lineId+"-amt")).click();
    $(By.id(lineId+"-amt")).sendKeys("\b");
    $(By.id(lineId+"-amt")).setValue(amount);
    $(By.id(lineId+"-unit")).click();
    $(By.id(lineId+"-unit")).setValue(unit);
    $(By.id(lineId+"-ingr")).click();
    $(By.id(lineId+"-ingr")).setValue(ingredient);
    $(By.id(lineId+"-searchingr")).click();
    $(By.id(lineId+"-searchingr")).setValue(searchIngredient);
  }



  private void removeAltLine(int list, int line, int altLine, IngredientLine ingrLine) {
    deletedAltLines.add(ingrLine.alternateLines.get(altLine));
    ingrLine.alternateLines.remove(altLine);
    $(By.id("list"+list+"-line"+line+"-altLine"+altLine+"-del")).scrollTo();
    $(By.id("list"+list+"-line"+line+"-altLine"+altLine+"-del")).click();
  }

  private void addAltLineValues(int list, int line, int altLine, String amount, String unit, String ingredient, String searchIngredient) {
    String altLineId = "list"+list+"-line"+line+"-altLine"+altLine;
    $(By.id(altLineId+"-amt")).should(exist);
    $(By.id(altLineId+"-amt")).click();
    $(By.id(altLineId+"-amt")).sendKeys("\b");
    $(By.id(altLineId+"-amt")).setValue(amount);
    $(By.id(altLineId+"-unit")).click();
    $(By.id(altLineId+"-unit")).setValue(unit);
    $(By.id(altLineId+"-ingr")).click();
    $(By.id(altLineId+"-ingr")).setValue(ingredient);
    $(By.id(altLineId+"-searchingr")).click();
    $(By.id(altLineId+"-searchingr")).setValue(searchIngredient);
  }

  private void removeCategory (Recipe recipe, int category) {
    deletedCategories.add(recipe.categories.get(category));
    $(By.id("category"+category+"-del")).click();
    recipe.categories.remove(category);
  }

  private void addImage() {
    $(By.id("image-input")).uploadFile(new File("src/test/images/testimage.jpg"));
  }

  private void deleteImage() {
    $(Selectors.byText("Kustuta pilt")).scrollTo();
    $(Selectors.byText("Kustuta pilt")).click();
  }

  //creating the text expected from the recipe's page (div ingredient-list)
  private String createRecipeIngredientInfo(Recipe recipe) {
    StringBuilder recipeInfo = new StringBuilder();
    for(IngredientList list : recipe.ingredientLists) {
      recipeInfo.append(list.name.name);
      recipeInfo.append(":\n");
      for (IngredientLine line : list.ingredientLines) {
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
    for (AlternateIngredientLine altLine : line.alternateLines) {
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
    for(Category category : recipe.categories) {
      categories.append(category.name);
      categories.append(", ");
    }
    categories.delete(categories.length()-2, categories.length());
    return categories.toString();
  }


  private void testTitleErrors() {
    testTitleError(" ", true);
    testTitleError("", true);
    testTitleError("title", false);
  }

  private void testInstructionsErrors() {
    testInstructionsError(" ", true);
    testInstructionsError("", true);
    testInstructionsError("instructions", false);
  }

  private void testIngredientlistErrors() {
    final String noListsError = "Retseptil peab olema vähemalt 1 jaotis.";
    final String unnamedListError = "Palun sisestage jaotise nimi.";
    while ($(By.id("list0-name")).has(visible)) {
      $(By.id("list0-del")).click();
    }
    $(By.id("save")).click();
    checkTextVisibility(noListsError, true);
    addList();
    checkTextVisibility(noListsError, false);
    addLine(0);
    addLine(0);
    checkTextVisibility(unnamedListError, true);
    setListName(0, " ");
    checkTextVisibility(unnamedListError, true);
    setListName(0, "listname");
    checkTextVisibility(unnamedListError, false);
    checkTextVisibility(emptyListError, true);
    checkTextVisibility(invalidLineError, false);
    testIngredientLineErrors();
    addAltLine(0, 0);
    testAltlineErrors();
    $(By.id("list0-line0-altLine0-del")).click();
  }

  private void testIngredientLineErrors() {
    testLineError("", ""," ", " ", true, false, false, false);
    testLineError("1", ""," ", " ", true, true, false, false);
    testLineError("a", ""," ", " ", true, true, false, true);
    testLineError("", "unit"," ", " ", true, true, false, false);
    testLineError("", " "," ", " ", true, false, false, false);
    testLineError("1", "unit"," ", " ", true, true, false, false);
    testLineError("", "","ingredient", "", false, false, true, false);
    testLineError("1", "unit","ingredient", "ingredient", false, false, false, false);
  }

  private void testAltlineErrors() {
    testAltlineError("", ""," ", " ", false, false, false);
    testAltlineError("1", ""," ", " ", true, false, false);
    testAltlineError("a", ""," ", " ", true, false, false);
    testAltlineError("", "unit"," ", " ", true, false, false);
    testAltlineError("", " "," ", " ", false, false, false);
    testAltlineError("1", "unit"," ", " ",true, false, false);
    testAltlineError("", "","ingredient", "",false, true, false);
    testAltlineError("1", "unit","ingredient", "ingredient",false, false, false);
  }

   private void checkTextVisibility(String text, Boolean visible) {
    if (visible) {
      $(Selectors.byText(text)).shouldHave(Condition.visible);
    } else {
      $(Selectors.byText(text)).shouldNotBe(Condition.visible);
    }
  }

  private void testLineError(String amount, String unit, String ingredient, String searchIngredient, Boolean emptyListErrorVisible, Boolean invalidLineErrorVisible, Boolean missingSearchIngredientErrorVisible, Boolean invalidAmountFormatErrorVisible) {
    addLineValues(0, 0, amount, unit, ingredient, searchIngredient);
    checkTextVisibility(emptyListError, emptyListErrorVisible);
    checkTextVisibility(invalidLineError, invalidLineErrorVisible);
    checkTextVisibility(missingSearchIngredientError, missingSearchIngredientErrorVisible);
    checkTextVisibility(invalidAmountFormatError, invalidAmountFormatErrorVisible);
  }

  private void testAltlineError(String amount, String unit, String ingredient, String searchIngredient, Boolean invalidLineErrorVisible, Boolean missingSearchIngredientErrorVisible, Boolean invalidAmountFormatErrorVisible) {
    addAltLineValues(0, 0, 0, amount, unit, ingredient, searchIngredient);
    checkTextVisibility(invalidLineError, invalidLineErrorVisible);
    checkTextVisibility(missingSearchIngredientError, missingSearchIngredientErrorVisible);
    checkTextVisibility(invalidAmountFormatError, invalidAmountFormatErrorVisible);
  }

  private void testTitleError(String value, Boolean errorVisible) {
    final String noTitleError = "Palun sisestage retsepti pealkiri.";
    getTitleField().sendKeys("\b");
    getTitleField().setValue(value);
    checkTextVisibility(noTitleError, errorVisible);
  }

  private void testInstructionsError(String value, Boolean errorVisible) {
    final String noInstructionsError = "Palun sisestage retsepti juhised.";
    getInstructionsField().sendKeys("\b");
    getInstructionsField().setValue(value);
    checkTextVisibility(noInstructionsError, errorVisible);
  }

  private void testPreparedTimeError() {
    checkTextVisibility(invalidPreparedTimeError, false);
    $(By.id("newpreparedtime0")).sendKeys("01019999");
    $(By.id("savedate")).click();
    $(Selectors.byText("Valmistamiskord salvestatud.")).shouldNotBe(visible);
    checkTextVisibility(invalidPreparedTimeError, true);
    $(By.id("newpreparedtime0")).sendKeys("01012018");
    checkTextVisibility(invalidPreparedTimeError, false);
    $(By.id("changebutton1")).click();
    $(By.id("newpreparedtime1")).sendKeys("01019999");
    $(By.id("savepreparedtime1")).click();
    $(Selectors.byText("Valmistamiskord salvestatud.")).shouldNotBe(visible);
    checkTextVisibility(invalidPreparedTimeError, true);
    $(By.id("newpreparedtime1")).sendKeys("01012018");
    checkTextVisibility(invalidPreparedTimeError, false);
  }

  private boolean checkSavedImagesExist() {
    return (new File(imageFolder + "1/1RecipePicture.jpeg").exists()) && (new File(imageFolder + "1/2RecipePicture.jpeg")).exists() && (new File(imageFolder + "1/3RecipePicture.jpeg")).exists();
  }

  private boolean checkTemporaryImagesExist() throws NullPointerException {
    File tempImageDirectory =new File(imageFolder + "temp/");
    return (tempImageDirectory.listFiles().length == 3);
  }
}