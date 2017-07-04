<template>
  <div id="addrecipe">
    <div id="recipeheader">
      <input id="title" type="text" v-model="recipe.name" placeholder="Pealkiri"/>
      <input id="source" type="text" list="source-data" v-model="recipe.source" placeholder="Allikas"/>
      <datalist id="source-data">
        <option v-for="source in autofill.sources" :value="source">{{ source }}</option>
      </datalist>
    </div>


    <div id="ingredients">
      <datalist id="ingredient-data">
        <option v-for="ingredient in autofill.ingredients" :value="ingredient">{{ ingredient }}</option>
      </datalist>
      <datalist id="unit-data">
        <option v-for="unit in autofill.units" :value="unit">{{ unit }}</option>
      </datalist>
      <datalist id="list-name-data">
        <option v-for="name in autofill.listNames" :value="name">{{ name }}</option>
      </datalist>
      <div id="ingredient-list">
        <div v-for="(list, listIndex) in recipe.ingredientLists">
          <input type="text" list="list-name-data" v-focus v-model="list.name" :id="'list' + listIndex + '-name'" placeholder="Jaotise nimi" @keyup.enter="list.ingredientLines.length === 0 ? addRow(list) : ''"/>
          <a class="one-char-button delete-button" @click="deleteList(list)" :id="'list' + listIndex +'-del'">-</a>
          <ul>
            <li v-for="(line, lineIndex) in list.ingredientLines">
              <input class="amount-input" v-focus type="number" v-model.number="line.amount" :id="'list' + listIndex + '-line' + lineIndex + '-amt'" placeholder="Kogus"/>
              <input class="unit-input" type="text" list="unit-data" v-model="line.unit" :id="'list' + listIndex + '-line' + lineIndex + '-unit'" placeholder="Ühik"/>
              <input class="ingredient-input" type="text" list="ingredient-data" v-model="line.ingredient" :id="'list' + listIndex + '-line' + lineIndex + '-ingr'" placeholder="Koostisosa" @keyup.enter="lineIndex === list.ingredientLines.length - 1 ? addRow(list) : ''"/>
              <a class="one-char-button add-button" :id="'list' + listIndex + '-line' + lineIndex + '-addAlt'" @click="addAltRow(line)">&or;</a>
              <a class="one-char-button delete-button" :id="'list' + listIndex + '-line' + lineIndex + '-del'" @click="deleteRow(line, list)">-</a>
              <ul v-if="line.alternateLines.length > 0">
                <li v-for="(altLine, altLineIndex) in line.alternateLines">
                  <input class="amount-input" v-focus type="number" v-model.number="altLine.amount" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-amt'" placeholder="Kogus"/>
                  <input class="unit-input" type="text" v-model="altLine.unit" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-unit'" placeholder="Ühik"/>
                  <input class="ingredient-input" type="text" v-model="altLine.ingredient" placeholder="Alternatiivkoostisosa" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-ingr'" @keyup.enter="altLineIndex === line.alternateLines.length - 1 ? addAltRow(line) : ''"/>
                  <a class="one-char-button delete-button" @click="remove(line.alternateLines, altLine)" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-del'">-</a>
                </li>
              </ul>
            </li>
          </ul>
          <ul>
            <li><a @click="addRow(list)" class="one-char-button add-button" :id="'list' + listIndex + '-addLine'">+</a></li>
          </ul>
        </div>
        <a @click="addList()" class="one-char-button add-button" id="addList">+</a>
      </div>
    </div>
    <div id="instructions">
      <textarea v-model="recipe.instructions" placeholder="Juhised" ></textarea>
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img src="http://placehold.it/350x350"/>
      </div>

      <div id="categories">
        <datalist id="category-data">
          <option v-for="category in autofill.categories" :value="category.name">{{ category.name }}</option>
        </datalist>
        <div v-for="(category, index) in recipe.categories">
          <input type="text" list="category-data" v-focus v-model="category.name" :id= "'category' + index" placeholder="Kategooria" @keyup.enter="index === recipe.categories.length - 1 ? addCategoryRow() : ''" />
          <a @click="deleteCategoryRow(category)" class="one-char-button delete-button" :id="'category' + index + '-del'">-</a>
        </div>
        <div>
          <a @click="addCategoryRow()" class="one-char-button add-button" id="addCategory">+</a>
        </div>
      </div>
    </div>

    <div id="savebutton">
      <a id="save" @click="saveRecipe()">Salvesta muudatused</a>
      <router-link id="cancel" v-if="recipe.id" v-bind:to="'/recipe/' + recipe.id">Katkesta</router-link>
    </div>


  </div>
</template>
<style scoped>
  #addrecipe {
    display:flex;
    flex-flow: row wrap;
    align-items: stretch;
  }

  #recipeheader, #ingredients, #instructions, #pictureholder, #savebutton {
    flex: 1 100%;
    margin: 10px;
  }

  #recipeheader {
    order: 0;
  }

  #pictureholder {
    order: 1;
  }

  #ingredients {
    order: 2;
  }

  #instructions {
    order: 3;
  }

  #savebutton {
    order: 4;
  }

  #recipeheader {
    flex-flow: column;
    display:flex;
    align-items: center;
    justify-content: center;
  }

  #ingredients {
    flex-flow: column;
    display:flex;
    align-items: center;
  }

  #instructions {
    flex-flow:column;
    align-items: center;
    display: flex;
  }

  #pictureholder {
    flex-flow:column;
    align-items: center;
    display: flex;
  }

  textarea {
    flex: 1;
    width: 100%;
  }

  #title {
    font-size: 1.5em;
    width: 20em;
  }
  #source {
    width: 20em;
    margin-top: 5px;
  }

  .amount-input {
    width: 4em;
  }

  .unit-input {
    width: 3em;
  }

  .ingredient-input {
    width: 15em;
  }

  #categories {
    display: flex;
    text-align:left;
    flex-flow:column;
  }

  #categories > div {
    margin: 5px;
  }

  @media all and (min-width: 840px) {
    #pictureholder, #ingredients {
      flex: 1 auto;
    }

    #ingredients {
      order: 1;
    }

    #pictureholder {
      order: 2;
    }

    #instructions {
      order: 3;
    }
  }

  @media all and (min-width: 1200px) {
    #instructions {
      flex: 6 0px;
    }

    #ingredients {
      order: 1;
    }

    #pictureholder {
      order: 3;
    }

    #instructions {
      order: 2;
    }
  }

  #ingredient-list {
    text-align: left;
    width: 26em;
  }

  #ingredient-list li {
    margin: 5px 0px;
  }

  .one-char-button {
    font-weight: bold;
    color: #FFF;
    padding: 0.1em 0.5em;
    border-radius: 4px;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    transition-duration: 0.2s;
    user-select:none;
    cursor: pointer;
  }

  .delete-button {
    background: #990000;
    border: solid 1px #20538D;
  }

  .add-button {
    background: #009900;
    border: solid 1px #20538D;
  }


  #cancel, #save {
    padding: 10px 15px;
    text-decoration: none;
    background: #4479BA;
    color: #FFF;
    border-radius: 4px;
    border: solid 1px #20538D;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    transition-duration: 0.2s;
    user-select:none;
    margin: 10px;
    margin-top: 30px;
    cursor: pointer;
  }
  #save:hover {
    background: #356094;
    border: solid 1px #2A4E77;
    text-decoration: none;
  }
  #save:active {
      box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.6);
      background: #2E5481;
      border: solid 1px #203E5F;
  }


</style>
<script>
  import { store } from "../datastore.js";
  import { getAutoFillData } from "../api.js";

  export default {
    name: "addrecipe",
    data: function () {
      const myData = {
        recipe: store.recipeToEdit,
        autofill: {
          units: [],
          ingredients: [],
          listNames: [],
          categories: [],
          sources: []
        }
      };
      store.resetRecipe();
      return myData;
    },
    watch: {
      $route () {
        this.recipe = store.recipeToEdit;
        store.resetRecipe();
      }
    },
    directives: {
      focus: {
        inserted: function (el) {
          el.focus();
        }
      }
    },
    mounted: function () {
      document.getElementById("title").focus();
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
      routeToRecipe: function (recipeId) {
        this.$router.push("/recipe/" + recipeId);
      },
      saveRecipe: function () {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/recipe");
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        let routeToRecipe = this.routeToRecipe;
        xhr.onload = function () {
          if (this.status === 200) {
            try {
              routeToRecipe(JSON.parse(this.responseText).recipeId);
            } catch (ex) {
              console.log(ex);
            }
          }
        };
        xhr.send(JSON.stringify(this.filteredRecipe()));
      },
      filteredRecipe: function () {
        const newRecipe = JSON.parse(JSON.stringify(this.recipe)); // do deep copy
        newRecipe.ingredientLists = newRecipe.ingredientLists.filter(x => x.name);
        newRecipe.ingredientLists.forEach(list => {
          list.ingredientLines = list.ingredientLines.filter(x => x.ingredient);
          list.ingredientLines.forEach(line => {
            line.alternateLines = line.alternateLines.filter(x => x.ingredient);
          });
        });
        newRecipe.categories = newRecipe.categories.filter(x => x.name);
        return newRecipe;
      },
      remove: function (arr, el) {
        var index = arr.indexOf(el);
        if (index > -1) {
          arr.splice(index, 1);
        }
      },
      addList: function () {
        this.recipe.ingredientLists.push({name: null, ingredientLines: []});
      },
      addRow: function (list) {
        list.ingredientLines.push({alternateLines: []});
      },
      addCategoryRow: function () {
        this.recipe.categories.push({});
      },
      addAltRow: function (line) {
        line.alternateLines.push({});
      },
      deleteList: function (list) {
        this.remove(this.recipe.ingredientLists, list);
      },
      deleteRow: function (row, list) {
        if (row.alternateLines.length > 0 && row.alternateLines[0].ingredient) {
          const newMain = row.alternateLines[0];
          row.amount = newMain.amount;
          row.unit = newMain.unit;
          row.ingredient = newMain.ingredient;
          row.alternateLines.splice(0, 1);
        } else {
          this.remove(list.ingredientLines, row);
        }
      },
      deleteCategoryRow: function (row) {
        this.remove(this.recipe.categories, row);
      },
      logData: function () {
        console.log(this.recipe);
      }
    }
  };
</script>
