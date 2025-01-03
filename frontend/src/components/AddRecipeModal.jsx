import { Fragment, useCallback } from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Paper from '@mui/material/Paper';
import ClearIcon from '@mui/icons-material/Clear';

export default function AddRecipeModal({ingredientLists, onUpdateIngredientLists, autoFillData, open, onClose, onSubmit}) {
  const updateIngredientField = useCallback((newValue, fieldName, listIdx, ingredientIdx, altIngredientIdx) => {
    const newLists = JSON.parse(JSON.stringify(ingredientLists)); // make deep copy
    let lineToEdit = newLists[listIdx].ingredientLines[ingredientIdx];
    if (altIngredientIdx !== undefined) {
      lineToEdit = lineToEdit.alternateLines[altIngredientIdx];
    }
    lineToEdit[fieldName] = newValue ?? undefined;
    onUpdateIngredientLists(newLists);
  }, [ingredientLists, onUpdateIngredientLists]);

  const deleteIngredient = useCallback((listIdx, ingredientIdx, altIngredientIdx) => {
    const newLists = JSON.parse(JSON.stringify(ingredientLists)); // make deep copy
    if (altIngredientIdx !== undefined) {
      newLists[listIdx].ingredientLines[ingredientIdx].alternateLines.splice(altIngredientIdx, 1);
    } else {
      newLists[listIdx].ingredientLines.splice(ingredientIdx, 1);
    }
    onUpdateIngredientLists(newLists);
    
  }, [ingredientLists, onUpdateIngredientLists])

  return (
    <Dialog onClose={onClose} open={open} fullWidth={true} maxWidth="md">
      <DialogTitle>Kontrolli ja vajadusel muuda ühikuid ja otsingukoostisosi</DialogTitle>
      <TableContainer component={Paper}>
       <Table>
         <TableHead>
           <TableRow>
             <TableCell>Kogus</TableCell>
             <TableCell>Ühik</TableCell>
             <TableCell>Koostisosa</TableCell>
             <TableCell>Otsingukoostisosa</TableCell>
             <TableCell></TableCell>
           </TableRow>
         </TableHead>
         {ingredientLists.map((list, listIdx) => (
           <TableBody key={`list-${listIdx}`}>
             {ingredientLists.length > 1 && <TableRow key={`list-heading-${listIdx}`}><TableCell colSpan="5">{list.name}</TableCell></TableRow>}
             {list.ingredientLines.map((ingredient, ingredientIdx) => (
               <Fragment key={`list-${listIdx}-ingredientline-${ingredientIdx}`}>
               <TableRow>
                 <TableCell align="right">{ingredient.amount}</TableCell>
                 <TableCell>
                   <Autocomplete
                     freeSolo
                     size="small"
                     options={autoFillData.units}
                     inputValue={ingredient.unit ?? ''}
                     onInputChange={(e, newValue) => updateIngredientField(newValue, 'unit', listIdx, ingredientIdx)}
                     renderInput={(params) => <TextField {...params}/>}
                   />
                 </TableCell>
                 <TableCell>
                   <TextField size="small" value={ingredient.ingredient ?? ''} onChange={(e) => updateIngredientField(e.target.value, 'ingredient', listIdx, ingredientIdx)}/>
                 </TableCell>
                 <TableCell>
                   <Autocomplete
                     freeSolo
                     size="small"
                     options={autoFillData.searchIngredients}
                     inputValue={ingredient.searchIngredient ?? ''}
                     onInputChange={(e, newValue) => updateIngredientField(newValue, 'searchIngredient', listIdx, ingredientIdx)}
                     renderInput={(params) => <TextField {...params}/>}
                   />
                 </TableCell>
                 <TableCell><IconButton onClick={() => deleteIngredient(listIdx, ingredientIdx)}><ClearIcon/></IconButton></TableCell>
               </TableRow>
               {ingredient.alternateLines.map((altIngredient, altIdx) => (
                 <TableRow>
                   <TableCell>{altIngredient.amount}</TableCell>
                   <TableCell>
                     <Autocomplete
                       freeSolo
                       size="small"
                       options={autoFillData.units}
                       inputValue={altIngredient.unit ?? ''}
                       onInputChange={(e, newValue) => updateIngredientField(newValue, 'unit', listIdx, ingredientIdx, altIdx)}
                       renderInput={(params) => <TextField {...params}/>}
                     />
                   </TableCell>
                   <TableCell>
                     <TextField size="small" value={altIngredient.ingredient ?? ''} onChange={(e) => updateIngredientField(e.target.value, 'ingredient', listIdx, ingredientIdx, altIdx)}/>
                   </TableCell>
                   <TableCell>
                     <Autocomplete
                       freeSolo
                       size="small"
                       options={autoFillData.searchIngredients}
                       inputValue={altIngredient.searchIngredient ?? ''}
                       onInputChange={(e, newValue) => updateIngredientField(newValue, 'searchIngredient', listIdx, ingredientIdx, altIdx)}
                       renderInput={(params) => <TextField {...params}/>}
                     />
                   </TableCell>
                   <TableCell><IconButton onClick={() => deleteIngredient(listIdx, ingredientIdx, altIdx)}><ClearIcon/></IconButton></TableCell>
                 </TableRow>
               ))}
               </Fragment>
             ))}
           </TableBody>
         ))}
       </Table>
      </TableContainer>
      <Button variant="contained" onClick={onSubmit}>Salvesta</Button>
    </Dialog>
  );
}