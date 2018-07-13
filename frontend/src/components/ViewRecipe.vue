<template>
  <div id="viewrecipe">
    <div id="recipeheader">
      <div><a @click="edit()" v-if="user.id === recipe.user.id" class="one-char-button edit-button">&#x270e</a> <h1> {{ recipe.name }} </h1>  </div>
      {{ recipe.source }}
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img v-show="recipe.pictureName || tempPictureName"  id="image1" :src="'images/' + recipe.id + '/1RecipePicture.' + recipe.pictureName" onclick="document.getElementById('mymodal').style.display='block'">
      </div>
      <p v-if="!recipe.pictureName">Lisa retseptile pilt:</p>
      <input type="file" name="file" formenctype="multipart/form-data" v-if="!recipe.pictureName" accept="image/*" @change="upload">
      <button v-if="tempPictureName" @click="savePicture">Salvesta pilt</button>
      <button v-if="tempPictureName" @click="deleteImage">Kustuta pilt</button>
      <div id="categories" v-if="recipe.categories && recipe.categories.length != 0">
        <h2>Kategooriad: </h2> {{ recipe.categories.map(x => x.name).join(", ") }}
      </div>
    </div>
    <div id="mymodal" class="modal">
      <span class="close" onclick="document.getElementById('mymodal').style.display='none'">&times;</span>
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
    <div id="preparedtime">
      Valmistatud:
        <ul v-for = "preparedDate in lastPrepared" >
          <li :id="'prepared'+preparedDate.id"> {{new Date(preparedDate.preparedTime).toLocaleDateString("et-ET", {day: "2-digit", month: "2-digit", year: "numeric"})}} <button :id="'changebutton'+preparedDate.id" @click="this.document.getElementById('newpreparedtime'+preparedDate.id).style.display='block', this.document.getElementById('savepreparedtime'+preparedDate.id).style.display='block'">Muuda</button><div v-if="errors.has('newpreparedtime'+preparedDate.id)" class="error">Valmistamiskorda ei saa lisada tulevikku.</div> <input :name="'newpreparedtime'+preparedDate.id" type="date" :id="'newpreparedtime'+preparedDate.id" v-validate="'before:today,true|date_format:YYYY-MM-DD|required'" class="editpreparedtime"/> <button class="editpreparedtime" :id="'savepreparedtime'+preparedDate.id" @click="saveDate(this.document.getElementById('newpreparedtime'+preparedDate.id).value, preparedDate.id)">Muuda kuupäeva</button> </li>
        </ul>
      <input name="today" :value="new Date().getFullYear() + '-' + ('0' + (new Date().getMonth()+1)).slice(-2) + '-' + ('0' + new Date().getDate()).slice(-2)" type="hidden">
      <input name="newpreparedtime0" type="date" id="newpreparedtime0" v-model="newDate" v-validate="'before:today,true|date_format:YYYY-MM-DD|required'"/>
      <div v-if="errors.has('newpreparedtime0')" class="error">Valmistamiskorda ei saa lisada tulevikku.</div>
      <button id="savedate" @click="saveDate(newDate, 0)">Salvesta uus kuupäev</button>
      <div v-show="preparedTimeSaved === true">Valmistamiskord salvestatud.</div>
    </div>
    <div id="rating" class="rating">
      <label>
        <input type="radio" id="ratinginput1" name="stars" v-model="newRating" @click="saveRating()" value="1" />
        <span class="icon" id="rating1" title="Ei taha enam kunagi süüa">★</span>
      </label>
      <label>
        <input type="radio" id="ratinginput2" name="stars" v-model="newRating" @click="saveRating()" value="2" />
        <span class="icon">★</span>
        <span class="icon" id="rating2" title="Kui on võimalik midagi muud süüa, siis seda ei sööks">★</span>
      </label>
      <label>
        <input type="radio" id="ratinginput3" name="stars" v-model="newRating" @click="saveRating()" value="3" />
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon" id="rating3" title="Kõlbab süüa, aga väga tihti ei tahaks">★</span>
      </label>
      <label>
        <input type="radio" id="ratinginput4" name="stars" v-model="newRating" @click="saveRating()" value="4" />
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon" id="rating4" title="Üsna hea, päris iga päev ei sööks">★</span>
      </label>
      <label>
        <input type="radio" id="ratinginput5" name="stars" v-model="newRating" @click="saveRating()" value="5" />
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon">★</span>
        <span class="icon" id="rating5" title="Super hea, võiks kogu aeg süüa">★</span>
      </label>
    </div>
    <div v-show="ratingSaved === true">Hinnang salvestatud.</div>
    <p id="averageRating" v-if="recipe.averageRating">Keskmine hinnang: {{recipe.averageRating.toFixed(1)}}</p>
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

  .error {
    color: red;
  }

  .rating {
    display: inline-block;
    position: relative;
    height: 50px;
    line-height: 50px;
    font-size: 50px;
  }

  .rating label {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    cursor: pointer;
  }

  .rating label:last-child {
    position: static;
  }

  .rating label:nth-child(1) {
    z-index: 5;
  }

  .rating label:nth-child(2) {
    z-index: 4;
  }

  .rating label:nth-child(3) {
    z-index: 3;
  }

  .rating label:nth-child(4) {
    z-index: 2;
  }

  .rating label:nth-child(5) {
    z-index: 1;
  }

  .rating label input {
    position: absolute;
    top: 0;
    left: 0;
    opacity: 0;
  }

  .rating label .icon {
    float: left;
    color: transparent;
  }

  .rating label:last-child .icon {
    color: #000;
  }

  .rating:not(:hover) label input:checked ~ .icon,
  .rating:hover label:hover input ~ .icon {
    color: #09f;
  }

  .rating label input:focus:not(:checked) ~ .icon:last-child {
    color: #000;
    text-shadow: 0 0 5px #09f;
  }

  .editpreparedtime {
    display: none;
  }


</style>
<script>
  import { getRecipe, uploadImage, saveImage, deleteTempImage, saveDate, saveRating } from "../api.js";
  import { store, getNewRecipe } from "../datastore.js";
  import Vue from "vue";
  import VeeValidate from "vee-validate";
  Vue.use(VeeValidate);

  export default{
    name: "viewrecipe",
    data: function () {
      return {
        recipe: getNewRecipe(),
        user: store.user,
        tempPictureName: "",
        newDate: null,
        lastPrepared: [],
        newRating: null,
        ratingId: 0,
        ratingSaved: false,
        preparedTimeSaved: false
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
    mounted: function () {
      this.$nextTick(function () {
        for (let i = 0; i < this.recipe.preparedHistory.length; i++) {
          if (this.recipe.preparedHistory[i].userId === this.user.id) {
            this.lastPrepared.push(this.recipe.preparedHistory[i]);
          }
        }
        for (let i = 0; i < this.recipe.rating.length; i++) {
          if (this.recipe.rating[i].userId === this.user.id) {
            this.newRating = this.recipe.rating[i].rating;
            this.ratingId = this.recipe.rating[i].id;
          }
        }
      });
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
        uploadImage(files[0], this.recipe.id, (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.tempPictureName = res.result;
            document.getElementById("image1").src = "images/temp/1" + this.tempPictureName;
            document.getElementById("image2").src = "images/temp/3" + this.tempPictureName;
          }
        });
      },
      savePicture: function () {
        let id = this.recipe.id;
        saveImage(id, this.tempPictureName.slice(this.tempPictureName.lastIndexOf(".") + 1), (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.recipe.pictureName = res.result;
            document.getElementById("image1").src = "images/" + id + "/1RecipePicture." + this.recipe.pictureName;
            document.getElementById("image2").src = "images/" + id + "/3RecipePicture." + this.recipe.pictureName;
            this.tempPictureName = "";
          }
        });
      },
      saveDate: function (date, id) {
        this.$validator.validate("newpreparedtime" + id);
        if (this.errors.has("newpreparedtime" + id)) {
          return;
        }
        saveDate(date, id, this.recipe.id, (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.preparedTimeSaved = true;
            let that = this;
            setTimeout(function () {
              that.preparedTimeSaved = false;
            }, 5000);
          }
        });
      },
      saveRating: function () {
        saveRating(this.newRating, this.ratingId, this.recipe.id, (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.ratingSaved = true;
            let that = this;
            setTimeout(function () {
              that.ratingSaved = false;
            }, 5000);
          }
        });
      },
      deleteImage: function () {
        deleteTempImage(this.tempPictureName, (err) => {
          if (err) {
            alert(err.message);
          } else {
            document.getElementsByName("file")[0].value = "";
            this.tempPictureName = "";
          }
        });
      }
    }
  };
</script>
