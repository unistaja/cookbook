import { Link } from 'react-router-dom';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import Typography from '@mui/material/Typography';
import Rating from '@mui/material/Rating';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import RecipeImage from './RecipeImage';
import dayjs from 'dayjs';

export function RecipeListRow({recipeData}) {
  const theme = useTheme();
  const isTiny = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <TableRow hover component={Link} to={`/recipe/${recipeData.id}`} style={{textDecoration: "none"}} key={recipeData.id}>
      <TableCell component='div' sx={{minWidth: isTiny ? "102" : "162px"}}>
        <RecipeImage recipeId={recipeData.id} imgName={recipeData.pictureName} sx={{height: isTiny? "70px" : "100px", maxWidth: isTiny ? "91" : "130px"}}/>
      </TableCell>
      <TableCell component='div'>
        <Typography variant={isTiny ? "subtitle2" : "h5"}>{recipeData.name}</Typography>
        {isTiny && <Rating value={recipeData.averageRating} readOnly size="small"/>}
        <Typography variant="body2">Lisatud: {dayjs(recipeData.added).format('DD.MM.YYYY')} ({recipeData.user.username})</Typography>
        {recipeData.preparedHistory && <Typography variant="body2">{isTiny ? "V" : "Viimati v"}almistasin: {dayjs(recipeData.preparedHistory[0].preparedTime).format('DD.MM.YYYY')}</Typography>}
      </TableCell>
      {!isTiny && <TableCell component='div' align='right'>
        <Rating value={recipeData.averageRating} readOnly/>
      </TableCell>}
    </TableRow>
  )

}