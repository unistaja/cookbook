import Chip from "@mui/material/Chip"

export function CategoriesSection({categories}) {
  return (
    <>
      {categories.map((category) => <Chip label={category} key={category} size="small" sx={{margin: "2px"}}/>)}
    </>
  );
}