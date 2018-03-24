<template>
  <div id="search">
    <div v-if="searchShown" id="search-input">
      <datalist id="ingredient-data">
        <option v-for="ingredient in autofill.ingredients" :value="ingredient">{{ ingredient }}</option>
      </datalist>
      <datalist id="recipe-data">
        <option v-for="name in autofill.names" :value="name">{{ name }}</option>
      </datalist>
      <datalist id="user-data">
        <option v-for="user in autofill.users" :value="user">{{ user }}</option>
      </datalist>
      <datalist id="source-data">
        <option v-for="source in autofill.sources" :value="source">{{ source }}</option>
      </datalist>
      Retsepti pealkiri:
      <input type="text" list="recipe-data" v-model="recipeToSearch.name" id="title" placeholder="Pealkiri"/>
      Retsepti allikas:
      <input id="source" type="text" list="source-data" v-model="recipeToSearch.source" placeholder="Allikas"/>
      Retsepti lisaja:
      <input type="text" list="user-data" v-model="recipeToSearch.username" id="creator" placeholder="Lisaja"/>
      <input type="checkbox" name="descending" v-model="recipeToSearch.hasPicture"> Pildiga?
      <div id="ingredient-list">
        Sisaldab koostisosa:
        <div>
          <div v-for="(line, lineIndex) in recipeToSearch.withIngredients">
            <input class="ingredient-input" type="text" list="ingredient-data"  v-model="line.ingredient" :id="'list0-line' + lineIndex + '-ingr'" placeholder="Koostisosa"/>
            <button class="one-char-button add-button" :id="'list0-line' + lineIndex + '-addAlt'" @click="addAltRow(line)">&or;</button>
            <button v-if="lineIndex > 0" class="one-char-button delete-button" :id="'lis0-line' + lineIndex + '-del'" @click="deleteRow(line)">-</button>
            <div v-if="line.alternateLines.length > 0">
              <div v-for="(altLine, altLineIndex) in line.alternateLines">
                <input class="ingredient-input" type="text" list="ingredient-data" v-model="altLine.ingredient" placeholder="Alternatiivkoostisosa" :id="'list0-line' + lineIndex + '-altLine' + altLineIndex + '-ingr'"/>
                <button class="one-char-button delete-button" @click="remove(line.alternateLines, altLine)" :id="'list0-line' + lineIndex + '-altLine' + altLineIndex + '-del'">-</button>
              </div>
            </div>
          </div>
          <button @click="addRow()" class="one-char-button add-button" :id="'list0-addLine'">+</button>
        </div>
        Ei sisalda koostisosa:
        <div>
          <div v-for="(line, lineIndex) in recipeToSearch.withoutIngredients">
            <input class="ingredient-input" type="text" list="ingredient-data"  v-model="recipeToSearch.withoutIngredients[lineIndex]" :id="'list1-line' + lineIndex + '-ingr'" placeholder="Koostisosa"/>
            <button v-if="lineIndex > 0" class="one-char-button delete-button" :id="'list1-line' + lineIndex + '-del'" @click="remove(recipeToSearch.withoutIngredients, line)">-</button>
          </div>
          <button @click="addIngredient()" class="one-char-button add-button" :id="'list1-addLine'">+</button>
        </div>
        Retsepti kategooria:
        <div>
          <div v-for="(category, index) in recipeToSearch.categories">
            <input type="text" list="category-data" v-focus v-model="recipeToSearch.categories[index]" :id="'category' + index" placeholder="Kategooria"/>
            <button v-if="index > 0" @click="deleteCategoryRow(category)" class="one-char-button delete-button" :id="'category' + index + '-del'">-</button>
          </div>
          <button @click="addCategoryRow()" class="one-char-button add-button" id="addCategory">+</button>
        </div>
      </div>
      <datalist id="category-data">
        <option v-for="category in autofill.categories" :value="category.name">{{ category.name }}</option>
      </datalist>
      <div>
        Sorteeri:
        <select id="sort" v-model="recipeToSearch.sortOrder">
          <option :value="0">Pealkirja järgi</option>
          <option :value="1">Lisaja järgi</option>
          <option :value="2">Lisamisaja järgi</option>
        </select>
      </div>
      <div>
        <input type="checkbox" name="descending" id="descendingorder" v-model="recipeToSearch.descending"> Vastupidises järjekorras?
      </div>
      <div>
        Tulemusi lehel:
        <select id="resultsPerPage" v-model="recipeToSearch.resultsPerPage">
          <option :value="10">10</option>
          <option :value="50">50</option>
          <option :value="0">Kõik</option>
        </select>
      </div>
      <button id="searchbutton" @click="search = JSON.parse(JSON.stringify(recipeToSearch)), startSearch(0)">Otsi</button>
      <button id="hide" @click="searchShown = false">Peida otsing</button>
    </div>
    <button v-if="!searchShown" id="show" @click="searchShown = true">Näita otsing</button>
    <div id="recipelist">
      <div id="pages">
        <button v-if="pages>1" :disabled="search.resultPage === 0" id="firstpage" @click="startSearch(0)">&#8656</button>
        <button v-if="pages>0" :disabled="search.resultPage === 0" id="previouspage" @click="startSearch(search.resultPage - 1)">&#8592</button>
        <div v-for="n in pages">
          <button :disabled="n === search.resultPage + 1" @click="startSearch(n - 1)" :id="'page' + n">{{ n }}</button>
        </div>
        <button v-if="pages>0" :disabled="search.resultPage + 1 >= pages" id="nextpage" @click="startSearch(search.resultPage + 1)">&#8594</button>
        <button v-if="pages>1" :disabled="search.resultPage + 1 >= pages" id="lastpage" @click="startSearch(pages-1)">&#8658</button>
      </div>
      <ul >
        <li id = "Result" v-for="recipe in shownRecipes"><img v-if="recipe.pictureName" :id="'recipe' + recipe.id" :src="'images/' + recipe.id + '/2RecipePicture.' + recipe.pictureName"><router-link v-bind:to="'/recipe/' + recipe.id">{{ recipe.name }}</router-link> ({{ recipe.user.username }} {{ new Date(recipe.added) }})</li>
      </ul>
    </div>


  </div>
</template>
<style scoped>
  ul {
    text-align: left;
  }
  #recipelist {
    text-align: center;
  }
  #search-input {
    display: block;
  }
  #ingredient-list {
    display: flex;
    text-align: left;
    place-content: center;
  }
  #pages {
    display: flex;
  }
  #pages :disabled {
    color: darkgray;
  }


</style>
<script>
  import { getAutoFillData, findRecipes } from "../api.js";
  import { store } from "../datastore.js";

  export default{
    name: "search",
    data: function () {
      const myData = {
        searchShown: true,
        recipeToSearch: store.recipeToSearch,
        pages: 0,
        search: {resultPage: 0},
        shownRecipes: [],
        autofill: {
          names: [],
          users: [],
          ingredients: [],
          categories: [],
          sources: []
        }
      };
      store.resetSearch();
      return myData;
    },
    beforeRouteEnter (to, from, next) {
      getAutoFillData((err, res) => {
        if (err) {
          console.log("Fetching autofill data failed!");
          next();
        } else {
          next(vm => {
            vm.autofill = res;
          });
        }
      });
    },
    methods: {
      startSearch: function (resultPage) {
        this.search.resultPage = resultPage;
        findRecipes(this.filteredRecipe(this.search), (err, res) => {
          if (err) {
            console.log("Search failed");
          } else {
            this.shownRecipes = res.recipes;
            this.pages = res.pages;
          }
        });
      },
      filteredRecipe: function (recipe) {
        const newRecipe = JSON.parse(JSON.stringify(recipe)); // do deep copy
        newRecipe.withIngredients = newRecipe.withIngredients.filter(x => x.ingredient);
        newRecipe.withIngredients.forEach(line => {
          line.alternateLines = line.alternateLines.filter(x => x.ingredient);
        });
        newRecipe.withoutIngredients = newRecipe.withoutIngredients.filter(x => x);
        newRecipe.categories = newRecipe.categories.filter(x => x);
        return newRecipe;
      },
      remove: function (arr, el) {
        var index = arr.indexOf(el);
        if (index > -1) {
          arr.splice(index, 1);
        }
      },
      addRow: function () {
        this.recipeToSearch.withIngredients.push({alternateLines: []});
      },
      addIngredient: function () {
        this.recipeToSearch.withoutIngredients.push("");
      },
      addCategoryRow: function () {
        this.recipeToSearch.categories.push("");
      },
      addAltRow: function (line) {
        line.alternateLines.push({});
      },
      deleteRow: function (row) {
        if (row.alternateLines.length > 0 && row.alternateLines[0].ingredient) {
          const newMain = row.alternateLines[0];
          row.amount = newMain.amount;
          row.unit = newMain.unit;
          row.ingredient = newMain.ingredient;
          row.alternateLines.splice(0, 1);
        } else {
          this.remove(this.recipeToSearch.withIngredients, row);
        }
      },
      deleteCategoryRow: function (row) {
        this.remove(this.recipeToSearch.categories, row);
      }
    }
  };
</script>
