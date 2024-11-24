import Stack from "@mui/material/Stack";
import { addUser, changeUserPassword } from "../api";
import UserAdminForm from "../components/UserAdminForm";

export default function AdminView() {
  return (
    <Stack spacing={2} alignItems={"center"} paddingTop={2}>
      <UserAdminForm title="Muuda kasutaja parooli" actionTitle="Muuda parool" successMessage="Parool muudetud" apiMethod={changeUserPassword}/>
      <UserAdminForm title="Lisa kasutaja" actionTitle="Loo kasutaja" successMessage="Kasutaja loodud" apiMethod={addUser}/>
      
    </Stack>

  );
}