<template>
  <div id="addrecipe">
    <div>
      <input type="text" v-model="recipe.name" placeholder="Pealkiri"/>
      <input type="text" v-model="recipe.source" placeholder="Allikas"/>
    </div>
    <div>
      <div v-for="list in recipe.ingredientLists">
        <input type="text" v-model="list.name" placeholder="Jaotise nimi"/><a @click="deleteList(list)">Kustuta</a>
        <div v-for="line in list.ingredientLines">
          <input type="number" v-model="line.amount" placeholder="Kogus"/>
          <input type="text" v-model="line.unit" placeholder="Ühik"/>
          <input type="text" v-model="line.ingredient" placeholder="Koostisosa"/>
          <a @click="deleteRow(line, list)">Kustuta</a>
        </div>
        <a @click="addRow(list)">Lisa rida</a>
      </div>
      <a @click="addList()">Lisa jaotis</a>
    </div>
    <div>
      <textarea v-model="recipe.instructions" placeholder="Juhised" ></textarea>
    </div>

    <div>
      <div v-for="category in recipe.categories">
        <input type="text" v-model="category.name" placeholder="Kategooria"/>
        <a @click="deleteCategoryRow(category)">Kustuta</a>
      </div>
      <a @click="addCategoryRow(list)">Lisa kategooria</a>
    </div>

    <div>
      <a id="save" @click="saveRecipe()">Salvesta muudatused</a>
    </div>
    <!--<a @click="logData()">Logi</a>-->


  </div>
</template>
<style scoped>
  #addrecipe {
    text-align: left;
  }
  div {
    display: block;
  }

  a {
    display: inline-block;
  }

  #save {
    padding: 10px 15px;
    background: #4479BA;
    color: #FFF;
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
    border-radius: 4px;
    border: solid 1px #20538D;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    -webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    -moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    -webkit-transition-duration: 0.2s;
    -moz-transition-duration: 0.2s;
    transition-duration: 0.2s;
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
  }
  #save:hover {
    background: #356094;
    border: solid 1px #2A4E77;
    text-decoration: none;
  }
  #save:active {
      -webkit-box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.6);
      -moz-box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.6);
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
          categories: []
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
        this.remove(list, row);
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
