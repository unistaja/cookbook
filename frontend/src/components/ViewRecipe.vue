<template>
  <div id="viewrecipe">
    <div id="recipeheader">
      <div><a @click="edit()" v-if="user.id === recipe.user.id" class="one-char-button edit-button">&#x270e</a> <h1> {{ recipe.name }} </h1>  </div>
      {{ recipe.source }}
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img v-show="recipe.pictureName || tempPictureName"  id="image1" :src="'images/' + recipe.id + '/1RecipePicture.' + recipe.pictureName" onclick="document.getElementById('myModal').style.display='block'">
      </div>
      <p v-if="!recipe.pictureName">Lisa retseptile pilt:</p>
      <input type="file" name="file" formenctype="multipart/form-data" v-if="!recipe.pictureName" accept="image/*" @change="upload">
      <button v-if="tempPictureName" @click="savePicture">Salvesta pilt</button>
      <div id="categories" v-if="recipe.categories && recipe.categories.length != 0">
        <h2>Kategooriad: </h2> {{ recipe.categories.map(x => x.name).join(", ") }}
      </div>
    </div>
    <div id="myModal" class="modal">
      <span class="close" onclick="document.getElementById('myModal').style.display='none'">&times;</span>
      <img id="image2" :src="'images/' + recipe.id + '/3RecipePicture.' + recipe.pictureName">
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

  .modal {
    display: none; /* Hidden by default */
    position: fixed; /* Stay in place */
    z-index: 1; /* Sit on top */
    padding-top: 100px; /* Location of the box */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.9); /* Black w/ opacity */
  }

  .close {
    position: absolute;
    top: 15px;
    right: 35px;
    color: #f1f1f1;
    font-size: 40px;
    font-weight: bold;
    transition: 0.3s;
  }



</style>
<script>
  import { getRecipe, uploadImage } from "../api.js";
  import { store, getNewRecipe } from "../datastore.js";

  export default{
    name: "viewrecipe",
    data: function () {
      return {
        recipe: getNewRecipe(),
        user: store.user,
        tempPictureName: ""
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
      },
      upload: function (e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length) {
          return;
        }
        uploadImage(files[0], (err, res) => {
          if (err) {
            alert("Pildi salvestamine ebaõnnestus");
          } else {
            this.tempPictureName = res.result;
            document.getElementById("image1").src = "images/temp/1" + this.tempPictureName;
          }
        });
      },
      savePicture: function () {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/recipe/saveimage");
        this.recipe.pictureName = this.tempPictureName.slice((this.tempPictureName.lastIndexOf(".") - 1 >>> 0) + 2);
        let id = this.recipe.id;
        let pictureName = this.recipe.pictureName;
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.onload = function () {
          if (this.status !== 200) {
            alert(this.message);
          }
          document.getElementById("image1").src = "images/" + id + "/1RecipePicture." + pictureName;
          document.getElementById("image2").src = "images/" + id + "/1RecipePicture." + pictureName;
        };
        xhr.onerror = function () {
          alert("Pildi salvestamine ebaõnnestus");
        };
        xhr.send(JSON.stringify([this.tempPictureName, this.recipe.id.toString()]));
        this.tempPictureName = "";
      },
      deleteImage: function () {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/recipe/deleteimage");
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.send(JSON.stringify([this.recipe.id.toString(), "RecipePicture." + this.recipe.pictureName]));
        this.recipe.pictureName = "";
      }
    }
  };
</script>
