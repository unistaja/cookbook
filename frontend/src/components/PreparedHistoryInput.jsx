import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import 'dayjs/locale/et';
import { useState } from 'react';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import CheckIcon from '@mui/icons-material/Check';
import { saveDate } from '../api';

export default function PreparedHistoryInput({recipeId, onChange}) {
  const [dateValue, setDateValue] = useState(dayjs());
  const [dateSaved, setDateSaved] = useState(false);

  async function savePreparation() {
    await saveDate(dateValue.format('YYYY-MM-DD'), 0, recipeId);
    setDateSaved(true);
    setTimeout(() => setDateSaved(false), 2000);
    onChange(dateValue);
  }

  return (
    <>
    <Stack direction="row" spacing={0.5} >
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale='et'>
        <DatePicker value={dateValue} onChange={(newValue) => setDateValue(newValue)} slotProps={{ textField: { size: 'small'}, openPickerIcon: { fontSize: 'small' } }} maxDate={dayjs()}/>
      </LocalizationProvider>
      <Button variant="outlined" size="small" sx={{height: "40px"}} onClick={savePreparation}>Valmistasin</Button>
    </Stack>
    {dateSaved && (
      <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
        Valmistuskord salvestatud
      </Alert>
    )}
    </>
  );
}