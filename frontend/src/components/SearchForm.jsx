import Autocomplete from '@mui/material/Autocomplete';
import Button from "@mui/material/Button";
import Chip from '@mui/material/Chip';
import Checkbox from '@mui/material/Checkbox';
import Collapse from '@mui/material/Collapse';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid2';
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import Select from '@mui/material/Select';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import { useState } from "react";

export default function SearchForm({onSearch, currentSearch, autoFillData}) {
  const [internalSearch, setInternalSearch] = useState(currentSearch);
  const [expanded, setExpanded] = useState(false);
  const [isIngredientAndMode, setIsIngredientAndMode] = useState(true);

  function updateField(fieldName, newValue) {
    setInternalSearch({
      ...internalSearch,
      [fieldName]: newValue
    });
  }

  function handleModeToggle() {
    const newMode = !isIngredientAndMode;
    setIsIngredientAndMode(newMode);
    if (!internalSearch.withIngredients.length) {
      return;
    }
    if (newMode) {
      // or to and
      const newIngredientFilter = [{
        ingredient: internalSearch.withIngredients[0].ingredient,
        alternateLines: []
      }, ...internalSearch.withIngredients[0].alternateLines.map(line => ({ingredient: line.ingredient, alternateLines: []}))];
      updateField('withIngredients', newIngredientFilter);
    } else {
      // and to or
      const newIngredientFilter = [{
        ingredient: internalSearch.withIngredients[0].ingredient,
        alternateLines: internalSearch.withIngredients.slice(1).map(line => ({ingredient: line.ingredient}))
      }];
      updateField('withIngredients', newIngredientFilter);
    }

  }

  function handleWithIngredientUpdate(newIngredientList) {
    if (!newIngredientList.length) {
      updateField('withIngredients', []);
    } else if (isIngredientAndMode) {
      updateField('withIngredients', newIngredientList.map(ingredient => ({ingredient, alternateLines: []})))
    } else {
      const newIngredientFilter = [{
        ingredient: newIngredientList[0],
        alternateLines: newIngredientList.slice(1).map(ingredient => ({ingredient}))
      }];
      updateField('withIngredients', newIngredientFilter);
    }
  }

  function getWithIngredientList() {
    if (!internalSearch.withIngredients.length) {
      return [];
    }
    if (isIngredientAndMode) {
      return internalSearch.withIngredients.map(line => line.ingredient);
    }
    return [internalSearch.withIngredients[0].ingredient, ...internalSearch.withIngredients[0].alternateLines.map(line => line.ingredient )];
  }

  


  return (
    <Paper sx={{margin: 2, padding: 2}} elevation={3}>
      <form>
        <Grid container spacing={2} columns={{xs: 6, sm: 12}}>
          <Grid size={6}>
            <TextField label="Pealkiri" value={internalSearch.name ?? ''} onChange={e => updateField('name', e.target.value)} size='small' fullWidth/>
          </Grid>
          <Grid size={5}>
            <Autocomplete
              multiple
              id="tags-filled"
              size='small'
              limitTags={2}
              options={autoFillData.categories ?? []}
              value={internalSearch.categories}
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
          </Grid>
          <Grid size={1}>
            <IconButton
              onClick={() => setExpanded(!expanded)}
              aria-expanded={expanded}
              aria-label="show more"
              style={{transform: `rotate(${expanded ? 180 : 0}deg)`}}
            >
              <ExpandMoreIcon />
            </IconButton>
          </Grid>
          <Grid size={expanded ? 12 : 0}>
            <Collapse in={expanded} timeout="auto">
              <Grid container spacing={2} columns={{xs: 6, sm: 12}}>
                <Grid size={12}><Divider/></Grid>
                <Grid size={6}>
                  <Autocomplete
                    multiple
                    id="ingredients-filled"
                    limitTags={2}
                    size='small'
                    options={autoFillData.searchIngredients ?? []}
                    value={getWithIngredientList()}
                    onChange={(e, newValue) => handleWithIngredientUpdate(newValue)}
                    renderTags={(value, getTagProps) =>
                      value.map((option, index) => {
                        const { key, ...tagProps } = getTagProps({ index });
                        return (
                          <Chip variant="outlined" label={option} key={key} {...tagProps} size="small" />
                        );
                      })
                    }
                    renderInput={(params) => 
                      <TextField
                        {...params}
                        label="Sisaldab"
                        InputProps={{
                          ...params.InputProps,
                          startAdornment: [(
                            <InputAdornment position="start" style={{marginRight: "0px"}} key="ingredientmodetoggle">
                              <Button
                                onClick={handleModeToggle}
                                size="small" 
                                variant="outlined"
                                sx={{padding: "0px", minWidth: "36px"}}
                              >
                                {isIngredientAndMode ? "ja" : "või"}
                              </Button>
                            </InputAdornment>
                          ), ...(params.InputProps.startAdornment ?? [])],

                        }}
                      />
                    }
                  />
                </Grid>
                <Grid size={6}>
                  <Autocomplete
                    multiple
                    id="no-ingredients-filled"
                    size='small'
                    limitTags={2}
                    options={autoFillData.searchIngredients ?? []}
                    value={internalSearch.withoutIngredients}
                    onChange={(e, newValue) => updateField('withoutIngredients', newValue)}
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
                        label="Ei sisalda"
                      />
                    )}
                  />
                </Grid>
                <Grid size={6}>
                  <Autocomplete
                    id="source"
                    options={autoFillData.sources ?? []}
                    inputValue={internalSearch.source ?? ''}
                    onInputChange={(e, newValue) => updateField('source', newValue)}
                    size="small"
                    renderInput={(params) => <TextField {...params}  label="Allikas" />}
                  />
                </Grid>
                <Grid size={{xs: 6, sm: 6, md: 2}}>
                  <Autocomplete
                    id="adder"
                    options={autoFillData.users ?? []}
                    inputValue={internalSearch.username ?? ''}
                    onInputChange={(e, newValue) => updateField('username', newValue)}
                    size="small"
                    renderInput={(params) => <TextField {...params}  label="Lisaja" />}
                  />
                </Grid>
                <Grid size={{xs: 4,sm: 6, md: 2}}>
                  <FormControl size="small" fullWidth>
                    <InputLabel id="prepared-label">Valmistanud</InputLabel>
                    <Select
                      labelId="prepared-label"
                      id="prepared"
                      value={internalSearch.hasPrepared ?? 0}
                      label="Valmistanud"
                      onChange={e => updateField('hasPrepared', e.target.value === 0 ? null : e.target.value)}
                    >
                      <MenuItem value={0}>Kõik</MenuItem>
                      <MenuItem value={true}>Jah</MenuItem>
                      <MenuItem value={false}>Ei</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid size={2}>
                <FormControlLabel
                  control={<Checkbox checked={internalSearch.hasPicture} onChange={e => updateField('hasPicture', e.target.checked)}/>}
                  label="Pildiga"
                />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>


          <Grid size={12}>
            <Divider/>
          </Grid>
          <Grid size={{xs: 4, sm:6}}>
            <FormControl size="small" fullWidth>
              <InputLabel id="sort-label">Järjesta</InputLabel>
              <Select
                labelId="sort-label"
                id="sort"
                value={internalSearch.sortOrder}
                label="Järjesta"
                onChange={e => updateField('sortOrder', e.target.value)}
                style={{paddingLeft: "0px"}}
                startAdornment={
                  <InputAdornment position="start" style={{marginRight: "0px"}}>
                    <IconButton
                      onClick={() => updateField('descending', !internalSearch.descending)}
                      style={{transform: `rotate(${internalSearch.descending ? 180 : 0}deg)`}}
                    >
                      <ArrowUpwardIcon />
                    </IconButton>
                  </InputAdornment>
                }
              >
                <MenuItem value={0}>Pealkirja järgi</MenuItem>
                <MenuItem value={1}>Lisaja järgi</MenuItem>
                <MenuItem value={2}>Lisamisaja järgi</MenuItem>
                <MenuItem value={3}>Valmistusaja järgi</MenuItem>
                <MenuItem value={4}>Juhuslikult</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid size={{xs: 2, sm:4}}>
            <FormControl size="small" fullWidth>
              <InputLabel id="rows-per-page-label">Tulemusi lehel</InputLabel>
              <Select
                labelId="rows-per-page-label"
                id="rows-per-page"
                value={internalSearch.resultsPerPage}
                label="Tulemusi lehel"
                onChange={e => updateField('resultsPerPage', e.target.value)}
                
              >
                <MenuItem value={10}>10</MenuItem>
                <MenuItem value={50}>20</MenuItem>
                <MenuItem value={50}>50</MenuItem>
                <MenuItem value={0}>Kõik</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid size={{xs: 6, sm:2}}>
            <Button onClick={(e) => {e.preventDefault(); onSearch(internalSearch)}} type="submit" variant='contained' color='secondary' fullWidth>Otsi</Button>
          </Grid>
        </Grid>
      </form>

    </Paper>
  );
}