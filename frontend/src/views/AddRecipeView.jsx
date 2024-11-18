import * as React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Chip from '@mui/material/Chip';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import ImageUploadInput from '../components/ImageUploadInput';
import AddRecipeModal from '../components/AddRecipeModal';
import { getAutoFillData, addRecipe, deleteTempImage } from "../api";
import RecipeImage from '../components/RecipeImage';

export default function AddRecipeView() {
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

  useEffect(() => {
      getAutoFillData()
        .then((data) => setAutoFillData(data))
        .catch((error) => {
          console.error("Error:", error);
        });
    }, [setAutoFillData]);

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
            <Button variant="contained" onClick={prepareAddRecipe}>Lisa retsept</Button>

          </Stack>
        </Grid>
        <Grid size="auto" align="center"> 
          {recipeImage 
            ?
              <Stack width="350px">
                <RecipeImage imgName={recipeImage} size="medium"/>
                <Button onClick={removeTempImage}>Eemalda</Button>
              </Stack>
            :
              <ImageUploadInput onImageChange={setRecipeImage}/>
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
  const lineParts = ingredientLine.split(" ").filter(x => x.trim().length > 0);
  const amount = Number(lineParts[0]);

  // no amount
  if (!amount) {
    return {
      ingredient: ingredientLine,
      searchIngredient: autoFillData.ingredients?.[ingredientLine] ?? undefined
    };
  }

  // no unit
  if (lineParts.length === 2) {
    lineParts.shift();
    const ingredient = lineParts.join(" ");
    return {
      amount: amount,
      ingredient: ingredient,
      searchIngredient: autoFillData.ingredients?.[ingredient] ?? undefined
    };
  }

  const possibleUnit = lineParts[1];
  if (autoFillData.units?.includes(possibleUnit)) {
    const ingredient = lineParts.slice(2).join(" ");
    return {
      amount: amount,
      unit: possibleUnit,
      ingredient: ingredient,
      searchIngredient: autoFillData.ingredients?.[ingredient] ?? undefined
    };
  }

  // no recognized unit
  lineParts.shift();
  const ingredient = lineParts.join(" ");
  return {
    amount: amount,
    ingredient: ingredient,
    searchIngredient: autoFillData.ingredients?.[ingredient] ?? undefined
  };
}