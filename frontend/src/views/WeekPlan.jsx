import * as React from 'react';
import {Container, Grid, styled} from "@mui/material";
import Button from "@mui/material/Button";
import {createTheme} from "@mui/material/styles";
import {Fragment, useEffect, useState} from "react";
import { Link } from 'react-router-dom';

const days = ['Esmaspäev', "Teisipäev", "Kolmapäev", "Neljapäev", "Reede", "Laupäev", "Pühapäev"]

export default function WeekPlan() {
    return (
        <Container>
            <Grid container spacing={2} mt={2} columnSpacing={0}>
                { days.map(day => DayPlan({day})) }
            </Grid>
        </Container>
    );
}

function DayPlan({day}) {
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
                setRecipe(data.recipes[0]);
            })
    }

    useEffect(() => {
        fetchNewRecipe();
    }, [setRecipe])
    return (
        <Fragment>
            <Grid item xs={3}>
                <Button variant="contained" onClick={ fetchNewRecipe }>
                    Genereeri uus päeva retsept
                </Button>
            </Grid>
            <Grid item xs={9}>
                <StyledDiv mt>
                    {day}
                </StyledDiv>
                <Link to={"/recipe/" + recipe?.id}>{recipe?.name}</Link>
            </Grid>
        </Fragment>
    );
}

