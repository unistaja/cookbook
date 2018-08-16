<template>
  <div id="viewrecipe">
    <div id="recipeheader" class="md-body-1">
      <div class="md-headline">
        {{ recipe.name }} <md-button class="md-raised md-dense edit-button md-accent"
                      v-if="user.id === recipe.user.id" @click="edit()">&#x270e
      </md-button>
      </div>
      {{ recipe.source }}
      <div id="rating-display">
        <div id="rating" class="rating">
          <label>
            <input type="radio" id="ratinginput0" name="stars" />
            <md-icon  :id="'rating'+n" v-for="n in 5" md-src="/static/icons/baseline-star_border-24px.svg" class="rating-star-style rating-star-opaque"></md-icon>
          </label>
          <label>
            <input type="radio" id="ratinginput1" name="stars" v-model="rating" @click="saveRating(1)" value="1" />
            <md-icon id="rating1" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon>
            <md-tooltip md-direction="bottom">
              Ei taha enam kunagi süüa
            </md-tooltip>
          </label>
          <label>
            <input type="radio" id="ratinginput2" name="stars" v-model="rating" @click="saveRating(2)" value="2" />
            <md-icon :id="'rating'+n" v-for="n in 2" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon>
            <md-tooltip md-direction="bottom">
              Kui on võimalik midagi muud süüa, siis seda ei sööks
            </md-tooltip>
          </label>
          <label>
            <input type="radio" id="ratinginput3" name="stars" v-model="rating" @click="saveRating(3)" value="3" />
            <md-icon v-for="n in 3" :id="'rating'+n" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon>
            <md-tooltip md-direction="bottom">
              Kõlbab süüa, aga väga tihti ei tahaks
            </md-tooltip>
          </label>
          <label>
            <input type="radio" id="ratinginput4" name="stars" v-model="rating" @click="saveRating(4)" value="4" />
            <md-icon v-for="n in 4" :id="'rating'+n" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon>
            <md-tooltip md-direction="bottom">
              Üsna hea, päris iga päev ei sööks
            </md-tooltip>
          </label>
          <label>
            <input type="radio" id="ratinginput5" name="stars" v-model="rating" @click="saveRating(5)" value="5" />
            <md-icon v-for="n in 5" :id="'rating'+n" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon>
          </label>
        </div>
        <div class="non-hover">
          <md-icon v-for="n in fullStarCount" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon><md-icon v-for="n in halfStarCount" md-src="/static/icons/baseline-star_half-24px.svg" class="rating-star-style"></md-icon><md-icon v-for="n in emptyStarCount" md-src="/static/icons/baseline-star_border-24px.svg" class="rating-star-style"></md-icon>
        </div>
        <div v-show="ratingSaved === true">Hinnang salvestatud.</div>
      </div>
      <div v-if="recipe.preparedHistory[0] && recipe.preparedHistory[0].preparedTime" class="md-caption">Viimati valmistasin: <a @click="findPreparedTimes();" id="preparedTimes">{{ formatDate(recipe.preparedHistory[0].preparedTime)}} </a></div>
      <div id="preparedTimesModal" class="modal" v-if="displayPreparedTimesModal">
        <span class="close" @click="displayPreparedTimesModal=false">&times;</span>
        <div class="md-layout md-alignment-top-center md-dense">
          <md-card class="md-layout-item md-size-50 md-xsmall-size-70 md-elevation-8">
            <md-card-header>
              <div class="md-title">Minu "{{recipe.name}}" valmistuskorrad</div>
            </md-card-header>
            <md-card-content>
              <md-list>
                <md-list-item v-for="preparedDate in recipe.preparedHistory" :key="preparedDate.id" >

                  <div class="md-list-item-text date-list">
                    <div :id="'prepared'+preparedDate.id">
                      {{ formatDate(preparedDate.preparedTime) }} <md-button :id="'changebutton' + preparedDate.id" class="md-raised md-dense edit-button md-accent" @click="this.document.getElementById('editpreparedtime'+preparedDate.id).style.display='block';this.document.getElementById('prepared'+preparedDate.id).style.display='none'">&#x270e
                    </md-button>
                    </div>
                    <div class="editpreparedtime" :id="'editpreparedtime' + preparedDate.id">
                      <input :name="'newpreparedtime'+preparedDate.id" v-once :value="formatDateForDatePicker(new Date(preparedDate.preparedTime))" type="date" :id="'newpreparedtime'+preparedDate.id" v-validate="'before:today,true|date_format:YYYY-MM-DD|required'"/>
                      <md-button class="md-raised md-dense edit-button md-accent" :id="'savepreparedtime'+preparedDate.id" @click="saveDate(this.document.getElementById('newpreparedtime'+preparedDate.id).value, preparedDate.id)">Salvesta</md-button>
                      <md-button class="md-raised md-dense edit-button md-accent" :id="'deletepreparedtime'+preparedDate.id" @click="deletePreparedTime(preparedDate.id)">Kustuta</md-button>
                      <div v-if="errors.has('newpreparedtime'+preparedDate.id)" class="error">Valmistamiskorda ei saa lisada tulevikku.</div>
                    </div>


                  </div>
                </md-list-item>
              </md-list>
            </md-card-content>
          </md-card>
        </div>
      </div>
      <div id="preparedtime">
        <input name="today" :value="formatDateForDatePicker(new Date())" type="hidden">
        <input name="newpreparedtime0" type="date" id="newpreparedtime0" v-model="newDate" v-validate="'before:today,true|date_format:YYYY-MM-DD|required'"/>
        <md-button id="savedate" class="md-raised md-dense edit-button md-accent" @click="saveDate(newDate, 0)">Valmistasin
        </md-button>
        <div v-if="errors.has('newpreparedtime0')" class="error">Valmistamiskorda ei saa lisada tulevikku.</div>
        <div v-show="preparedTimeSaved === true">Valmistamiskord salvestatud.</div>
      </div>
    </div>

    <div id="pictureholder">
      <div id="picture">
        <img v-show="recipe.pictureName || tempPictureName"  id="image1" :src="'images/' + recipe.id + '/1RecipePicture.' + recipe.pictureName" @click="displayImageModal=true">
        <img v-if="!(recipe.pictureName || tempPictureName)" src="/static/icons/baseline-photo-24px.svg" class="recipe-image">
      </div>
      <div v-if="!recipe.pictureName">
        <md-field>
          <label>Lisa pilt:</label>
          <md-file name="file" id="file" formenctype="multipart/form-data" v-if="!recipe.pictureName" accept="image/*" @change="upload">
          </md-file>
        </md-field>
        <md-button class="md-raised md-dense edit-button md-accent" v-if="tempPictureName" @click="savePicture">Salvesta pilt</md-button>
        <md-button class="md-raised md-dense edit-button md-accent" v-if="tempPictureName" @click="deleteImage">Kustuta pilt</md-button>
      </div>

      <div id="categories" v-if="recipe.categories && recipe.categories.length != 0">
        <span class="md-title">Kategooriad: </span> {{ recipe.categories.join(", ") }}
      </div>
    </div>
    <div id="myModal" class="modal" v-show="displayImageModal">
      <span class="close" @click="displayImageModal=false">&times;</span>
      <img id="image2" :src="'images/' + recipe.id + '/3RecipePicture.' + recipe.pictureName">
    </div>

    <div id="ingredients">
      <div id="ingredient-list">
        <div v-for="list in recipe.ingredientLists">
          <span class="md-title">{{ list.name }}: </span>
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
    height: 24px;
    min-width: 24px;
    text-transform: none;
  }

  #recipeheader > div > * {
    margin: 0px;
  }

  .modal {
    position: fixed; /* Stay in place */
    z-index: 4; /* Sit on top */
    text-align: center;
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
    top: 55px;
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
    display: none;
    position: relative;
  }

  #rating-display:hover .rating {
    display: inline-flex;
  }

  #rating-display:hover .non-hover {
    display: none;
  }

  .non-hover {
    display: inline-flex;
  }

  .rating label {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    cursor: pointer;
  }



  .rating label:nth-child(1) {
    z-index: 1;
  }

  .rating label:nth-child(2) {
    z-index: 6;
  }

  .rating label:nth-child(3) {
    z-index: 5;
  }

  .rating label:nth-child(4) {
    z-index: 4;
  }

  .rating label:nth-child(5) {
    z-index: 3;
  }

  .rating label:nth-child(6) {
    z-index: 2;
  }
  .rating label:first-child {
    position: static;
  }

  .rating label input {
    position: absolute;
    top: 0;
    left: 0;
    opacity: 0;
    height: 100%;
  }

  .rating label .rating-star-style {
    opacity: 0;
  }

  .rating-star-style {
    float: left;
    display: inline-flex;
  }

  .rating label .rating-star-opaque {
    opacity: 100;
  }

  .rating:hover label:hover .rating-star-style {
    opacity: 100;
  }

  #preparedTimesModal .date-list {
    align-items: center;
    width: auto;
  }
  #preparedTimesModal .date-list * {
    align-items: center;
    width: auto;
  }

  .editpreparedtime {
    display: none;
  }

  .recipe-image {
    height: 300px;
  }




</style>
<style>
  .md-ripple {
    z-index: 3;
  }
</style>
<script>
  import { getRecipe, uploadImage, saveImage, deleteTempImage, saveDate, saveRating, findPreparedTimes } from "../api.js";
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
        newDate: new Date().getFullYear() + "-" + ("0" + (new Date().getMonth() + 1)).slice(-2) + "-" + ("0" + new Date().getDate()).slice(-2),
        rating: null,
        ratingSaved: false,
        preparedTimeSaved: false,
        displayPreparedTimesModal: false,
        displayImageModal: false
      };
    },
    computed: {
      fullStarCount: function () {
        if (this.rating) {
          return parseInt(this.rating);
        }
        if (this.recipe.averageRating) {
          return Math.floor(this.recipe.averageRating);
        }
        return 0;
      },
      halfStarCount: function () {
        if (this.rating) {
          return 0;
        }
        if (this.recipe.averageRating) {
          return Math.ceil(this.recipe.averageRating) - Math.floor(this.recipe.averageRating);
        }
        return 0;
      },
      emptyStarCount: function () {
        return 5 - this.fullStarCount - this.halfStarCount;
      }
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
        if (this.recipe.rating[0].userId) {
          this.rating = this.recipe.rating[0].rating;
          this.ratingId = this.recipe.rating[0].id;
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
            if (id > 0) {
              this.recipe.preparedHistory.find(x => x.id === id).preparedTime = new Date(date).getTime();
              document.getElementById("editpreparedtime" + id).style.display = "none";
              document.getElementById("prepared" + id).style.display = "block";
            }
            let that = this;
            setTimeout(function () {
              that.preparedTimeSaved = false;
            }, 5000);
          }
        });
      },
      saveRating: function (rating) {
        saveRating(rating, this.recipe.id, (err, res) => {
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
      },
      findPreparedTimes: function () {
        findPreparedTimes(this.recipe.id, (err, res) => {
          if (err) {
            alert(err.message);
          } else {
            this.recipe.preparedHistory.splice(0, this.recipe.preparedHistory.length);
            for (let i = 0; i < res.length; i++) {
              this.recipe.preparedHistory.push(res[i]);
            }
          }
          this.displayPreparedTimesModal = true;
        });
      },
      deletePreparedTime: function (id) {
        let xhr = new XMLHttpRequest();
        let that = this;
        const formData = new FormData();
        formData.append("id", id);
        xhr.open("POST", "api/recipe/deletedate");
        xhr.onload = function () {
          if (this.status === 200) {
            that.recipe.preparedHistory.splice(that.recipe.preparedHistory.findIndex(x => x.id === id));
          } else {
            alert(this.responseText);
          }
        };
        xhr.send(formData);
      },
      formatDate: function (timestamp) {
        return new Date(timestamp).toLocaleDateString("et-ET", {day: "2-digit", month: "2-digit", year: "numeric"});
      },
      formatDateForDatePicker: function (date) {
        return date.getFullYear() + "-" + ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
      }
    }
  };
</script>
