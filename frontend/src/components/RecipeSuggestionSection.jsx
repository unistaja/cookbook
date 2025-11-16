import { useEffect, useState } from "react";
import { findRecipes } from "../api";
import Grid from "@mui/material/Grid2";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import SimpleRecipeCard from './SimpleRecipeCard';
import IconButton from "@mui/material/IconButton"; 
import RefreshIcon from '@mui/icons-material/Refresh';
import PostAddIcon from '@mui/icons-material/PostAdd';

export default function RecipeSuggestionsSection({onAddRecipeToMenu, suggestionConfiguration}) {
  const [recipes, setRecipes] = useState([]);

  async function fetchNewRecipe(currentRecipeId) {
    let newRecipe = await findRecipes({
      ...suggestionConfiguration.config,
      resultsPerPage: 1
    });
    if (newRecipe.recipes.length === 0 || recipes.find(r => r.id === newRecipe.recipes[0].id)) {
      return;
    }
    setRecipes(recipes.map(recipe => recipe.id === currentRecipeId ? newRecipe.recipes[0] : recipe));
  }

  useEffect(() => {
    findRecipes(suggestionConfiguration.config)
      .then((data) => setRecipes(data.recipes))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [suggestionConfiguration, setRecipes]);
  return (
      <Paper variant="outlined" sx={{align: "center", padding: "20px", margin: "10px"}}>
      <Typography variant="h5">{suggestionConfiguration.name}</Typography>
      <Grid container spacing={2} mt={2} >
          { recipes.map(recipe => (
            <SimpleRecipeCard recipe={{...recipe, recipeId: recipe.id}} key={recipe.id}>
              <IconButton color="primary" onClick={ () => fetchNewRecipe(recipe.id) }>
                  <RefreshIcon/>
              </IconButton>
              <IconButton color="primary" onClick={ () => onAddRecipeToMenu(recipe) } sx={{marginLeft: "auto"}}>
                  <PostAddIcon/>
              </IconButton>
            </SimpleRecipeCard>
            )
          )}
      </Grid>
      </Paper>
  );
}