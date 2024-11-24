import TextField from "@mui/material/TextField";
import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";
import CheckIcon from '@mui/icons-material/Check';
import { useState } from "react";
import { Typography } from "@mui/material";

export default function UserAdminForm({title, actionTitle, successMessage, apiMethod}) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [actionSucceeded, setActionSucceeded] = useState(false);

  async function handleActionClick() {
    if (!username) {
      alert("Sisesta kasutajanimi!");
      return;
    }
    if (!password) {
      alert("Sisesta parool");
      return;
    }
    await apiMethod(username, password);
    setActionSucceeded(true);
    setUsername('');
    setPassword('');
    setTimeout(() => setActionSucceeded(false), 2000);
  }
  return (
    <>
      <Typography variant="h5">{title}</Typography>
      <TextField label="Kasutajanimi" value={username} onChange={(e) => setUsername(e.target.value)}/>
      <TextField label="Parool" value={password} onChange={(e) => setPassword(e.target.value)} type="password"/>
      <Button onClick={handleActionClick} variant="contained" color="secondary">{actionTitle}</Button>
      {actionSucceeded && (
        <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
          {successMessage}
        </Alert>
      )}
    </>
  )
}