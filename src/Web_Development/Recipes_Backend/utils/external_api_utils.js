const axios= require("axios"); // supports promises
const api_domain = "https://api.spoonacular.com/recipes";
const api_key = process.env.api_key;



exports.extractRecipeIds = function (recipes) {
    recipe_ids = [];
    recipes.map((recipe) =>
        recipe_ids.push(recipe.id)
    );
    return recipe_ids;
}


exports.getRecipesPreview = async function (recipesIdList) {
    try {
        let url_list = [];
        recipesIdList.map((id) =>
            url_list.push(`${api_domain}/${id}/information?apiKey=${process.env.spooncular_apiKey}`)
        );
        // console.log(url_list);
        let info_response = await promiseAll(axios.get, url_list);

        recipesPreview = extractRecipesPreviewFromPromises(info_response);
        // console.log(recipesPreview);
        return recipesPreview;
    }
    catch (error) {

    }

}

function extractRecipesPreviewFromPromises(recipes_info_promises) {
    // console.log(recipes_info_promises);
    return recipes_info_promises.map((recipe_info) => {
        const {
            id,
            title,
            readyInMinutes,
            aggregateLikes,
            vegetarian,
            vegan,
            glutenFree,
            image
        } = recipe_info.data;

        return {
            recipeId: id,
            name: title,
            image: image,
            readyInMinutes: readyInMinutes,
            likes: aggregateLikes,
            vegetarian: vegetarian,
            vegan: vegan,
            glutenFree: glutenFree
        }
    })
}


let promiseAll = async function (func, param_list) {
    let promises = [];
    param_list.map((param) => promises.push(func(param)));
    let info_response = await Promise.all(promises);

    return info_response;
}

exports.getRecipe = async function (id) {
    const recipe = await getRecipeFromApi(id);
    var ourRecipe = {
        recipeId: recipe.data.id,
        name: recipe.data.title,
        image: recipe.data.image,
        readyInMinutes: recipe.data.readyInMinutes,
        likes: recipe.data.aggregateLikes,
        vegetarian: recipe.data.vegetarian,
        vegan: recipe.data.vegan,
        glutenFree: recipe.data.glutenFree,
        servings: recipe.data.servings,
    };

    // add ingredients
    ingredients = [];
    recipe.data.extendedIngredients.map((ingredient) => ingredients.push({
        name: ingredient.name,
        unit: ingredient.unit,
        amount: ingredient.amount,
        image: ingredient.image
    }));

    ourRecipe.ingredients = ingredients;

    //add instructions by steps
    steps = [];
    recipe.data.analyzedInstructions[0].steps.map((step) => steps.push({
        step_number: step.number,
        step_content: step.step
    }));

    ourRecipe.steps = steps;
    return ourRecipe;
}

 function getRecipeFromApi(id) {
    return axios.get(`${api_domain}/${id}/information`, {
        params: {
            includeNutrition: false,
            apiKey: process.env.spooncular_apiKey
        }
    });
}

exports.getRandomRecipes = async function (numberOfRadomRecipes) {
    const random_response = await axios.get(`${api_domain}/random`, {
        params: {
            number: numberOfRadomRecipes,
            apiKey: process.env.spooncular_apiKey
        }
    });

    random_recipes = random_response.data.recipes;
    return random_recipes;
}

exports.containsNonInstructionsRecipe = function(recipes) {
    for (let i = 0; i < recipes.length; i++) {
        let recipe = recipes[i];
        if (recipe.analyzedInstructions.length == 0) {
            return true;
        }
    }
    return false;
}



exports.extractRecipesPreviewFromRecipes = function(recipes_info) {
    return recipes_info.map((recipe_info) => {
        const {
            id,
            title,
            readyInMinutes,
            aggregateLikes,
            vegetarian,
            vegan,
            glutenFree,
            image
        } = recipe_info;

        return {
            recipeId: id,
            name: title,
            image: image,
            readyInMinutes: readyInMinutes,
            likes: aggregateLikes,
            vegetarian: vegetarian,
            vegan: vegan,
            glutenFree: glutenFree
        }
    })
}