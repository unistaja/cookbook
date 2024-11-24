import { useEffect, useState } from "react";
import WeekPlan from "../components/WeekPlan";
import { getMenuRecipes, removeMenuRecipe } from "../api";
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid2';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import SimpleRecipeCard from '../components/SimpleRecipeCard';
import DeleteIcon from '@mui/icons-material/Delete';

export default function MenuPlanView() {
  const [menuRecipes, setMenuRecipes] = useState([]);

  function onRecipeAddedToMenu(recipe) {
    setMenuRecipes([recipe, ...menuRecipes]);
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
        <WeekPlan onRecipeAddedToMenu={onRecipeAddedToMenu}/>
                 
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );
}