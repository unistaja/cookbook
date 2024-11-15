import { useState, useEffect } from "react";
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import Stack from "@mui/material/Stack";
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import { deletePreparedDate, findPreparedTimes } from "../api";
import Typography from "@mui/material/Typography";
import dayjs from "dayjs";

export default function PreparedHistoryModal({recipeId, recipeTitle, open, onClose}) {
  const [preparedHistory, setPreparedHistory] = useState([]);

  async function handleDelete(preparedDateId) {
    await deletePreparedDate(preparedDateId);
    setPreparedHistory(preparedHistory.filter(item => item.id !== preparedDateId));
  }

  useEffect(() => {
    if (open) {
      findPreparedTimes(recipeId)
        .then((data) => setPreparedHistory(data))
        .catch((error) => {
          console.error("Error:", error);
        });
    }
  }, [recipeId, setPreparedHistory, open]);

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Minu "{recipeTitle}" valmistuskorrad</DialogTitle>
      <Stack alignItems="center" spacing={1} sx={{marginBottom: "1em"}}>
        {preparedHistory.map((item) => (
          <Typography variant="body1" key={item.id}>{dayjs(item.preparedTime).format('DD.MM.YYYY')}<IconButton size="small" onClick={() => handleDelete(item.id)}><DeleteIcon/></IconButton></Typography>
        ))}
      </Stack>
    </Dialog>
  )

}