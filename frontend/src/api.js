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