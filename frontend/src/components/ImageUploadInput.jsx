import * as React from 'react';
import Button from '@mui/material/Button';
import AddAPhotoIcon from '@mui/icons-material/AddAPhoto';

export default function ImageUploadInput({recipeId}) {
  return (
    <Button
      component="label"
      variant="outlined"
      style={{width: '300px', height: '300px'}}
    >
      <AddAPhotoIcon/>
      <input
        type="file"
        style={{ display: 'none' }}
        onChange={(event) => console.log(event.target.files)}
        multiple
      />
    </Button>
  );
}