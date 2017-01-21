import {loadUser} from "./api.js";

export function getNewRecipe () {
  return {
    name: null,
    source: null,
    instructions: null,
    user: {},
    ingredientLists: [
      {name: "Koostis", ingredientLines: [{}]}
    ],
    categories: [{}]
  };
}

export const store = {
  user: {username: null, id: null},
  recipeToEdit: getNewRecipe(),
  resetRecipe: function () {
    this.recipeToEdit = getNewRecipe();
  }
};

loadUser(user => { store.user = user; });
