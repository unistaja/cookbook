<template>

  <div id="search">
    <md-content>
    <div id="search-input">
      <form class="md-layout md-dense md-alignment-center-left">
        <md-card class="md-layout-item md-size=100">
          <md-card-header>
            <div class="md-title">Otsing
              <md-card-expand-trigger>

              <md-button class="md-icon-button" id="toggleSearch">
                <md-icon md-src="/static/icons/baseline-keyboard_arrow_down-24px.svg"/>
              </md-button>
            </md-card-expand-trigger></div>

          </md-card-header>

          <md-card-expand>
            <md-card-expand-content>
              <md-card-content>


                <div class="md-layout md-gutter md-alignment-center-left">
                  <div class="md-layout-item md-size-20 md-medium-size-33 md-small-size-100">
                    <md-autocomplete md-dense v-model="recipeToSearch.name" id="title" :md-options="autofill.names">
                      <label>Pealkiri</label>
                    </md-autocomplete>
                  </div>
                  <div class="md-layout-item md-size-20 md-medium-size-33 md-small-size-100">
                    <md-autocomplete v-model="recipeToSearch.source" id="source" :md-options="autofill.sources">
                      <label>Allikas</label>
                    </md-autocomplete>
                  </div>
                  <div class="md-layout-item md-size-20 md-medium-size-33 md-small-size-100">
                    <md-autocomplete v-model="recipeToSearch.username" id="creator" :md-options="autofill.users">
                      <label>Lisaja</label>
                    </md-autocomplete>
                  </div>
                  <div class="md-layout-item md-size-15 md-medium-size-33 md-small-size-100">
                    <md-checkbox v-model="recipeToSearch.hasPicture">Ainult pildiga</md-checkbox>
                  </div>
                  <div class="md-layout-item md-size-25 md-medium-size-33 md-small-size-100">
                    <fieldset>
                      <legend>Valmistanud:</legend>
                      <md-radio v-model="recipeToSearch.hasPrepared" :value="null">Kõik</md-radio>
                      <md-radio v-model="recipeToSearch.hasPrepared" :value="true">Jah</md-radio>
                      <md-radio v-model="recipeToSearch.hasPrepared" :value="false">Ei</md-radio>
                    </fieldset>
                  </div>
                </div>
                &nbsp;
                <md-divider></md-divider>

                <div class="md-subheading">Sisaldab</div>

                <div class="md-layout md-gutter">
                  <div class="md-layout-item md-size-20 md-medium-size-33 md-small-size-100"
                       v-for="(line, lineIndex) in recipeToSearch.withIngredients">
                    <div class="md-layout">
                      <div class="md-layout-item md-size-80">
                        <md-autocomplete class="md-dense" v-model="line.ingredient"
                                         :id="'list0-line' + lineIndex + '-ingr'"
                                         :md-options="autofill.searchIngredients">
                          <label>Koostisosa</label>
                          <md-button v-if="recipeToSearch.withIngredients.length > 1"
                                     :id="'list0-line' + lineIndex + '-del'"
                                     @click="deleteRow(line)" class="md-dense md-icon-button md-input-action">
                            <md-icon md-src="/static/icons/baseline-clear-24px.svg"/>
                          </md-button>

                        </md-autocomplete>

                      </div>
                      <div class="md-layout-item md-size-20">
                        <md-button class="md-raised md-dense and-button md-accent"
                                   v-if="lineIndex == recipeToSearch.withIngredients.length - 1" @click="addRow()"
                                   :id="'list0-addLine'">Ja
                        </md-button>
                        <md-button class="md-disabled md-dense and-button"
                                   v-if="lineIndex < recipeToSearch.withIngredients.length - 1">Ja
                        </md-button>
                      </div>
                    </div>
                    <md-list class="md-dense or-list" v-if="line.alternateLines.length > 0">
                      <md-list-item class="or-list" v-for="(altLine, altLineIndex) in line.alternateLines">
                        <md-button class="md-dense md-disabled or-text">Või
                        </md-button>
                        <md-autocomplete class="md-dense" v-model="altLine.ingredient"
                                         :id="'list0-line' + lineIndex + '-altLine' + altLineIndex + '-ingr'"
                                         :md-options="autofill.searchIngredients">
                          <label>Alternatiiv</label>
                          <md-button :id="'list0-line' + lineIndex + '-altLine' + altLineIndex + '-del'"
                                     @click="remove(line.alternateLines, altLine)"
                                     class="md-dense md-icon-button md-input-action">
                            <md-icon md-src="/static/icons/baseline-clear-24px.svg"/>
                          </md-button>

                        </md-autocomplete>
                      </md-list-item>
                    </md-list>
                    <md-button class="md-dense md-accent md-raised or-button" :id="'list0-line' + lineIndex + '-addAlt'"
                               @click="addAltRow(line)">Või
                    </md-button>
                  </div>
                </div>

                &nbsp;
                <md-divider></md-divider>

                <div class="md-subheading">Ei sisalda</div>

                <div class="md-layout md-gutter">
                  <div class="md-layout-item md-size-20 md-medium-size-33 md-small-size-100"
                       v-for="(line, lineIndex) in recipeToSearch.withoutIngredients">
                    <div class="md-layout">
                      <div class="md-layout-item md-size-80">
                        <md-autocomplete class="md-dense" v-model="recipeToSearch.withoutIngredients[lineIndex]"
                                         :id="'list1-line' + lineIndex + '-ingr'"
                                         :md-options="autofill.searchIngredients">
                          <label>Koostisosa</label>
                          <md-button v-if="recipeToSearch.withoutIngredients.length > 1"
                                     :id="'list1-line' + lineIndex + '-del'"
                                     @click="remove(recipeToSearch.withoutIngredients, line)"
                                     class="md-dense md-icon-button md-input-action">
                            <md-icon md-src="/static/icons/baseline-clear-24px.svg"/>
                          </md-button>

                        </md-autocomplete>

                      </div>
                      <div class="md-layout-item md-size-20">
                        <md-button class="md-raised md-dense and-button md-accent"
                                   v-if="lineIndex == recipeToSearch.withoutIngredients.length - 1"
                                   @click="addIngredient()"
                                   :id="'list1-addLine'">Ja
                        </md-button>
                        <md-button class="md-disabled md-dense and-button"
                                   v-if="lineIndex < recipeToSearch.withoutIngredients.length - 1">Ja
                        </md-button>
                      </div>
                    </div>
                  </div>
                </div>

                &nbsp;
                <md-divider></md-divider>

                <div class="md-subheading">Kategooria</div>

                <div class="md-layout md-gutter">
                  <div class="md-layout-item md-size-15 md-medium-size-25 md-small-size-50"
                       v-for="(category, index) in recipeToSearch.categories">
                    <div class="md-layout">
                      <div class="md-layout-item md-size-80">
                        <md-autocomplete class="md-dense" v-model="recipeToSearch.categories[index]"
                                         :id="'category' + index"
                                         :md-options="autofill.categories">
                          <label>Kategooria</label>
                          <md-button v-if="recipeToSearch.categories.length > 1" :id="'category' + index + '-del'"
                                     @click="deleteCategoryRow(category)"
                                     class="md-dense md-icon-button md-input-action">
                            <md-icon md-src="/static/icons/baseline-clear-24px.svg"/>
                          </md-button>

                        </md-autocomplete>

                      </div>
                      <div class="md-layout-item md-size-20">
                        <md-button class="md-raised md-dense and-button md-accent"
                                   v-if="index == recipeToSearch.categories.length - 1" @click="addCategoryRow()"
                                   id="addCategory">Ja
                        </md-button>
                        <md-button class="md-disabled md-dense and-button"
                                   v-if="index < recipeToSearch.categories.length - 1">Ja
                        </md-button>
                      </div>
                    </div>
                  </div>
                </div>

                &nbsp;
                <md-divider></md-divider>

                <div class="md-layout md-gutter md-dense">
                  <div class="md-layout-item">
                    <div class="md-layout md-dense md-gutter md-alignment-bottom-left">
                      <div class="md-layout-item  md-size-80 md-small-size-70">
                        <md-field>
                          <label for="sort">Sorteeri:</label>
                          <md-select id="sort" v-model="recipeToSearch.sortOrder">
                            <md-option :value="0">Pealkirja järgi</md-option>
                            <md-option :value="1">Lisaja järgi</md-option>
                            <md-option :value="2">Lisamisaja järgi</md-option>
                          </md-select>

                        </md-field>
                      </div>
                      <div class="md-layout-item md-size-20 md-small-size-30">
                        <md-checkbox v-model="recipeToSearch.descending">Kahanevalt</md-checkbox>
                      </div>
                    </div>
                  </div>

                  <div class="md-layout-item md-size-25 md-medium-size-33 md-small-size-100">
                    <md-field>
                      <label for="resultsPerPage">Tulemusi lehel:</label>
                      <md-select id="resultsPerPage" v-model="recipeToSearch.resultsPerPage">
                        <md-option :value="10">10</md-option>
                        <md-option :value="50">50</md-option>
                        <md-option :value="0">Kõik</md-option>
                      </md-select>

                    </md-field>
                  </div>
                </div>
                &nbsp;
                <div class="md-layout">
                  <div class="md-layout-item" style="text-align: right;">
                    <md-button class="md-primary md-raised" id="searchbutton"
                               @click="search = JSON.parse(JSON.stringify(recipeToSearch)), startSearch(0)">Otsi
                    </md-button>
                  </div>
                </div>
                <div class="center">
                <md-card-expand-trigger>

                  <md-button class="md-icon-button" id="expand-button">
                    <md-icon md-src="/static/icons/baseline-keyboard_arrow_down-24px.svg"/>
                  </md-button>
                </md-card-expand-trigger>
                  </div>

              </md-card-content>
            </md-card-expand-content>
          </md-card-expand>
        </md-card>
      </form>
    </div>
    </md-content>
    <md-content id="recipelist">
      <div class="md-layout md-alignment-top-center md-dense">
        <md-card class="md-layout-item md-size-80 md-small-size-100">
          <div id="page" v-if="pages>0">
            <md-button class="md-icon-button md-raised" :disabled="search.resultPage === 0" id="firstpage" @click="startSearch(0)">
              <md-icon md-src="/static/icons/baseline-first_page-24px.svg"/>
            </md-button>
            <md-button class="md-icon-button md-raised" :disabled="search.resultPage === 0" id="previouspage" @click="startSearch(search.resultPage - 1)">
              <md-icon md-src="/static/icons/baseline-chevron_left-24px.svg"/>
            </md-button>
            <span>
              Page: {{ search.resultPage + 1 }}/{{ pages }}
            </span>
            <md-button class="md-icon-button md-raised" :disabled="search.resultPage + 1 >= pages" id="nextpage" @click="startSearch(search.resultPage + 1)">
              <md-icon md-src="/static/icons/baseline-chevron_right-24px.svg"/>
            </md-button>
            <md-button class="md-icon-button md-raised" :disabled="search.resultPage + 1 >= pages" id="lastpage" @click="startSearch(pages-1)">
              <md-icon md-src="/static/icons/baseline-last_page-24px.svg"/>
            </md-button>

            <md-divider></md-divider>
          </div>

          <md-list class="md-double-line">
            <md-list-item v-for="recipe in shownRecipes" :to="'/recipe/' + recipe.id">
              <div class="recipe-list-image-div">
              <img v-if="recipe.pictureName" :id="'recipe' + recipe.id + 'image'" :src="'images/' + recipe.id + '/2RecipePicture.' + recipe.pictureName" class="recipe-list-image">
              <img v-else src="/static/icons/baseline-photo-24px.svg" class="recipe-list-image">
              </div>
              <div class="md-list-item-text">
                <span class="md-title" :id="'recipe' + recipe.id">{{ recipe.name }}</span>
                <div>Lisatud: {{ new Date(recipe.added).toLocaleDateString("et-ET") }} ({{ recipe.user.username }})
                  <div v-if="recipe.preparedHistory[0].preparedTime" :id="'recipe' + recipe.id + 'preparedtime'">Viimati valmistasin: {{ new Date(recipe.preparedHistory[0].preparedTime).toLocaleDateString("et-ET")}} </div>
                </div>

              </div>
              <rating-display :id="'recipe' + recipe.id + 'rating'" :userRating="recipe.rating[0].rating" :avgRating="recipe.averageRating"></rating-display>
            </md-list-item>
          </md-list>
        </md-card>
      </div>
    </md-content>
  </div>


</template>
<style scoped>
  #recipelist {
    text-align: center;
    margin-top: 48px;
  }

  #page {
    text-align: right;
    margin: 8px;
  }
  #page > .md-button {
    margin: 8px;
  }

  #page > span {
    margin: 8px;
    margin-top: 16px;
    position: relative;
    text-transform: uppercase;
    height: 36px;
    font-weight: 500;
    display: inline-block;
    vertical-align: bottom;
  }

  .or-button {
    margin-left: 0px;
    padding: 0px;
    min-width: 48px;
  }

  .or-text {
    margin-left: -16px;
    margin-top: 20px;
    padding: 0px;
    min-width: 24px;
  }

  .and-button {
    margin-top: 20px;
    padding: 0px;
    min-width: 48px;
  }

  .md-field {
    margin-bottom: 4px;
  }

  .or-list {
    padding: 0px;
  }

  .md-radio {
    margin-bottom: 0px;
  }

  .center {
    text-align: center;
  }

  .right {
    text-align: right;
  }

  .card-link:hover {
    text-decoration: inherit;
  }

  .recipe-list-image {
    height: 100px;
  }

  .recipe-list-image-div {
    width: 200px;
    text-align: center;
    margin-right: 16px;
  }


</style>
<script>
  import { getAutoFillData, findRecipes } from "../api.js";
  import { store } from "../datastore.js";
  import RatingDisplay from "./RatingDisplay.vue";

  export default {
    name: "search",
    data: function () {
      const myData = {
        searchShown: true,
        recipeToSearch: store.recipeToSearch,
        pages: 0,
        user: store.user,
        search: {resultPage: 0},
        shownRecipes: [],
        autofill: {
          names: [],
          users: [],
          searchIngredients: [],
          categories: [],
          sources: []
        }
      };
      myData.recipeToSearch.userId = myData.user.id;
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
    mounted: function () {
      findRecipes(this.filteredRecipe(this.recipeToSearch), (err, res) => {
        if (err) {
          console.log("Search failed");
        } else {
          this.shownRecipes = res.recipes;
        }
      });
    },
    methods: {
      startSearch: function (resultPage) {
        if (document.getElementsByClassName("md-expand-active").length > 0) {
          document.getElementById("expand-button").dispatchEvent(new MouseEvent("click"));
        }
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
    },
    components: {
      RatingDisplay
    }
  };
</script>
