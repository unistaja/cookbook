import * as React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Chip from '@mui/material/Chip';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import ImageUploadInput from '../components/ImageUploadInput';
import AddRecipeModal from '../components/AddRecipeModal';
import { getAutoFillData, addRecipe, deleteTempImage, deleteSavedImage, getRecipe } from "../api";
import RecipeImage from '../components/RecipeImage';

const amountValidator = /^\d+(?:[.,]\d+)?(?:-\d+(?:[.,]\d+)?)?$/;
export default function AddRecipeView() {
  let { recipeId } = useParams();
  const navigate = useNavigate();
  const sampleRecipe = `1 kg kartuleid
vett
soola

Koori kartulid ja keeda soolaga maitsestatud vees pehmeks.
  `;
  const [autoFillData, setAutoFillData] = useState({});
  const [recipeName, setRecipeName] = useState('');
  const [recipeAmount, setRecipeAmount] = useState('');
  const [recipePrepareTime, setRecipePrepareTime] = useState('');
  const [recipeSource, setRecipeSource] = useState('');
  const [recipeContent, setRecipeContent] = useState('');
  const [recipeCategories, setRecipeCategories] = useState([]);
  const [parsedIngredientLists, setParsedIngredientLists] = useState([]);
  const [parsedInstructions, setParsedInstructions] = useState('');
  const [submitModalOpen, setSubmitModalOpen] = useState(false);
  const [existingRecipeImage, setExistingRecipeImage] = useState(null);
  const [recipeImage, setRecipeImage] = useState(null);
  const availableCategories = autoFillData.categories ?? [];
  const recipeSources = autoFillData.sources ?? [];

  function prepareAddRecipe() {
    if (!recipeName) {
      alert("Palun sisesta retsepti pealkiri!");
      return;
    }
    if (!recipeContent) {
      alert("Palun sisesta retsept!");
      return;
    }
    if (!recipeCategories.length) {
      alert("Palun sisesta vähemalt üks kategooria!");
      return;
    }
    const parsedRecipe = parseRecipeContent(recipeContent, autoFillData);
    setParsedIngredientLists(parsedRecipe.ingredientLists);
    setParsedInstructions(parsedRecipe.instructions);
    setSubmitModalOpen(true);
  }

  async function submitRecipe() {
    const recipeToSubmit = {
      ...(recipeId ? {id: recipeId} : null),
      name: recipeName,
      source: recipeSource || null,
      amount: recipeAmount || null,
      prepareTime: recipePrepareTime || null,
      instructions: parsedInstructions,
      ingredientLists: parsedIngredientLists,
      categories: recipeCategories,
    };
    if (recipeImage) {
      recipeToSubmit.pictureName = recipeImage;
    } else if (existingRecipeImage) {
      recipeToSubmit.pictureName = existingRecipeImage;
    }
    const resp = await addRecipe(recipeToSubmit);
    if (resp.message) {
      alert("Pildi lisamine ebaõnnestus!");
    }
    navigate(`/recipe/${resp.recipeId}`);
  }

  async function removeTempImage() {
    await deleteTempImage(recipeImage);
    setRecipeImage(null);
  }

  async function removeExistingImage() {
    if(window.confirm("Kustuta jäädavalt eksisteeriv pilt?")) {
      await deleteSavedImage(recipeId);
      setExistingRecipeImage(null);
    }
  }

  useEffect(() => {
      getAutoFillData()
        .then((data) => setAutoFillData(data))
        .catch((error) => {
          console.error("Error:", error);
        });
    }, [setAutoFillData]);

    useEffect(() => {
      if (recipeId) {
        getRecipe(recipeId)
        .then((recipe) => {
          setRecipeName(recipe.name);
          setRecipeAmount(recipe.amount ?? '');
          setRecipePrepareTime(recipe.prepareTime ?? '');
          setRecipeSource(recipe.source ?? '');
          setRecipeCategories(recipe.categories);
          setExistingRecipeImage(recipe.pictureName);
          setRecipeContent(getRecipeContent(recipe.ingredientLists, recipe.instructions));
        })
        .catch((error) => {
          console.error("Error:", error);
        });
      }
    }, [recipeId]);

  return (
    <Container sx={{ mt: 2 }}>
      <Grid container direction={{ xs: "column", sm: "row"}} spacing={1}>
        <Grid size="grow">
          <Stack spacing={1}>
            <TextField id="heading" label="Pealkiri" value={recipeName} onChange={e => setRecipeName(e.target.value)}/>
            <Autocomplete
                    id="source"
                    freeSolo
                    options={recipeSources}
                    inputValue={recipeSource}
                    onInputChange={(e, newValue) => setRecipeSource(newValue)}
                    renderInput={(params) => <TextField {...params}  label="Allikas" />}
                  />
            <TextField id="amount" label="Kogus" value={recipeAmount} onChange={e => setRecipeAmount(e.target.value)} placeholder="Neljale"/>
            <TextField id="time" label="Valmistusaeg" value={recipePrepareTime} onChange={e => setRecipePrepareTime(e.target.value)} placeholder="15 min + 30 min ahjus"/>
            <TextField
              id="recipe"
              label="Retsept"
              multiline
              minRows={4}
              placeholder={sampleRecipe}
              value={recipeContent}
              onChange={e => setRecipeContent(e.target.value)}
            />
            <Autocomplete
              multiple
              id="tags-filled"
              options={availableCategories}
              freeSolo
              value={recipeCategories}
              onChange={(e, newValue) => setRecipeCategories(newValue)}
              renderTags={(value, getTagProps) =>
                value.map((option, index) => {
                  const { key, ...tagProps } = getTagProps({ index });
                  return (
                    <Chip variant="outlined" label={option} key={key} {...tagProps} />
                  );
                })
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Kategooriad"
                />
              )}
            />
            <Button variant="contained" onClick={prepareAddRecipe}>{recipeId ? "Muuda" : "Lisa"} retsept</Button>

          </Stack>
        </Grid>
        <Grid size="auto" align="center"> 
          {recipeImage 
            ?
              <Stack width="200px">
                <RecipeImage imgName={recipeImage} size="medium"/>
                <Button onClick={removeTempImage}>Eemalda</Button>
              </Stack>
            : (existingRecipeImage ?
              <Stack width="200px" spacing={1}>
                <RecipeImage imgName={existingRecipeImage} recipeId={recipeId} size="medium"/>
                <Button onClick={removeExistingImage} color="error" variant="contained">Kustuta</Button>
              </Stack>
              :
              <ImageUploadInput onImageChange={setRecipeImage}/>
            )           
          }
        </Grid>
      </Grid>
      <AddRecipeModal open={submitModalOpen} onClose={() => setSubmitModalOpen(false)} ingredientLists={parsedIngredientLists} onUpdateIngredientLists={(lists) => setParsedIngredientLists(lists)} autoFillData={autoFillData} onSubmit={submitRecipe} />
    </Container>
  );
}

function parseRecipeContent(content, autoFillData) {
  const [ingredientsString, ...recipeParts] = content.split("\n\n");
  if (!recipeParts.length) {
    throw Error("koostisosade ja retsepti vahel peab olema tühi rida!");
  }
  const recipe = recipeParts.join("\n\n");
  const ingredientLists = [];
  let currentList = {name: "Koostis", ingredientLines: []};
  for (const ingredientLine of ingredientsString.split("\n").map(line => line.trim())) {
    if (ingredientLine.endsWith(":")) {
      if (currentList.ingredientLines.length) {
        ingredientLists.push(currentList);
      }
      currentList = {name: ingredientLine.slice(0, ingredientLine.length - 1), ingredientLines: []}
    } else {
      const [ingredient, ...altIngredients] = ingredientLine.split(" või ");
      const parsedIngredient = parseIngredientLine(ingredient, autoFillData);
      currentList.ingredientLines.push({
        ...parsedIngredient,
        alternateLines: altIngredients.map(altIngredient => parseIngredientLine(altIngredient, autoFillData))
      });
    }
  }
  if (currentList.ingredientLines.length) {
    ingredientLists.push(currentList);
  }
  return {
    instructions: recipe,
    ingredientLists
  };
}

function parseIngredientLine(ingredientLine, autoFillData) {
  ingredientLine = ingredientLine.trim();
  let existingSearchIngredient = undefined;
  if (ingredientLine.endsWith("]")) {
    const [line, searchIngredient] = ingredientLine.split("[");
    ingredientLine = line;
    existingSearchIngredient = searchIngredient.substring(0, searchIngredient.length -1);

  }
  const lineParts = ingredientLine.split(" ").filter(x => x.trim().length > 0);
  
  // no amount
  if (!amountValidator.test(lineParts[0])) {
    return {
      ingredient: ingredientLine,
      searchIngredient: existingSearchIngredient ?? (autoFillData.ingredients?.[ingredientLine] ?? undefined)
    };
  }

  const amount = lineParts[0];

  // no unit
  if (lineParts.length === 2) {
    lineParts.shift();
    const ingredient = lineParts.join(" ");
    return {
      amount: amount,
      ingredient: ingredient,
      searchIngredient: existingSearchIngredient ?? (autoFillData.ingredients?.[ingredient] ?? undefined)
    };
  }

  const possibleUnit = lineParts[1];
  if (autoFillData.units?.includes(possibleUnit)) {
    const ingredient = lineParts.slice(2).join(" ");
    return {
      amount: amount,
      unit: possibleUnit,
      ingredient: ingredient,
      searchIngredient: existingSearchIngredient ?? (autoFillData.ingredients?.[ingredient] ?? undefined)
    };
  }

  // no recognized unit
  lineParts.shift();
  const ingredient = lineParts.join(" ");
  return {
    amount: amount,
    ingredient: ingredient,
    searchIngredient: existingSearchIngredient ?? (autoFillData.ingredients?.[ingredient] ?? undefined)
  };
}

function getRecipeContent(ingredientLists, instructions) {
  const ingredientsPart = ingredientLists.map((list) => {
    return list.name + ":\n" + list.ingredientLines.map((line) => {
      const lines = [line, ...line.alternateLines];
      return lines.map(ingredient => getContentIngredient(ingredient)).join(' või ')
    }).join('\n');
  }).join('\n');
  return ingredientsPart + '\n\n' + instructions;
}

function getContentIngredient(ingredient) {
  return `${ingredient.amount ? ingredient.amount + " " : "" }${ingredient.unit ? ingredient.unit + " " : "" }${ingredient.ingredient} [${ingredient.searchIngredient}]`

}