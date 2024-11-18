import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TablePagination from "@mui/material/TablePagination";
import { RecipeListRow } from "./RecipeListRow";
import Divider from "@mui/material/Divider";
export default function RecipeList({data, currentPage, rowsPerPage, onPageChange}) {
  return (

    <Paper sx={{margin: 2, padding: 2}} elevation={3}>
      <TablePagination
        rowsPerPageOptions={[rowsPerPage]}
        component="div"
        count={data.total}
        rowsPerPage={rowsPerPage}
        page={currentPage}
        onPageChange={onPageChange}
        sx={{alignContent: "stretch"}}
      />
      <Divider/>
      <Table component='div'>
        <TableBody component='div'>
        {data.recipes.map(recipe => <RecipeListRow recipeData={recipe} key={recipe.id}/>)}
        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[rowsPerPage]}
        component="div"
        count={data.total}
        rowsPerPage={rowsPerPage}
        page={currentPage}
        onPageChange={onPageChange}
      />

    </Paper>
  );
}