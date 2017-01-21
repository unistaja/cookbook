export function getRecipe (recipeId, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", "/api/recipe/" + recipeId);
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, JSON.parse(this.responseText));
      } catch (ex) {
        callback({message: "Retsepti laadimine ebaõnnestus. Proovi lehekülge uuendada."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.onerror = function () {
    callback({message: "Retsepti laadimine ebaõnnestus. Proovi lehekülge uuendada."});
  };
  xhr.send();
}

export function loadUser (callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", "/api/user");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(JSON.parse(this.responseText));
      } catch (ex) {
        console.log(ex);
        callback({});
      }
    } else {
      console.log(this.status);
      console.log(this.responseText);
      callback({});
    }
  };
  xhr.onerror = function () {
    console.log("error loading user");
    callback({});
  };
  xhr.send();
}

export function getRecipes (callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", "/api/recipe/find");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, JSON.parse(this.responseText));
      } catch (ex) {
        callback({message: "Retseptide laadimine ebaõnnestus. Proovi lehekülge uuendada."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.onerror = function () {
    callback({message: "Retseptide laadimine ebaõnnestus. Proovi lehekülge uuendada."});
  };
  xhr.send();
}
