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

export function getAutoFillData (callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", "/api/recipe/autofill");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, JSON.parse(this.responseText));
      } catch (ex) {
        callback({});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.onerror = function () {
    callback({});
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

export function changePassword (oldPassword, newPassword, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/user");
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.onload = function () {
    if (this.status === 200) {
      if (this.responseText === "OK") {
        callback(null);
      } else {
        callback("Parooli muutmine ebaõnnestus. Kontrolli vana parooli õigsust.");
      }
    } else {
      callback("Parooli muutmine ebaõnnestus. Kontrolli vana parooli õigsust.");
    }
  };
  xhr.onerror = function () {
    callback("Parooli muutmine ebaõnnestus võrguvea tõttu. Proovi uuesti.");
  };
  xhr.send("oldPassword=" + oldPassword + "&newPassword=" + newPassword);
}
