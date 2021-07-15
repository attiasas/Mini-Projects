var express = require("express");
var router = express.Router();
var axios = require("axios");

const externalAPI = require("../utils/external_api_utils");

const api_domain = "https://api.spoonacular.com/recipes";

router.get("/preview", async (req, res, next) => {
    try {
        // parameters exist
        console.log(typeof(req.query.ids));
        if (!req.query.ids || typeof(req.query.ids) != "array")
            throw { status: 400, message: "Request Body Not Following The API" };
        

        let recipesList = await externalAPI.getRecipesPreview(req.query.ids); // get a list of recipes containing one recipe

        console.log(recipesList);

        recipesList == undefined ? res.send(recipesList) : res.send(recipesList[0]);

    } catch (error) {
        next(error);
    }
});

router.get("/recipe/:recipeId", async (req, res, next) => {
    try {
        // parameters exist
        if (!req.params.recipeId)
            throw { status: 400, message: "Request Body Not Following The API" };

        res.send(await externalAPI.getRecipe(req.params.recipeId));
    } catch (error) {
        next(error);
    }
});

router.get("/search/query/:searchQuery/amount/:numberOfResults", async (req, res, next) => {
    try {

        // parameters exist
        if (!req.params.numberOfResults || !req.params.searchQuery)
            throw { status: 400, message: "Request Body Not Following The API" };

        const { diet, intolerances, cuisine } = req.query;

        // make the search call
        const search_response = await axios.get(`${api_domain}/search`, {
            params: {
                query: req.params.searchQuery,
                cuisine: cuisine,
                diet: diet,
                intolerances: intolerances,
                number: req.params.numberOfResults,
                instructionsRequired: true,
                apiKey: process.env.spooncular_apiKey
            }
        });

        if (search_response.data.results.length == 0)
            throw { status: 409, message: "No recipes found for the search" };

        //get the ids of the recipes from the search
        let recipeIds = externalAPI.extractRecipeIds(search_response.data.results);

        //get the recipes preview
        let recipesPreview = await externalAPI.getRecipesPreview(recipeIds);

        // console.log(recipe_ids);

        res.send(recipesPreview);
    } catch (error) {
        next(error);
    }
});

router.get("/fetchrandom", async (req, res, next) => {
    try {
        // parameters exist
        if (!req.query.number)
            throw { status: 400, message: "Request Body Not Following The API" };

        random_recipes = await externalAPI.getRandomRecipes(req.query.number);

        while (externalAPI.containsNonInstructionsRecipe(random_recipes)) {
            random_recipes = await externalAPI.getRandomRecipes(req.query.number);
        }

        recipesPreview = [];
        recipesPreview = externalAPI.extractRecipesPreviewFromRecipes(random_recipes);

        res.status(200).send(recipesPreview);

    }
    catch (error) {
        next(error);
    }
});

module.exports = router;