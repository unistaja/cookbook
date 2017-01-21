<template>
  <div id="searchrecipes">
    <div id="recipelist">
      <ul >
        <li v-for="recipe in recipes"><router-link v-bind:to="'/recipe/' + recipe.id">{{ recipe.name }}</router-link> ({{ recipe.user.username }} {{ new Date(recipe.added) }})</li>
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



</style>
<script>
  import { getRecipes } from "../api.js";
  import { store } from "../datastore.js";

  export default{
    name: "searchrecipes",
    data: function () {
      return {
        recipes: []
      };
    },
    beforeRouteEnter (to, from, next) {
      getRecipes((err, res) => {
        if (err) {
          window.alert("Fetching recipes failed!");
          next(false);
        } else {
          next(vm => {
            vm.recipes = res;
          });
        }
      });
    },
    watch: {
      $route () {
        getRecipes((err, res) => {
          if (err) {
            window.alert("Fetching recipes failed!");
            this.error = err;
          } else {
            this.recipes = res;
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
