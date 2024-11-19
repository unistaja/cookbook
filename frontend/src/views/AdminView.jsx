import TextField from "@mui/material/TextField";
import Alert from "@mui/material/Alert";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import CheckIcon from '@mui/icons-material/Check';
import { useState } from "react";
import { addUser } from "../api";
import { Typography } from "@mui/material";

export default function AdminView() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [userCreated, setUserCreated] = useState(false);

  async function handleUserCreated() {
    if (!username) {
      alert("Sisesta kasutajanimi!");
      return;
    }
    if (!password) {
      alert("Sisesta parool");
      return;
    }
    await addUser(username, password);
    setUserCreated(true);
    setUsername('');
    setPassword('');
    setTimeout(() => setUserCreated(false), 2000);
  }
  return (
    <Stack spacing={2} alignItems={"center"} paddingTop={2}>
      <Typography variant="h5">Lisa kasutaja</Typography>
      <TextField label="Kasutajanimi" value={username} onChange={(e) => setUsername(e.target.value)}/>
      <TextField label="Parool" value={password} onChange={(e) => setPassword(e.target.value)} type="password"/>
      <Button onClick={handleUserCreated} variant="contained" color="secondary">Loo kasutaja</Button>
      {userCreated && (
        <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
          Kasutaja loodud
        </Alert>
      )}
    </Stack>

  );
}