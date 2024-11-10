import * as React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Chip from '@mui/material/Chip';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import ImageUploadInput from './ImageUploadInput';
import AddRecipeModal from './AddRecipeModal';
import { getAutoFillData, addRecipe } from "../api";

export default function AddRecipeView() {
  const sampleRecipe = `1 kg kartuleid
vett
soola

Koori kartulid ja keeda soolaga maitsestatud vees pehmeks.
  `;
  const [autoFillData, setAutoFillData] = useState({});
  const [recipeName, setRecipeName] = useState('');
  const [recipeSource, setRecipeSource] = useState('');
  const [recipeContent, setRecipeContent] = useState('');
  const [recipeCategories, setRecipeCategories] = useState([]);
  const [parsedIngredientLists, setParsedIngredientLists] = useState([]);
  const [parsedInstructions, setParsedInstructions] = useState('');
  const [submitModalOpen, setSubmitModalOpen] = useState(false);
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
    console.log(`State: name: ${recipeName}, source: ${recipeSource}, recipe: ${JSON.stringify(parsedRecipe)}, categories: ${recipeCategories}`);
  }

  async function submitRecipe() {
    const recipeToSubmit = {
      name: recipeName,
      source: recipeSource ?? null,
      instructions: parsedInstructions,
      ingredientLists: parsedIngredientLists,
      categories: recipeCategories,
    };
    const resp = await addRecipe(recipeToSubmit);
    if (resp.message) {
      alert("Pildi lisamine ebaõnnestus!");
    }
    window.location.href=`/index-vue.html#/recipe/${resp.recipeId}`;
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
{/*         <Grid size="auto" align="center"> */}
{/*           <ImageUploadInput/> */}
{/*         </Grid> */}
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