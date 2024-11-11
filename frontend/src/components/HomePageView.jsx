import ViewedRecipesWidget from './ViewedRecipesWidget';
import Stack from '@mui/material/Stack';

export default function Home() {
  return (
    <Stack spacing={2} sx={{ mt: 2 }}>
      <ViewedRecipesWidget/>
    </Stack>
  );
}