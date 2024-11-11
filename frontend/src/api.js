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
  console.log("Sending recipe");
  const response = await fetch('/api/recipe', {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipeToSend)
  });
  console.log(response);
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