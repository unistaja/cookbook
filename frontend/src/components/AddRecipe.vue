<template>
  <div id="addrecipe">
    <div id="recipeheader">
      <div v-show="errors.has('title')" class="error">Palun sisestage retsepti pealkiri.</div>
      <input id="title" v-validate="'required'"  type="text" v-model="recipe.name" placeholder="Pealkiri" name="title">
      <input id="source" type="text" list="source-data" v-model="recipe.source" placeholder="Allikas"/>
      <datalist id="source-data">
        <option v-for="source in autofill.sources" :value="source">{{ source }}</option>
      </datalist>
    </div>

    <div id="ingredients">
      <datalist id="ingredient-data">
        <option v-for="ingredient in Array.from(new Set(Object.keys(autofill.ingredients).concat(Object.values(autofill.ingredients))))" :value="ingredient">{{ ingredient }}</option>
      </datalist>
      <datalist id="unit-data">
        <option v-for="unit in autofill.units" :value="unit">{{ unit }}</option>
      </datalist>
      <datalist id="list-name-data">
        <option v-for="name in autofill.listNames" :value="name">{{ name }}</option>
      </datalist>
      <datalist id="searchingredient-data">
        <option v-for="searchIngredient in Array.from(new Set(Object.values(autofill.ingredients)))" :value="searchIngredient">{{ searchIngredient }}</option>
      </datalist>
      <div id="ingredient-list">
        <span v-if="message" :class="[error ? 'error' : 'success']">{{ message }}</span>
        <div v-for="(list, listIndex) in recipe.ingredientLists">
          <div v-show="errors.has('list' + listIndex + '-name')" class="error">{{errors.first('list' + listIndex + '-name')}}</div>
          <input type="text" list="list-name-data" v-focus v-model="list.name" :name="'list' + listIndex + '-name'" :id="'list' + listIndex + '-name'" placeholder="Jaotise nimi" @keyup="checkList(list, listIndex), checkListsExist()" @focusout="checkList(list, listIndex)" @keyup.enter="list.ingredientLines.length === 0 ? addRow(list) : ''"/>
          <a class="one-char-button delete-button" @click="deleteList(list), checkListsExist()" :id="'list' + listIndex +'-del'">-</a>
          <ul>
            <li v-for="(line, lineIndex) in list.ingredientLines">
              <div> <span v-if="errors.has('list'+listIndex+'-line'+lineIndex+'-amt')" class="error"> Palun sisestage koostisosa kogus sobivas vormingus.</span><span v-if="errors.has('list'+listIndex+'-line'+lineIndex+'-ingredient')" class="error">Palun sisestage koostisosa nimi. </span><span v-if="errors.has('list'+listIndex+'-line'+lineIndex+'-searchingredient')" class="error">Palun sisestage otsingukoostisosa nimi. </span></div>
              <input v-focus class="amount-input" @keyup="updateInfo()" type="text" v-validate = "{regex : /\d+[.,]*\d*-*\d*[.,]*\d*/}" v-model="line.amount" :id="'list' + listIndex + '-line' + lineIndex + '-amt'" placeholder="Kogus" v-bind:ref="'list' + listIndex + 'line' + lineIndex + 'amt'" :name="'list'+listIndex+'-line'+lineIndex+'-amt'"/>
              <input class="unit-input" @keyup="updateInfo()" :name="'list' + listIndex + '-line' + lineIndex + '-unit'" type="text" list="unit-data" v-model="line.unit" :id="'list' + listIndex + '-line' + lineIndex + '-unit'" placeholder="Ühik"/>
              <input v-validate="(line.unit && /\S/.test(line.unit)) || (line.amount && /\S/.test(line.amount))? 'required' : ''" @keyup="checkList(list, listIndex), updateInfo(), checkSearchIngredient(line, line.ingredient)" @focusout="checkList(list, listIndex), updateInfo()" :name="'list'+listIndex+'-line'+lineIndex+'-ingredient'" class="ingredient-input" type="text" list="ingredient-data"  v-model="line.ingredient" :id="'list' + listIndex + '-line' + lineIndex + '-ingr'" placeholder="Koostisosa"/>
              <input v-validate="line.ingredient && /\S/.test(line.ingredient)? 'required' : ''" @keyup="checkList(list, listIndex), updateInfo()" @focusout="checkList(list, listIndex), updateInfo()" :name="'list'+listIndex+'-line'+lineIndex+'-searchingredient'" class="ingredient-input" type="text" list="searchingredient-data"  v-model="line.searchIngredient" :id="'list' + listIndex + '-line' + lineIndex + '-searchingr'" placeholder="Otsingukoostisosa" @keyup.enter="lineIndex === list.ingredientLines.length - 1 ? addRow(list) : ''"/>
              <button v-if="listIndex == 0" @click="this.document.getElementById('modal').style.display = 'block'">?</button>
              <div id="modal" class="modal">
                <div class="modal-content">
                  <span class="close" @click="this.document.getElementById('modal').style.display = 'none'">&times;</span>
                  <p>Otsingukoostisosa lahtrisse käib selle rea koostisosa ainsuse nimetavas käändes.</p>
                </div>
              </div>
              <a class="one-char-button add-button" :id="'list' + listIndex + '-line' + lineIndex + '-addAlt'" @click="addAltRow(line)">&or;</a>
              <a class="one-char-button delete-button" :id="'list' + listIndex + '-line' + lineIndex + '-del'" @click="deleteRow(line, list)">-</a>
              <ul v-if="line.alternateLines.length > 0">
                <li v-for="(altLine, altLineIndex) in line.alternateLines">
                  <div><span v-if="errors.has('list'+listIndex+'-line'+lineIndex+'-altLine'+altLineIndex+'-ingredient')" class="error">Palun sisestage koostisosa nimi.</span><span v-if="errors.has('list'+listIndex+'-line'+lineIndex+'-altLine'+altLineIndex+'-searchingredient')" class="error"> Palun sisestage otsingukoostisosa nimi.</span></div>
                  <input class="amount-input" @keyup="updateInfo()" v-validate = "{regex : /\d+[.,]*\d*-*\d*[.,]*\d*/}" v-focus type="text" v-model="altLine.amount" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-amt'" placeholder="Kogus"/>
                  <input class="unit-input" @keyup="updateInfo()" type="text" v-model="altLine.unit" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-unit'" placeholder="Ühik"/>
                  <input class="ingredient-input" list="ingredient-data" v-validate="(altLine.unit && /\S/.test(altLine.unit)) || (altLine.amount && /\S/.test(altLine.amount)) ? 'required' : ''" :name="'list'+listIndex+'-line'+lineIndex+'-altLine'+altLineIndex+'-ingredient'" type="text" v-model="altLine.ingredient" placeholder="Alternatiivkoostisosa" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-ingr'" @keyup="checkSearchIngredient(altLine, line.ingredient)"/>
                  <input class="ingredient-input" list="searchingredient-data" v-validate="altLine.ingredient && /\S/.test(altLine.ingredient)? 'required' : ''" :name="'list'+listIndex+'-line'+lineIndex+'-altLine'+altLineIndex+'-searchingredient'" type="text" v-model="altLine.searchIngredient" placeholder="Otsingukoostisosa" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-searchingr'" @keyup.enter="altLineIndex === line.alternateLines.length - 1 ? addAltRow(line) : ''"/>
                  <a class="one-char-button delete-button" @click="remove(line.alternateLines, altLine)" :id="'list' + listIndex + '-line' + lineIndex + '-altLine' + altLineIndex + '-del'">-</a>
                </li>
              </ul>
            </li>
          </ul>

          <ul>
            <li><a @click="addRow(list)" class="one-char-button add-button" :id="'list' + listIndex + '-addLine'">+</a></li>
          </ul>
        </div>
        <a @click="addList(), checkListsExist()" class="one-char-button add-button" id="addList">+</a>
      </div>
    </div>
    <div id="instructions">
      <div v-show="errors.has('instructions')" class="error">Palun sisestage retsepti juhised.</div>
      <textarea v-validate="'required'" v-model="recipe.instructions" placeholder="Juhised" name="instructions" ></textarea>
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img v-if="image" id="recipeimage" :src="'images/temp/1' + image" />
        <img v-else-if="recipe.pictureName" :src="'images/' + recipe.id + '/1RecipePicture.' + recipe.pictureName"/>
      </div>
      <input id="image-input" type="file" name="file" formenctype="multipart/form-data" @change="onFileChange" accept="image/*">
      <button v-if="recipe.pictureName || image" @click="deleteImage()">Kustuta pilt</button>
      <div id="categories">
        <datalist id="category-data">
          <option v-for="category in autofill.categories" :value="category.name">{{ category.name }}</option>
        </datalist>
        <div v-for="(category, index) in recipe.categories">
          <input type="text" list="category-data" v-focus v-model="category.name" :id="'category' + index" placeholder="Kategooria" @keyup.enter="index === recipe.categories.length - 1 ? addCategoryRow() : ''" />
          <a @click="deleteCategoryRow(category)" class="one-char-button delete-button" :id="'category' + index + '-del'">-</a>
        </div>
        <div>
          <a @click="addCategoryRow()" class="one-char-button add-button" id="addCategory">+</a>
        </div>
      </div>
    </div>
    <div id="savebutton">
      <a id="save" @click="validateBeforeSubmit()">Salvesta muudatused</a>
      <router-link id="cancel" v-if="recipe.id" v-bind:to="'/recipe/' + recipe.id" >Katkesta</router-link>
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
    width: 4em;
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
  .error {
    color: red;
  }
  #recipeimage {
    height: 350px;
    width: 350px;
  }

  .modal {
    position: fixed;
    z-index: 1;
    padding-top: 100px;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    display: none;
  }
  .modal-content {
    background-color: #fefefe;
    margin: 15% auto;
    padding: 20px;
    border: 1px solid #888;
    width: 80%;
    border-radius: 25px;
  }
  .close {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
  }
  .close:hover,
  .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }

</style>
<script>
  import { store } from "../datastore.js";
  import { getAutoFillData, loadUser, uploadImage, deleteTempImage, deleteSavedImage } from "../api.js";
  import Vue from "vue";
  import VeeValidate from "vee-validate";
  Vue.use(VeeValidate);

  export default {
    name: "addrecipe",
    data: function () {
      const myData = {
        recipe: store.recipeToEdit,
        pictureTimeOut: null,
        image: "",
        user: loadUser(res => {
          this.user = res.username;
        }),
        autofill: {
          units: [],
          ingredients: [],
          listNames: [],
          categories: [],
          sources: []
        },
        saveButtonClicked: false,
        error: false,
        message: null
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
    mounted: function () {
      document.getElementById("title").focus();
    },
    beforeRouteLeave (to, from, next) {
      if (this.image) {
        this.deleteImage();
      }
      next();
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
      validateBeforeSubmit: function () {
        this.saveButtonClicked = true;
        this.checkListsExist();
        let listIndex = 0;
        for (const list of this.recipe.ingredientLists) {
          this.checkList(list, listIndex);
          listIndex++;
        }
        return this.$validator.validateAll().then(() => {
          if (!(this.errors.any() || this.error)) {
            this.saveRecipe();
          }
        });
      },
      routeToRecipe: function (recipeId) {
        this.$router.push("/recipe/" + recipeId);
      },
      saveRecipe: function () {
        if (this.image) {
          this.recipe.pictureName = this.image;
        }
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/recipe");
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        let routeToRecipe = this.routeToRecipe;
        xhr.onload = function () {
          if (this.status === 200) {
            try {
              routeToRecipe(JSON.parse(this.responseText).recipeId);
            } catch (ex) {
              if (ex.message.includes("Unexpected token")) {
                alert("Teie konto on tõenäoliselt välja logitud. Palun logige teises aknas sisse et vältida sisestatud info kaduma minekut.");
              } else {
                alert("Retsepti salvestamine ebaõnnestus. Palun proovige uuesti.");
              }
              console.log(ex);
            }
          } else {
            alert(JSON.parse(this.responseText).message);
          }
        };
        xhr.send(JSON.stringify(this.filteredRecipe()));
        this.image = "";
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
        let index = arr.indexOf(el);
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
        this.errors.clear();
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
      },
      updateInfo: function () {
        this.$forceUpdate();
      },
      checkList: function (list, listIndex) {
        if (this.saveButtonClicked) {
          this.errors.remove("list" + listIndex + "-name");
          if (!list.name || !/\S/.test(list.name)) {
            this.errors.add("list" + listIndex + "-name", "Palun sisestage jaotise nimi.");
            return;
          }
          let emptyLines = 0;
          for (const line of list.ingredientLines) {
            if (!line.ingredient || !/\S/.test(line.ingredient)) {
              emptyLines++;
            }
          }
          if (emptyLines === list.ingredientLines.length) {
            this.errors.add("list" + listIndex + "-name", "Siestage jaotisele vähemalt 1 koostisosa.");
            return;
          }
          this.errors.remove("list" + listIndex + "-name");
        }
      },
      checkListsExist: function () {
        if (this.saveButtonClicked) {
          if (this.recipe.ingredientLists.length === 0) {
            this.error = true;
            this.message = "Retseptil peab olema vähemalt 1 jaotis.";
            this.updateInfo();
            return;
          }
          this.message = null;
          this.error = false;
        }
      },
      onFileChange: function (e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length) {
          return;
        }
        uploadImage(files[0], null, (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.image = res.result;
          }
        });
      },
      deleteImage: function () {
        if (!this.image && this.recipe.pictureName && !confirm("Kas soovite eelnevalt salvestatud pilti jäädavalt kustutada?")) {
          return;
        }

        if (this.image) {
          deleteTempImage(this.image, (err) => {
            if (err) {
              alert(err.message);
            } else {
              this.image = "";
              document.getElementsByName("file")[0].value = "";
            }
          });
          this.image = "";
        } else if (this.recipe.pictureName) {
          deleteSavedImage(this.recipe.id, (err) => {
            if (err) {
              alert(err.message);
            } else {
              this.recipe.pictureName = "";
              document.getElementsByName("file")[0].value = "";
            }
          });
        }
      },
      checkSearchIngredient: function (line, value) {
        if (this.autofill.ingredients[value]) {
          line.searchIngredient = this.autofill.ingredients[value];
        } else if (Object.values(this.autofill.ingredients).includes(value)) {
          line.searchIngredient = value;
        }
      }

    }
  };
</script>


