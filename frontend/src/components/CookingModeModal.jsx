import { useState} from "react";
import { useTheme, createTheme, ThemeProvider } from '@mui/material/styles';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import Typography from "@mui/material/Typography";
import IngredientListSection from "./IngredientListSection";
import { DialogContent, TextField } from "@mui/material";
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import TextDecreaseIcon from '@mui/icons-material/TextDecrease';
import TextIncreaseIcon from '@mui/icons-material/TextIncrease';

export default function CookingModeModal({ recipe, open, onClose }) {
  const theme = useTheme();
  const [sizeModifier, setSizeModifier] = useState(1.5);
  const [amountMultiplier, setAmountMultiplier] = useState(1);

  const sizedTheme = createTheme({
    ...theme,
    typography: {
      fontSize: theme.typography.fontSize * sizeModifier,
    },
  });

  console.log(sizedTheme.typography);

  return (
    <ThemeProvider theme={sizedTheme}>
      <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: 1 }}>
          {recipe.name}
          <span>
            <IconButton aria-label="decrease font" onClick={() => setSizeModifier(Math.max(sizeModifier - 0.1, 0.8))}><TextDecreaseIcon /></IconButton>
            <IconButton aria-label="decrease font" onClick={() => setSizeModifier(Math.min(sizeModifier + 0.1, 10))}><TextIncreaseIcon /></IconButton>
          </span>
          <IconButton aria-label="close" onClick={onClose} edge="end">
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent >
          <TextField sx={{ marginTop: 2 }} size="small" type="number" label="Koguse kordaja" value={amountMultiplier} onChange={e => setAmountMultiplier(parseFloat(e.target.value))} />
          <IngredientListSection ingredientLists={recipe.ingredientLists} amountMultiplier={amountMultiplier} />
          <Typography variant="body1" align="left" paddingTop="1em" whiteSpace="pre-line">{recipe.instructions}</Typography>
        </DialogContent>
      </Dialog>
    </ThemeProvider>
  )
}