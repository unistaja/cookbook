import { useState, useEffect } from "react";
import { useNavigate, useParams } from 'react-router-dom';
import { getRecipe } from "../api";
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import IconButton from '@mui/material/IconButton';
import Stack from "@mui/material/Stack";
import EditableRating from "../components/EditableRating";
import PreparedHistoryInput from "../components/PreparedHistoryInput";
import dayjs from 'dayjs';
import Link from "@mui/material/Link";
import PreparedHistoryModal from "../components/PreparedHistoryModal";
import RecipeImageSection from "../components/RecipeImageSection";
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import EditIcon from '@mui/icons-material/Edit';
import { Fragment } from "react";
import { CategoriesSection } from "../components/CategoriesSection";



export default function RecipeView ({user}) {
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState(null);
  const [historyModalOpen, setHistoryModalOpen] = useState(false);

  let { recipeId } = useParams();

  function onRatingChange(newRating) {
    setRecipe({
      ...recipe,
      rating: [{
        rating: newRating
      }]
    });
  }

  function onPreparedDateAdded(newPreparedDate) {
    if (recipe.preparedHistory.length === 0 || newPreparedDate.isAfter(dayjs(recipe.preparedHistory[0].preparedTime))) {
      setRecipe({
        ...recipe,
        preparedHistory: [{
          preparedTime: newPreparedDate
        }]
      });
    }
  }

  function onImageAdded(newImageName) {
    setRecipe({
      ...recipe,
      pictureName: newImageName
    });
  }

  function onCategoriesAdded(newCategories) {
    setRecipe({
      ...recipe,
      categories: newCategories.concat(recipe.categories)
    });
  }

  useEffect(() => {
    getRecipe(recipeId)
      .then((data) => setRecipe(data))
      .catch((error) => {
        alert(error.message)
        console.error("Error:", error);
      });
  }, [recipeId, setRecipe]);

  if (!recipe) {
    return null;
  }

  return (
    <Container align="center">
      <Stack spacing={1} padding={2} maxWidth="700px" align="center" alignItems="center">
        <Stack direction="row"><Typography variant="h4">{recipe.name}</Typography>{user.isAdmin || user.id === recipe.user.id ? <IconButton onClick={() => navigate(`/edit-recipe/${recipeId}`)}><EditIcon/></IconButton> : null}</Stack>
        {recipe.source && <Typography variant="subtitle1" sx={{marginTop: 0}}>{recipe.source}</Typography>}
        <EditableRating recipeId={recipeId} value={recipe.rating.length > 0 ? recipe.rating[0].rating : recipe.averageRating} onChange={onRatingChange}/>
        {(recipe.amount || recipe.prepareTime) && <Stack alignItems="center" direction="row" gap={2}>
            {recipe.amount && <><RestaurantIcon fontSize="small"/><Typography variant="body2" sx={{marginTop: 0}}>{recipe.amount}</Typography></>}
            {recipe.prepareTime && <><AccessTimeIcon fontSize="small"/><Typography variant="body2" sx={{marginTop: 0}}>{recipe.prepareTime}</Typography></>}
          </Stack>
        }
        {recipe.preparedHistory[0]?.preparedTime && 
          <Typography variant="body2">
            Viimati valmistasin: {" "}
            <Link component="button" onClick={() => setHistoryModalOpen(true)}>
              {dayjs(recipe.preparedHistory[0].preparedTime).format('DD.MM.YYYY')}
            </Link>
          </Typography>
        }
        <PreparedHistoryInput recipeId={recipeId} onChange={onPreparedDateAdded}/>
        <Stack direction={{xs: "column-reverse", sm: "row"}} justifyContent="space-between" spacing={2} width="100%">
          <Stack align="left">
          {recipe.ingredientLists.map((list, listIdx) => (
            <Fragment key={`list-${listIdx}`}>
              <Typography key={`list-${listIdx}`} variant="subtitle1" sx={{fontWeight: "bold"}}>{list.name}:</Typography>
              {list.ingredientLines.map((line, lineIdx) => (
                <Typography key={`list-${listIdx}-line-${lineIdx}`}>
                  {line.amount} {line.unit} {line.ingredient} {line.alternateLines.length > 0 && "või "} {line.alternateLines.map((altLine) => `${altLine.amount ?? ''} ${altLine.unit ?? ''} ${altLine.ingredient}`).join(" või ")}
                </Typography>
              ))}
            </Fragment>
          ))}
          </Stack>
          <Box sx={{alignSelf: {xs: "center", sm: "auto"}, width:"200px"}}>
            <RecipeImageSection recipeId={recipeId} imageName={recipe.pictureName} onUpdateImage={onImageAdded}/>
            <CategoriesSection categories={recipe.categories} recipeId={recipeId} onCategoryUpdate={onCategoriesAdded}/>
          </Box>
        </Stack>
        <Typography variant="body1" align="left" paddingTop="1em" whiteSpace="pre-line">{recipe.instructions}</Typography>
    </Stack>
    <PreparedHistoryModal recipeId={recipeId} recipeTitle={recipe.name} open={historyModalOpen} onClose={() => setHistoryModalOpen(false)}/>
    </Container>
  );
}