<template>
  <div id="addrecipe">
    <div id="recipeheader">
      <input id="title" type="text" v-model="recipe.name" placeholder="Pealkiri"/>
      <input id="source" type="text" v-model="recipe.source" placeholder="Allikas"/>
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img src="http://placehold.it/350x350"/>
      </div>

      <div id="categories">
        <div v-for="category in recipe.categories">
          <input type="text" v-model="category.name" placeholder="Kategooria"/>
          <a @click="deleteCategoryRow(category)" class="one-char-button delete-button">-</a>
        </div>
        <div>
          <a @click="addCategoryRow()" class="one-char-button add-button">+</a>
        </div>
      </div>
    </div>


    <div id="ingredients">
      <div id="ingredient-list">
        <div v-for="list in recipe.ingredientLists">
          <input type="text" v-model="list.name" placeholder="Jaotise nimi"/>
          <a class="one-char-button delete-button" @click="deleteList(list)">-</a>
          <ul v-for="line in list.ingredientLines">
            <li><input class="amount-input" type="number" v-model="line.amount" placeholder="Kogus"/>
            <input class="unit-input" type="text" v-model="line.unit" placeholder="Ühik"/>
            <input class="ingredient-input" type="text" v-model="line.ingredient" placeholder="Koostisosa"/>
            <a class="one-char-button delete-button" @click="deleteRow(line, list)">-</a>
          </ul>
          <ul>
            <li><a @click="addRow(list)" class="one-char-button add-button">+</a>
          </ul>
        </div>
        <a @click="addList()" class="one-char-button add-button">+</a>
      </div>
    </div>
    <div id="instructions">
      <textarea v-model="recipe.instructions" placeholder="Juhised" ></textarea>
    </div>

    <div id="savebutton">
      <a id="save" @click="saveRecipe()">Salvesta muudatused</a>
    </div>




    <!--<a @click="logData()">Logi</a>-->


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

  @media all and (min-width: 825px) {
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
    width: 25em;
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
  }

  .delete-button {
    background: #990000;
    border: solid 1px #20538D;
  }

  .add-button {
    background: #009900;
    border: solid 1px #20538D;
  }


  #save {
    padding: 10px 15px;
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
  export default{
    name: "addrecipe",
    data: function () {
      return {
        recipe: {
          name: null,
          instructions: null,
          ingredientLists: [
            {name: "Koostis", ingredientLines: [{amount: null, unit: null, ingredient: null}]}
          ],
          categories: [{}]
        }
      };
    },
    methods: {
      saveRecipe: function () {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/recipe");
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onready = function (event) {
          console.log(xhr.status);
          console.log(xhr.responseText);
        };
        xhr.send(JSON.stringify(this.recipe));
      },
      remove: function (arr, el) {
        var index = arr.indexOf(el);
        if (index > -1) {
          arr.splice(index, 1);
        }
      },
      addList: function () {
        this.recipe.ingredientLists.push({name: null, ingredientLines: [{amount: null, unit: null, ingredient: null}]});
      },
      addRow: function (list) {
        list.ingredientLines.push({amount: null, unit: null, ingredient: null});
      },
      addCategoryRow: function () {
        this.recipe.categories.push({name: null});
      },
      deleteList: function (list) {
        this.remove(this.recipe.ingredientLists, list);
      },
      deleteRow: function (row, list) {
        this.remove(list.ingredientLines, row);
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
