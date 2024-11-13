import { useState, useEffect } from 'react';
import { getViewedRecipes} from "../api";
import Paper from '@mui/material/Paper';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Grid from '@mui/material/Grid2';
import Typography from '@mui/material/Typography';
import CardActionArea from '@mui/material/CardActionArea';
import RecipeImage from './RecipeImage';

export default function ViewedRecipesWidget() {
  const [viewedRecipes, setViewedRecipes] = useState([]);

  useEffect(() => {
    getViewedRecipes()
      .then((data) => setViewedRecipes(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [setViewedRecipes]);

  function goToRecipe(recipeId) {
    window.location.href=`/index-vue.html#/recipe/${recipeId}`;
  }
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
          <Card elevation={4}>
            <CardActionArea onClick={() => goToRecipe(recipe.recipeId)} sx={{width: "180px", textAlign: "center", paddingTop: "10px"}}>
              <RecipeImage recipeId={recipe.recipeId} imgName={recipe.pictureName} />
              <CardContent>
                <Typography variant="subtitle2">{recipe.name}</Typography>
              </CardContent>
            </CardActionArea>
          </Card>
          </Grid>
        ))}
        </Grid>
        </Paper>
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );
}