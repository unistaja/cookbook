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