import Grid from "@mui/material/Grid2";
import SearchForm from "../components/SearchForm";
import RecipeList from "../components/RecipeList";
import { useEffect, useState } from "react";
import { findRecipes, getAutoFillData } from "../api";

export default function SearchView({currentSearch, setCurrentSearch}) {
  const [autoFillData, setAutoFillData] = useState({});
  const [searchResult, setSearchResult] = useState({recipes: [], total: 0});

  async function onSearch(newSearch) {
    setCurrentSearch({...newSearch, resultPage: 0});
  }

  async function onPageChange(event, newPage) {
    setCurrentSearch({...currentSearch, resultPage: newPage});
  }

  useEffect(() => {
    getAutoFillData()
      .then((data) => setAutoFillData(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [setAutoFillData]);

  useEffect(() => {
    findRecipes({
      ...currentSearch
    })
      .then((data) => setSearchResult(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [currentSearch]);

  return (
    <Grid container>
      <Grid size="grow"></Grid>
      <Grid size={{xs: 12, sm: 12, md: 10, lg: 8, xl: 8}}>
      <SearchForm onSearch={onSearch} currentSearch={currentSearch} onSearchChange={setCurrentSearch} autoFillData={autoFillData}/>
      <RecipeList data={searchResult} currentPage={currentSearch.resultPage} onPageChange={onPageChange} rowsPerPage={currentSearch.resultsPerPage}/>
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );

}