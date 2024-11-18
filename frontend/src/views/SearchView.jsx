import Grid from "@mui/material/Grid2";
import SearchForm from "../components/SearchForm";
import RecipeList from "../components/RecipeList";
import { useCallback, useEffect, useState } from "react";
import { findRecipes, getAutoFillData } from "../api";

const defaultSearch = {
  name: null,
  source: null,
  hasPicture: false,
  resultsPerPage: 10,
  sortOrder: 2,
  descending: true,
  username: null,
  withIngredients: [],
  withoutIngredients: [],
  categories: [],
  userId: null,
  hasPrepared: null
};

export default function SearchView() {
  const [autoFillData, setAutoFillData] = useState({});
  const [searchResult, setSearchResult] = useState({recipes: [], total: 0});
  const [currentSearch, setCurrentSearch] = useState({...defaultSearch})
  const [currentPage, setCurrentPage] = useState(0);

  const search = useCallback(async (searchParams) => {
    const result = await findRecipes(searchParams);
    setSearchResult(result);
  }, [])

  async function onSearch(newSearch) {
    await search({...newSearch, resultPage: 0});
    setCurrentPage(0);
    setCurrentSearch(newSearch);
  }

  async function onPageChange(event, newPage) {
    await search({...currentSearch, resultPage: newPage});
    setCurrentPage(newPage);

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
      ...defaultSearch,
      resultPage: 0
    })
      .then((data) => setSearchResult(data))
      .catch((error) => {
        console.error("Error:", error);
      });
  }, []);

  return (
    <Grid container>
      <Grid size="grow"></Grid>
      <Grid size={{xs: 12, sm: 12, md: 10, lg: 8, xl: 8}}>
      <SearchForm onSearch={onSearch} currentSearch={currentSearch} onSearchChange={setCurrentSearch} autoFillData={autoFillData}/>
      <RecipeList data={searchResult} currentPage={currentPage} onPageChange={onPageChange} rowsPerPage={currentSearch.resultsPerPage}/>
      </Grid>
      <Grid size="grow"></Grid>
    </Grid>
  );

}