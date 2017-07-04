<template>
  <div id="viewrecipe">
    <div id="recipeheader">
      <div><a @click="edit()" v-if="user.id === recipe.user.id" class="one-char-button edit-button">&#x270e</a> <h1> {{ recipe.name }} </h1>  </div>
      {{ recipe.source }}
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img src="http://placehold.it/350x350"/>
      </div>

      <div id="categories" v-if="recipe.categories && recipe.categories.length != 0">
        <h2>Kategooriad: </h2> {{ recipe.categories.map(x => x.name).join(", ") }}
      </div>
    </div>


    <div id="ingredients">
      <div id="ingredient-list">
        <div v-for="list in recipe.ingredientLists">
          <h2>{{ list.name }}: </h2>
          <ul >
            <li v-for="line in list.ingredientLines">
              {{ line.amount }} {{ line.unit }} {{ line.ingredient }}
              <template v-if="line.alternateLines.length > 0">
                või
              <ul>
                <li v-for="(altLine, index) in line.alternateLines">
                  {{ altLine.amount }} {{ altLine.unit }} {{ altLine.ingredient }} <template v-if="index < line.alternateLines.length - 1">või</template>
                </li>
              </ul>
              </template>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div id="instructions">
      {{ recipe.instructions }}
    </div>


  </div>
</template>
<style scoped>
  #viewrecipe {
    display:flex;
    flex-flow: row wrap;
    align-items: stretch;
  }

  #recipeheader, #ingredients, #instructions, #pictureholder {
    flex: 1 100%;
    margin: 10px;
  }

  #recipeheader {
    order: 0;
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
    text-align:left;
    white-space: pre-line;
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
    cursor: pointer;
  }

  .edit-button {
    background: #009900;
    border: solid 1px #20538D;
  }

  #recipeheader > div > * {
    display: inline-block;
    margin: 10px;
  }


</style>
<script>
  import { getRecipe } from "../api.js";
  import { store, getNewRecipe } from "../datastore.js";

  export default{
    name: "viewrecipe",
    data: function () {
      return {
        recipe: getNewRecipe(),
        user: store.user
      };
    },
    beforeRouteEnter (to, from, next) {
      getRecipe(to.params.id, (err, res) => {
        if (err) {
          window.alert("Fetching recipe failed!");
          next(false);
        } else {
          next(vm => {
            vm.recipe = res;
          });
        }
      });
    },
    watch: {
      $route () {
        getRecipe(this.$route.params.id, (err, res) => {
          if (err) {
            window.alert("Fetching recipe failed!");
            this.error = err;
          } else {
            this.recipe = res;
          }
        });
      }
    },
    methods: {
      edit: function () {
        store.recipeToEdit = this.recipe;
        this.$router.push("/editrecipe");
      }
    }
  };
</script>
