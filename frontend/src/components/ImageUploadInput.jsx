import * as React from 'react';
import Button from '@mui/material/Button';
import AddAPhotoIcon from '@mui/icons-material/AddAPhoto';
import { uploadImage } from '../api';

export default function ImageUploadInput({recipeId, onImageChange}) {
  async function handleImageSelect(file) {
    if (!file) return;
    const uploadedImgName = await uploadImage(file);
    onImageChange(uploadedImgName);
  }

  return (
    <Button
      component="label"
      variant="outlined"
      style={{width: '350px', height: '300px'}}
    >
      <AddAPhotoIcon/>
      <input
        type="file"
        style={{ display: 'none' }}
        onChange={(event) => handleImageSelect(event.target.files?.[0])}
      />
    </Button>
  );
}