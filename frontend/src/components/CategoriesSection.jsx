import Chip from "@mui/material/Chip"
import Autocomplete from "@mui/material/Autocomplete"
import Button from "@mui/material/Button"
import TextField from "@mui/material/TextField"
import { useState } from "react";
import { addCategories, getCategoriesAutoFillData } from "../api";

export function CategoriesSection({categories, recipeId, onCategoryUpdate}) {
  const [showCategoryAdditionForm, setShowCategoryAdditionForm] = useState(false);
  const [addedCategories, setAddedCategories] = useState([]);
  const [availableCategories, setAvailableCategories] = useState([]);

  function handleAddCategoryClick() {
    if (!availableCategories.length) {
      getCategoriesAutoFillData().then(data => setAvailableCategories(data.filter(category => !categories.includes(category)))).catch();
    }
    setShowCategoryAdditionForm(true);
  }

  async function handleSubmitClick() {
    if (addCategories.length > 0) {
      await addCategories(addedCategories, recipeId);
      setAddedCategories([]);
      setShowCategoryAdditionForm(false);
      onCategoryUpdate(addedCategories);
    }
  }

  function handleCancelClick() {
    setShowCategoryAdditionForm(false);
    setAddedCategories([]);
  }
  return (
    <>
      {categories.map((category) => <Chip label={category} key={category} size="small" sx={{margin: "2px"}}/>)}
      <Chip label="+" key="addcategory" size="small" sx={{margin: "2px"}} onClick={handleAddCategoryClick}/>
      {showCategoryAdditionForm && 
        <>
        <Autocomplete
          multiple
          id="tags-filled"
          options={availableCategories}
          freeSolo
          value={addedCategories}
          onChange={(e, newValue) => setAddedCategories(newValue)}
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
              autoFocus
              size="small"
              sx={{marginTop: "8px"}}
            />
          )}
        />
        <Button size="small" variant="contained" onClick={handleSubmitClick} sx={{margin: "4px"}}>Salvesta</Button>
        <Button size="small" variant="contained" color="error" onClick={handleCancelClick} sx={{margin: "4px"}}>Katkesta</Button>
        </>
      }
    </>
  );
}