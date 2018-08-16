import {loadUser} from "./api.js";

export function getNewRecipe () {
  return {
    name: null,
    source: null,
    instructions: null,
    pictureName: "",
    user: {},
    ingredientLists: [
      {name: "Koostis", ingredientLines: [{alternateLines: []}]}
    ],
    categories: [""],
    preparedHistory: [],
    rating: [null],
    averageRating: null
  };
}

export function getNewSearch () {
  return {
    name: null,
    source: null,
    hasPicture: false,
    resultsPerPage: 0,
    resultPage: 0,
    sortOrder: 0,
    descending: false,
    username: null,
    withIngredients: [{alternateLines: []}],
    withoutIngredients: [""],
    categories: [""],
    userId: null,
    hasPrepared: null
  };
}

export const store = {
  user: {username: null, id: null},
  recipeToEdit: getNewRecipe(),
  recipeToSearch: getNewSearch(),
  resetSearch: function () {
    this.recipeToSearch = getNewSearch();
  },
  resetRecipe: function () {
    this.recipeToEdit = getNewRecipe();
  }
};

loadUser(user => { store.user = user; });
