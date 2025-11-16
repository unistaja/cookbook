import { useEffect, useState } from "react";
import { getMenuRecipes, removeMenuRecipe, addRecipeToMenu } from "../api";
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid2';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import SimpleRecipeCard from '../components/SimpleRecipeCard';
import DeleteIcon from '@mui/icons-material/Delete';
import RecipeSuggestionsList from "../components/RecipeSuggestionsList";

export default function MenuPlanView() {
  const [menuRecipes, setMenuRecipes] = useState([]);

  async function onAddRecipeToMenu(recipe) {
    if (menuRecipes.find(r => r.recipeId === recipe.id)) {
      return;
    }
    await addRecipeToMenu(recipe.id);
    setMenuRecipes([{...recipe, recipeId: recipe.id}, ...menuRecipes]);
  }

  async function removeRecipeFromMenu(recipeId) {
    await removeMenuRecipe(recipeId);
    setMenuRecipes(menuRecipes.filter(recipe => recipe.recipeId !== recipeId));
  }

  useEffect(() => {
    getMenuRecipes()
      .then((data) => setMenuRecipes(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [setMenuRecipes]);

  return (
    <Grid container spacing={2}>
      <Grid size="grow"></Grid>
      <Grid>
        <Paper variant="outlined" sx={{align: "center", padding: "20px", margin: "10px"}}>
        <Typography variant="h5">Minu menüü:</Typography>
        <Grid container spacing={2} mt={2} >
        {menuRecipes.map((recipe) => (
          <Grid key={recipe.recipeId} sx={{  display: "flex", alignItems: "stretch", justifyContent: "center", flexWrap: "wrap"}}>
            <SimpleRecipeCard recipe={recipe}>
              <IconButton color="error" onClick={() => removeRecipeFromMenu(recipe.recipeId) } sx={{marginLeft: "auto"}}>
                  <DeleteIcon/>
              </IconButton>
            </SimpleRecipeCard>
          </Grid>
        ))}
        </Grid>
        </Paper>
        <RecipeSuggestionsList onAddRecipeToMenu={onAddRecipeToMenu}/>
                 
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );
}