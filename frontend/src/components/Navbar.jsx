import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import Toolbar from '@mui/material/Toolbar';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import Link from "@mui/material/Link";
import {
  Link as RouterLink,
  useLocation,
} from 'react-router-dom';

const knownPaths = ["/", "/add-recipe", "/search", "/weekplan"];
export default function Navbar({user}) {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const { pathname } = useLocation();
  const open = Boolean(anchorEl);
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  return (
    <AppBar position="sticky">
      <Toolbar variant="dense" disableGutters>
        <Tabs value={knownPaths.includes(pathname) ? pathname : false} sx={{flexGrow: 1}} textColor="inherit" TabIndicatorProps={{style: {backgroundColor: 'black'}}}>
          <Tab label="Avaleht" value="/" to="/" component={RouterLink}/>
          <Tab label="Lisa retsept" value="/add-recipe" to="/add-recipe" component={RouterLink}/>
          <Tab label="Otsing" value="/search" to="/search" component={RouterLink}/>
          <Tab label="Nädalaplaan" value="/weekplan" to="/weekplan" component={RouterLink}/>
        </Tabs>
        <Button startIcon={<AccountCircleIcon />} color="inherit" onClick={handleClick}>
          {user.username}
        </Button>
        <Menu
          id="basic-menu"
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
        >
          <MenuItem component={RouterLink} to="/user" onClick={handleClose}>Muuda parooli</MenuItem>
          {user.isAdmin && <MenuItem component={RouterLink} to="/admin" onClick={handleClose}>Halda kasutajaid</MenuItem>}
          <MenuItem component={Link} href="/logout" onClick={handleClose}>Logi välja</MenuItem>
        </Menu>

      </Toolbar>
    </AppBar>
  );
}