import Button from "@mui/material/Button";
import Stack from "@mui/material/Stack";
import RecipeImage from "./RecipeImage";
import { useState } from "react";
import ImageUploadInput from "./ImageUploadInput";
import { deleteTempImage, saveImage } from "../api";
import Dialog from "@mui/material/Dialog";

export default function RecipeImageSection({recipeId, imageName, onUpdateImage}) {
  const [tempImageName, setTempImageName] = useState(null);
  const [imgDialogOpen, setImgDialogOpen] = useState(false);

  async function handleSave() {
    const extension = tempImageName.slice(tempImageName.lastIndexOf(".") + 1);
    await saveImage(recipeId, extension);
    onUpdateImage(extension);
    setTempImageName(null);
  }

  async function handleCancel() {
    await deleteTempImage(tempImageName);
    setTempImageName(null);
  }

  if (tempImageName) {
    return (
      <Stack alignItems="center" spacing={1}>
      <RecipeImage imgName={tempImageName} size="medium" sx={{width:"200px"}}/>
        <Stack direction="row" spacing={1}>
          <Button variant="contained" size="small" onClick={handleSave}>Salvesta</Button>
          <Button variant="contained" size="small" color="error" onClick={handleCancel}>Katkesta</Button>
        </Stack>
      </Stack>
    );
  }
  return imageName ? (
    <>
      <RecipeImage recipeId={recipeId} imgName={imageName} size="medium" sx={{width:"200px"}} onClick={() => setImgDialogOpen(true)}/>
      <Dialog open={imgDialogOpen} onClose={() => setImgDialogOpen(false)}>
        <RecipeImage recipeId={recipeId} imgName={imageName} size="large"/>
      </Dialog>
    </>
  ) : <ImageUploadInput onImageChange={(imgName) => setTempImageName(imgName)} recipeId={recipeId}/>;

}