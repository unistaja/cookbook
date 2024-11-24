import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import CardActionArea from '@mui/material/CardActionArea';
import CardActions from '@mui/material/CardActions';
import RecipeImage from './RecipeImage';
import {
  Link as RouterLink
} from 'react-router-dom';

export default function SimpleRecipeCard({recipe, children}) {
  return (
  <Card elevation={4} sx={{display: "flex", flexDirection: "column", alignItems: "stretch"}}>
    <CardActionArea to={`/recipe/${recipe.recipeId}`} component={RouterLink} sx={{width: "180px", textAlign: "center", paddingTop: "10px"}}>
      <RecipeImage recipeId={recipe.recipeId} imgName={recipe.pictureName} sx={{height: "100px"}} />
      <CardContent sx={{ mt: "auto", padding: "0px", paddingTop: "4px" }}>
        <Typography variant="subtitle2" sx={{ verticalAlign: "center" }}>{recipe.name}</Typography>
      </CardContent>
    </CardActionArea>
    {children && <CardActions disableSpacing sx={{ mt: "auto", padding: "2px", paddingTop: "0px" }}>{children}</CardActions>}
  </Card>
  )

}