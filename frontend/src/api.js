export const getLoggedInUser = async () => {
  const response = await fetch('/api/user');
  if (!response.ok) {
    throw new Error("Invalid response when fetching user");
  }
  return response.json();
};

export const getAutoFillData = async () => {
  const response = await fetch('/api/recipe/autofill');
  if (!response.ok) {
    console.log("Invalid response when fetching autofill data");
    return {};
  }
  return response.json();
};

export const getCategoriesAutoFillData = async () => {
  const response = await fetch('/api/recipe/autofill/categories');
  if (!response.ok) {
    console.log("Invalid response when fetching autofill data");
    return [];
  }
  return response.json();
};

export const addCategories = async (categories, recipeId) => {
  const formData = new FormData();
  formData.append("categories", categories);
  formData.append("recipeId", recipeId);
  const response = await fetch('/api/recipe/addcategories', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    throw Error("Kategooriate lisamine ebaõnnestus!");
  }
}

export const addRecipe = async (recipeToSend) => {
  const response = await fetch('/api/recipe', {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipeToSend)
  });
  if (!response.ok) {
    alert("Retsepti lisamine ebaõnnestus")
    throw Error("Retsepti lisamine ebaõnnestus!");
  }
  return response.json();
};

export const getRecipe = async (recipeId) => {
  const response = await fetch('/api/recipe/' + recipeId);
  if (!response.ok) {
    throw new Error("Retsepti laadimine ebaõnnestus. Proovi lehekülge uuendada.");
  }
  return response.json();
}

export const findRecipeIdsByName = async (name) => {
  const formData = new FormData();
  formData.append("name", name);
  const response = await fetch('/api/search/findByName', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    throw Error("Sama nimega retseptide kontrollimine ebaõnnestus!");
  }
  return response.json();
}

export const findRecipes = async (query) => {
  const response = await fetch('/api/search', {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
    },
    body: JSON.stringify(query)
  });
  if (!response.ok) {
    alert("Otsing ebaõnnestus")
    throw Error("Otsing ebaõnnestus");
  }
  return response.json();
}

export const getViewedRecipes = async () => {
  const response = await fetch('/api/widgets/viewedRecipes');
  if (!response.ok) {
    console.log("Invalid response when fetching viewed recipe data");
    return {};
  }
  return response.json();
};

export const uploadImage = async (image, recipeId) => {
  const formData = new FormData();
  formData.append("file", image);
  if (recipeId) {
    formData.append("id", recipeId);
  }
  const response = await fetch('/api/image/upload', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Pildi lisamine ebaõnnestus")
    throw Error("Pildi lisamine ebaõnnestus!");
  }
  return response.text();
}

export const saveImage = async (recipeId, extension) => {
  const formData = new FormData();
  formData.append("id", recipeId);
  formData.append("extension", extension);
  const response = await fetch('/api/image/save', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Pildi salvestamine ebaõnnestus")
    throw Error("Pildi salvestamine ebaõnnestus!");
  }
}

export const deleteTempImage = async (name) => {
  const formData = new FormData();
  formData.append("name", name);
  const response = await fetch('/api/image/deletetempimage', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Pildi eemaldamine ebaõnnestus")
    throw Error("Pildi eemaldamine ebaõnnestus!");
  }
}

export const deleteSavedImage = async (id) => {
  const formData = new FormData();
  formData.append("id", id);
  const response = await fetch('/api/image/deletesavedimage', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Pildi eemaldamine ebaõnnestus")
    throw Error("Pildi eemaldamine ebaõnnestus!");
  }
}

export const saveRating = async (rating, recipeId) => {
  const formData = new FormData();
  formData.append("recipeId", recipeId);
  formData.append("rating", rating);
  const response = await fetch('/api/recipe/saverating', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Hinnangu salvestamine ebaõnnestus")
    throw Error("Hinnangu salvestamine ebaõnnestus!");
  }
}

export const saveDate = async (date, id, recipeId) => {
  const formData = new FormData();
  formData.append("id", id);
  formData.append("recipeId", recipeId);
  formData.append("date", date);
  const response = await fetch('/api/recipe/savedate', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Kuupäeva salvestamine ebaõnnestus.")
    throw Error("Kuupäeva salvestamine ebaõnnestus.");
  }
}

export const deletePreparedDate = async (preparedDateId) => {
  const formData = new FormData();
  formData.append("id", preparedDateId);
  const response = await fetch('/api/recipe/deletedate', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Kuupäeva kustutamine ebaõnnestus.")
    throw Error("Kuupäeva kustutamine ebaõnnestus.");
  }
}

export const findPreparedTimes = async (recipeId) => {
  const formData = new FormData();
  formData.append("recipeId", recipeId);
  const response = await fetch('/api/recipe/findpreparedtimes', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    throw Error("Valmistuskordade otsing ebaõnnestus.");
  }
  return response.json();
}

export const changePassword = async (oldPassword, newPassword) => {
  const formData = new FormData();
  formData.append("oldPassword", oldPassword);
  formData.append("newPassword", newPassword);
  const response = await fetch('/api/user', {
    method: "POST",
    body: formData
  });
  if (!response.ok || await response.text() !== "OK") {
    alert("Parooli muutmine ebaõnnestus. Kas vana parooli oli õige?")
    throw Error("Parooli muutmine ebaõnnestus.");
  }
}

export const addUser = async (username, password) => {
  const formData = new FormData();
  formData.append("username", username);
  formData.append("password", password);
  const response = await fetch('/api/admin/addUser', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Kasutaja lisamine ebaõnnestus")
    throw Error("Kasutaja lisamine ebaõnnestus.");
  }
}

export const changeUserPassword = async (username, password) => {
  const formData = new FormData();
  formData.append("username", username);
  formData.append("password", password);
  const response = await fetch('/api/admin/changeUserPassword', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Kasutaja parooli muutmine ebaõnnestus")
    throw Error("Kasutaja parooli muutmine ebaõnnestus.");
  }
}

export const addRecipeToMenu = async (recipeId) => {
  const formData = new FormData();
  formData.append("recipeId", recipeId);
  const response = await fetch('/api/menu/addRecipe', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Retsepti menüüsse ebaõnnestus")
    throw Error("Retsepti menüüsse ebaõnnestus");
  }
}

export const removeMenuRecipe = async (recipeId) => {
  const formData = new FormData();
  formData.append("recipeId", recipeId);
  const response = await fetch('/api/menu/removeRecipe', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Retsepti menüüst eemaldamine ebaõnnestus")
    throw Error("Retsepti menüüst eemaldamine ebaõnnestus");
  }
}

export const getMenuRecipes = async () => {
  const response = await fetch('/api/menu/get');
  if (!response.ok) {
    console.log("Invalid response when fetching menu recipe data");
    return [];
  }
  return response.json();
};

export const getRemoteRecipe = async (url) => {
  const formData = new FormData();
  formData.append("url", url);
  const response = await fetch('/api/getRemoteRecipe', {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    alert("Retsepti laadimine ebaõnnestus")
    throw Error("Retsepti laadimine ebaõnnestus!");
  }
  const responseText = await response.text();
  if (!responseText) {
    return {};
  }
  return JSON.parse(responseText);
};