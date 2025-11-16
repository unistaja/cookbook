import { useState, useEffect } from "react";
import { getAutoFillData } from "../api";
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import { TextField } from "@mui/material";
import { Stack } from "@mui/material";
import Autocomplete from '@mui/material/Autocomplete';
import Chip from '@mui/material/Chip';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';

const defaultConfiguration = {
  name: "",
  config: {
    resultPage: 0,
    resultsPerPage: 7,
    sortOrder: 4,
    withIngredients: [],
    withoutIngredients: [],  
    categories: []
  }
};
  

export default function EditRecipeSuggestionConfigurationModal({open, onClose, onSubmit, configuration}) {
  const [config, setConfig] = useState(JSON.parse(JSON.stringify(configuration ? configuration.config : defaultConfiguration)));
  const [autoFillData, setAutoFillData] = useState({});

  function updateName(newName) {
    setConfig({
      ...config,
      name: newName
    });
  }

  function updateField(fieldName, newValue) {
    setConfig({
      ...config,
      config: {
        ...config.config,
        [fieldName]: newValue
      }
    });
  }

  useEffect(() => {
    if (open && !autoFillData.categories) {
      getAutoFillData()
        .then((data) => setAutoFillData(data))
        .catch((error) => {
          console.error("Error:", error);
        });
    }
  }, [autoFillData, open, setAutoFillData]);

  return (
    <Dialog onClose={onClose} open={open} fullWidth={true} maxWidth="md">
      <DialogTitle>{config.id ? "Muuda retseptisoovitajat" : "Lisa retseptisoovitaja"}</DialogTitle>
      <Stack spacing={2} sx={{padding: "1em"}}>
        <TextField label="Pealkiri" value={config.name ?? ''} onChange={e => updateName(e.target.value)} size='small' fullWidth/>
        <TextField label="Soovituste arv" type={"number"} value={config.config.resultsPerPage} onChange={e => updateField('resultsPerPage', parseInt(e.target.value))} size='small' fullWidth/>
        <Autocomplete
          multiple
          id="tags-filled"
          size='small'
          limitTags={2}
          options={autoFillData.categories ?? []}
          value={config.config.categories}
          onChange={(e, newValue) => updateField('categories', newValue)}
          renderTags={(value, getTagProps) =>
            value.map((option, index) => {
              const { key, ...tagProps } = getTagProps({ index });
              return (
                <Chip variant="outlined" label={option} key={key} {...tagProps} size="small" />
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
        <FormControl size="small" fullWidth>
          <InputLabel id="prepared-label">Valmistanud</InputLabel>
          <Select
            labelId="prepared-label"
            id="prepared"
            value={config.config.hasPrepared ?? 0}
            label="Valmistanud"
            onChange={e => updateField('hasPrepared', e.target.value === 0 ? null : e.target.value)}
          >
            <MenuItem value={0}>Kõik</MenuItem>
            <MenuItem value={true}>Jah</MenuItem>
            <MenuItem value={false}>Ei</MenuItem>
          </Select>
        </FormControl>
        <Button onClick={(e) => {e.preventDefault(); onSubmit(config)}} type="submit" variant='contained' fullWidth>Lisa</Button>
      </Stack>
    </Dialog>
  );
}