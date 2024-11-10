import { useCallback } from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Paper from '@mui/material/Paper';

export default function AddRecipeModal({ingredientLists, onUpdateIngredientLists, autoFillData, open, onClose, onSubmit}) {
  const updateIngredientField = useCallback((newValue, fieldName, listIdx, ingredientIdx, altIngredientIdx) => {
    const newLists = JSON.parse(JSON.stringify(ingredientLists)); // make deep copy
    console.log(newLists);
    let lineToEdit = newLists[listIdx].ingredientLines[ingredientIdx];
    if (altIngredientIdx !== undefined) {
      lineToEdit = lineToEdit.alternateLines[altIngredientIdx];
    }
    lineToEdit[fieldName] = newValue ?? undefined;
    onUpdateIngredientLists(newLists);
  }, [ingredientLists, onUpdateIngredientLists]);

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
           </TableRow>
         </TableHead>
         {ingredientLists.map((list, listIdx) => (
           <TableBody>
             {ingredientLists.length > 1 && <TableRow><TableCell colSpan="4">{list.name}</TableCell></TableRow>}
             {list.ingredientLines.map((ingredient, ingredientIdx) => (
               <>
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
                   <TextField size="small" value={ingredient.ingredient ?? ''} onChange={(e, newValue) => updateIngredientField(newValue, 'ingredient', listIdx, ingredientIdx)}/>
                 </TableCell>
                 <TableCell>
                   <TextField size="small" value={ingredient.searchIngredient ?? ''} onChange={(e, newValue) => updateIngredientField(newValue, 'searchIngredient', listIdx, ingredientIdx)}/>
                 </TableCell>
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
                     <TextField size="small" value={altIngredient.ingredient ?? ''} onChange={(e, newValue) => updateIngredientField(newValue, 'ingredient', listIdx, ingredientIdx, altIdx)}/>
                   </TableCell>
                   <TableCell>
                     <TextField size="small" value={altIngredient.searchIngredient ?? ''} onChange={(e, newValue) => updateIngredientField(newValue, 'searchIngredient', listIdx, ingredientIdx, altIdx)}/>
                   </TableCell>
                 </TableRow>
               ))}
               </>
             ))}
           </TableBody>
         ))}
       </Table>
      </TableContainer>
      <Button variant="contained" onClick={onSubmit}>Salvesta</Button>
    </Dialog>
  );
}