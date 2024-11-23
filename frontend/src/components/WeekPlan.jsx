import * as React from 'react';
import {Container, styled} from "@mui/material";
import Grid from "@mui/material/Grid2";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import {createTheme} from "@mui/material/styles";
import {Fragment, useEffect, useState} from "react";
import { Link } from 'react-router-dom';
import SimpleRecipeCard from './SimpleRecipeCard';
import RefreshIcon from '@mui/icons-material/Refresh';
import PostAddIcon from '@mui/icons-material/PostAdd';
import { addRecipeToMenu } from '../api';

const days = ['Esmaspäev', "Teisipäev", "Kolmapäev", "Neljapäev", "Reede", "Laupäev", "Pühapäev"]

export default function WeekPlan({onRecipeAddedToMenu}) {
    return (
        <Paper variant="outlined" sx={{align: "center", padding: "20px"}}>
        <Typography variant="h5">Juhuslik valik:</Typography>
        <Grid container spacing={2} mt={2} >
            { days.map(day => DayPlan({day, onRecipeAddedToMenu})) }
        </Grid>
        </Paper>
    );
}

function DayPlan({day, onRecipeAddedToMenu}) {
    const theme = createTheme();
    const StyledDiv = styled('div')({
        marginTop: theme.spacing(1)
    })
    const [recipe, setRecipe] = useState({});
    const fetchNewRecipe = () => {
            fetch('api/search', {
                method: 'POST',
                url: 'api/search',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: null,
                    source: null,
                    hasPicture: false,
                    resultsPerPage: 1,
                    resultPage: 0,
                    sortOrder: 3,
                    descending: false,
                    username: null,
                    withIngredients: [],
                    withoutIngredients: [],
                    categories: [],
                    userId: null,
                    hasPrepared: null
                })
            }).then(response => {
                if (!response.ok) {
                    throw new Error("Invalid response when fetching recipe");
                }
                return response.json();
            }).then(data => {
                setRecipe({...data.recipes[0], recipeId: data.recipes[0].id});
            })
    }

    async function handleAddRecipeToMenu() {
        await addRecipeToMenu(recipe.recipeId);
        onRecipeAddedToMenu(recipe);
    }

    useEffect(() => {
        fetchNewRecipe();
    }, [setRecipe])
    return (
    <SimpleRecipeCard recipe={recipe} key={day}>
        <IconButton color="primary" onClick={ fetchNewRecipe }>
            <RefreshIcon/>
        </IconButton>
        <IconButton color="primary" onClick={ handleAddRecipeToMenu } sx={{marginLeft: "auto"}}>
            <PostAddIcon/>
        </IconButton>
    </SimpleRecipeCard>
    );
}

