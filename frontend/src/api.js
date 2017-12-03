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
      } else if (this.responseText === "PASSWORD_EMPTY") {
        callback("Palun sisesta uus parool");
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

export function findRecipes (recipe, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/recipe/search");
  xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
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
  xhr.send(JSON.stringify(recipe));
}

export function uploadImage (image, callback) {
  const formData = new FormData();
  formData.append("file", image);
  formData.append("name", image.name.slice((Math.max(0, image.name.lastIndexOf(".")) || Infinity) + 1));
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/recipe/images");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, {result: this.responseText});
      } catch (ex) {
        callback({message: "Pildi üleslaadimine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.onerror = function () {
    alert("Pildi salvestamine ebaõnnestus.");
  };
  xhr.send(formData);
}

