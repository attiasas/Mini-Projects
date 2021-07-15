var express = require("express");
const DB = require("../utils//DB_utils");

var router = express.Router();

// Authenticate all incoming requests
router.use(async (req,res,next) => 
{
    try
    {
        if(req.session && req.session.id)
        {
            const id = req.session.id;
            const user = await DB.searchUserByID(id);

            if(user)
            {
                req.user = user;
                next();
                return;
            }
        }

        throw { status: 401, message: "Unauthorized" };
    }
    catch (error) 
    {
        next(error);
    }
});

router.get("/logOut", (req,res) => {
    req.session.reset();
    res.sendStatus(200);
});

router.get("/getInfo", (req,res) => {
    // Remove Password and ID for secrecy
    delete req.user.id;
    delete req.user.password;
    // Send Info
    res.status(200).send(req.user);
});

router.get("/lastwatch", async (req,res,next) => {
    try
    {
        let watched = await DB.getWatchedRecipeByUser(req.user.id);
        let result = [];

        let size = req.query.numberOfResults ? req.query.numberOfResults : 3;

        for(let i = 0; i < size && i < watched.length; i++)
        {
            result.push(watched[i].recipe_id);
        }

        console.log(watched);    
        res.send(result);
    }
    catch (error) 
    {
        next(error);
    }
});

router.post("/watch/:recipe_id", async (req,res,next) => 
{
    try
    {
        if(!req.params.recipe_id)
            throw { status: 400, message: "Request Not Following The API" };

        let watched = await DB.setRecipeWatch(req.user.id,req.params.recipe_id);
        res.sendStatus(201);
    }
    catch (error) 
    {
        next(error);
    }
});

router.get("/favorites", async (req,res,next) => {
    try
    {
        let favorites = await DB.getFavoriteRecipeByUser(req.user.id);
        let result = [];

        favorites.forEach(element => {
            result.push(element.recipe_id);
        });

        res.send(result);
    }
    catch (error) 
    {
        next(error);
    }
});

router.post("/favorites/:recipe_id", async (req,res,next) => {
    try
    {
        if(!req.params.recipe_id)
            throw { status: 400, message: "Request Not Following The API" };

        await DB.setRecipeFavorite(req.user.id,req.params.recipe_id);
        res.sendStatus(201);
    }
    catch (error) 
    {
        next(error);
    }
});

router.get("/recipeInfo", async (req, res, next) => {
    try
    {
        let ids;
        
        if(!req.query.ids)
            throw { status: 400, message: "Request Not Following The API" };

        ids = JSON.parse(req.query.ids);

        if(!Array.isArray(ids))
            throw { status: 400, message: "Request Not Following The API" };

        let watchList = await DB.getWatchedRecipeByUser(req.user.id);
        let favList = await DB.getFavoriteRecipeByUser(req.user.id);

        let result = {};

        ids.forEach(rid => 
        {
            result[rid] = {};
            result[rid].watched = false;
            result[rid].favorite = false;

            if(watchList.find(element => element.recipe_id == rid))
                result[rid].watched = true;

            if(favList.find(element => element.recipe_id == rid))
                result[rid].favorite = true;
        });

        res.send(result);
    }
    catch (error) 
    {
        next(error);
    }
});

// ------------------------ Recipes of user ------------------------

router.get("/familyrecipes", async (req, res, next) => {
    try {
        const familyRecipes = await DB.getFamilyRecipesByUser(req.user.id); // get recipe previews of family recipes
        res.send(familyRecipes);

    }
    catch (error) {
        next(error);
    }
});


router.get("/myrecipes", async (req, res,next) => {
    try {
        const myRecipes = await DB.getMyRecipes(req.user.id);
        res.send(myRecipes);
    }
    catch (error) {
        next(error);
    }
});


router.get("/familyrecipes/recipe/:recipe_id", async (req, res, next) => {
    if(!req.params.recipe_id)
      throw { status: 400, message: "Request Body Not Following The API" };

    try {
        const familyRecipe = await DB.getFamilyRecipe(req.user.id, req.params.recipe_id); // get recipe previews of family recipes
        res.send(familyRecipe);
    }
    catch (error) {
        next(error);
    }
});

router.get("/myrecipes/recipe/:recipe_id", async (req, res, next) => {
    //console.log(req);
    if(!req.params.recipe_id)
      throw { status: 400, message: "Request Body Not Following The API" };

    try {
        const myRecipe = await DB.getMyRecipe(req.user.id, req.params.recipe_id); // get recipe previews of family recipes
        res.send(myRecipe);
    }
    catch (error) {
        next(error);
    }
});

module.exports = router;