import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import Toolbar from '@mui/material/Toolbar';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import HomeIcon from '@mui/icons-material/Home';
import SearchIcon from '@mui/icons-material/Search';
import PostAddIcon from '@mui/icons-material/PostAdd';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import Link from "@mui/material/Link";
import {
  Link as RouterLink,
  useLocation,
} from 'react-router-dom';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

const knownPaths = ["/", "/add-recipe", "/search", "/menuplan"];
export default function Navbar({user}) {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const { pathname } = useLocation();
  const theme = useTheme();
  const isTiny = useMediaQuery(theme.breakpoints.down('sm'));
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
          <Tab icon={<HomeIcon/>} value="/" to="/" component={RouterLink} aria-label="home" sx={{minWidth: "48px"}}/>
          <Tab label={isTiny ? undefined : "Lisa retsept"} icon={isTiny ? <PostAddIcon/> : undefined} value="/add-recipe" to="/add-recipe" component={RouterLink} sx={{minWidth: "48px"}}/>
          <Tab label={isTiny ? undefined : "Otsing"} icon={isTiny ? <SearchIcon/> : undefined} value="/search" to="/search" component={RouterLink} sx={{minWidth: "48px"}}/>
          <Tab label={isTiny ? undefined : "Menüü"} icon={isTiny ? <MenuBookIcon/> : undefined} value="/menuplan" to="/menuplan" component={RouterLink} sx={{minWidth: "48px"}}/>
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