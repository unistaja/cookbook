import { useState, useEffect } from 'react';
import { getViewedRecipes} from "../api";
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid2';
import Typography from '@mui/material/Typography';
import SimpleRecipeCard from './SimpleRecipeCard';

export default function ViewedRecipesWidget() {
  const [viewedRecipes, setViewedRecipes] = useState([]);

  useEffect(() => {
    getViewedRecipes()
      .then((data) => setViewedRecipes(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [setViewedRecipes]);

  if (!viewedRecipes.length) {
    return null;
  }
  return (
    <Grid container spacing={2}  >
      <Grid size="grow"></Grid>
      <Grid>
        <Paper variant="outlined" sx={{align: "center", padding: "20px"}}>
        <Typography variant="h5">Viimati vaadatud</Typography>
        <Grid container spacing={2}  >
        {viewedRecipes.map((recipe) => (
          <Grid key={recipe.recipeId} sx={{  display: "flex", alignItems: "stretch", justifyContent: "center", flexWrap: "wrap"}}>
            <SimpleRecipeCard recipe={recipe}/>
          </Grid>
        ))}
        </Grid>
        </Paper>
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );
}