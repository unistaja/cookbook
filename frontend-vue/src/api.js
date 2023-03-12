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

export function addUser (username, password, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/admin/addUser");
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.onload = function () {
    if (this.status === 200) {
      if (this.responseText === "") {
        callback(null);
      } else {
        callback(this.responseText);
      }
    } else {
      callback("Uue kasutaja loomine ebaõnnestus. Viga: " + this.responseText);
    }
  };
  xhr.onerror = function () {
    callback("Uue kasutaja loomine ebaõnnestus võrguvea tõttu. Proovi uuesti.");
  };
  xhr.send("username=" + username + "&password=" + password);
}

export function findRecipes (query, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/search");
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
  xhr.send(JSON.stringify(query));
}

export function uploadImage (image, id, callback) {
  const formData = new FormData();
  formData.append("file", image);
  if (id) {
    formData.append("id", id);
  }
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/image/upload");
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

export function saveImage (id, extension, callback) {
  const formData = new FormData();
  formData.append("id", id);
  formData.append("extension", extension);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/image/save");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, {result: this.responseText});
      } catch (ex) {
        callback({message: "Pildi salvestamine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

export function saveDate (date, id, recipeId, callback) {
  const formData = new FormData();
  formData.append("id", id);
  formData.append("recipeId", recipeId);
  formData.append("date", date);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/recipe/savedate");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, {result: this.responseText});
      } catch (ex) {
        callback({message: "Kuupäeva salvestamine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

export function saveRating (rating, recipeId, callback) {
  const formData = new FormData();
  formData.append("recipeId", recipeId);
  formData.append("rating", rating);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/recipe/saverating");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, {result: this.responseText});
      } catch (ex) {
        callback({message: "Hinnangu salvestamine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

export function deleteTempImage (name, callback) {
  const formData = new FormData();
  formData.append("name", name);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/image/deletetempimage");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, {result: this.responseText});
      } catch (ex) {
        callback({message: "Pildi kustutamine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

export function deleteSavedImage (id, callback) {
  const formData = new FormData();
  formData.append("id", id);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/image/deletesavedimage");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null);
      } catch (ex) {
        callback({message: "Pildi kustutamine ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

export function findPreparedTimes (id, callback) {
  const formData = new FormData();
  formData.append("recipeId", id);
  let xhr = new XMLHttpRequest();
  xhr.open("POST", "/api/recipe/findpreparedtimes");
  xhr.onload = function () {
    if (this.status === 200) {
      try {
        callback(null, JSON.parse(this.responseText));
      } catch (ex) {
        callback({message: "Valmistamiskordade otsing ebaõnnestus."});
      }
    } else {
      callback({errorCode: this.status, message: this.responseText});
    }
  };
  xhr.send(formData);
}

