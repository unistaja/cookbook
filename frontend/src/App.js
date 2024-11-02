import { useState, useEffect } from 'react';
import { Routes, Route, Outlet, Link } from "react-router-dom";
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { green } from '@mui/material/colors';
import Navbar from './components/Navbar';
import WeekPlan from "./components/WeekPlan";

const theme = createTheme({
  palette: {
    primary: green,
    secondary: {
      main: '#ccff90',
    },
  },
});

export default function App() {
  const [user, setUser] = useState({});

  useEffect(() => {
    fetch('/api/user')
      .then((response) => {
        if (!response.ok) {
          throw new Error("Invalid response when fetching user");
        }
        return response.json();
      })
      .then((data) => setUser(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [setUser]);

  return (
    <ThemeProvider theme={theme}>
      {/* Routes nest inside one another. Nested route paths build upon
            parent route paths, and nested route elements render inside
            parent route elements. See the note about <Outlet> below. */}
      <Routes>
        <Route path="/" element={<Layout user={user}/>}>
          <Route index element={<Home />} />
            <Route path="weekplan" element={<WeekPlan />}></Route>

          {/* Using path="*"" means "match anything", so this route
                acts like a catch-all for URLs that we don't have explicit
                routes for. */}
          <Route path="*" element={<NoMatch />} />
        </Route>
      </Routes>
    </ThemeProvider>
  );
}

function Layout({user}) {
  return (
    <>
      <Navbar user={user}/>
      <Outlet />
    </>
  );
}

function Home() {
  return (
    <div>
      Tere tulemast retseptibaasi!
    </div>
  );
}

function NoMatch() {
  return (
    <div>
      <h2>Lehte ei leitud!</h2>
      <p>
        <Link to="/">Mine avalehele</Link>
      </p>
    </div>
  );
}