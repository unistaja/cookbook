import TextField from "@mui/material/TextField";
import Alert from "@mui/material/Alert";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import CheckIcon from '@mui/icons-material/Check';
import { useState } from "react";
import { changePassword } from "../api";
import { Typography } from "@mui/material";

export default function ChangePasswordView() {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [passwordChanged, setPasswordChanged] = useState(false);

  async function handlePasswordChange() {
    if (!oldPassword) {
      alert("Sisesta vana parool!");
      return;
    }
    if (!newPassword) {
      alert("Sisesta uus parool");
      return;
    }
    await changePassword(oldPassword, newPassword);
    setPasswordChanged(true);
    setOldPassword('');
    setNewPassword('');
    setTimeout(() => setPasswordChanged(false), 2000);
  }
  return (
    <Stack spacing={2} alignItems={"center"} paddingTop={2}>
      <Typography variant="h5">Muuda parool</Typography>
      <TextField label="Vana parool" value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} type="password"/>
      <TextField label="Uus parool" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} type="password"/>
      <Button onClick={handlePasswordChange} variant="contained" color="secondary">Salvesta</Button>
      {passwordChanged && (
        <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
          Parool muudetud
        </Alert>
      )}
    </Stack>

  );
}