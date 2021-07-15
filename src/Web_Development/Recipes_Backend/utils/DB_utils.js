require("dotenv").config();
const sql = require("mssql");

const config = {
  user: process.env.tedious_userName,
  password: process.env.tedious_password,
  server: process.env.tedious_server,
  database: process.env.tedious_database,
  options: {
    encrypt: true,
    enableArithAbort: true
  }
};

const pool = new sql.ConnectionPool(config);
const poolConnect = pool.connect();

exports.searchUserByID = async function(user_id)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('id',sql.Int);

    await ps.prepare('SELECT * FROM dbo.users WHERE id=@id');
    let result = await ps.execute({id:user_id});
    await ps.unprepare();

    return result.recordset.length == 1 ? result.recordset[0] : null;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.searchUserByUserName = async function (userName)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('un',sql.VarChar(50));

    await ps.prepare('SELECT * FROM dbo.users WHERE userName=@un');
    let result = await ps.execute({un:userName});
    await ps.unprepare();

    return result.recordset;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.addUser = async function (user_data)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('userName',sql.VarChar(30));
    ps.input('password',sql.VarChar(300));
    ps.input('firstName',sql.VarChar(50));
    ps.input('lastName',sql.VarChar(50));
    ps.input('country',sql.VarChar(50));
    ps.input('email',sql.VarChar(50));
    ps.input('img',sql.VarChar(300));

    await ps.prepare('INSERT INTO dbo.users (userName, password,firstName,lastName,country,email,img) VALUES (@userName,@password,@firstName,@lastName,@country,@email,@img)');
    let result = await ps.execute(user_data);
    await ps.unprepare();
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.setRecipeWatch = async function (user_id,recipe_id)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('uid',sql.VarChar(50));
    ps.input('rid',sql.VarChar(50));

    await ps.prepare('SELECT recipe_id FROM dbo.users_watched WHERE user_id=@uid AND recipe_id=@rid');
    let result = await ps.execute({uid:user_id,rid:recipe_id});
    await ps.unprepare();

    if(result.recordset.length > 0)
    {
      ps = new sql.PreparedStatement(pool);
      ps.input('uid',sql.VarChar(50));
      ps.input('rid',sql.VarChar(50));
  
      await ps.prepare('UPDATE dbo.users_watched SET watchTime=CURRENT_TIMESTAMP WHERE user_id=@uid AND recipe_id=@rid');
      let result = await ps.execute({uid:user_id,rid:recipe_id});
      await ps.unprepare();
    }
    else
    {
      ps = new sql.PreparedStatement(pool);
      ps.input('uid',sql.VarChar(50));
      ps.input('rid',sql.VarChar(50));
  
      await ps.prepare('INSERT INTO dbo.users_watched (user_id,recipe_id) VALUES (@uid,@rid)');
      let result = await ps.execute({uid:user_id,rid:recipe_id});
      await ps.unprepare();
    }
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getWatchedRecipeByUser = async function (user_id)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('id',sql.Int);

    await ps.prepare('SELECT recipe_id,watchTime FROM dbo.users_watched WHERE user_id=@id ORDER BY watchTime DESC');
    let result = await ps.execute({id:user_id});
    await ps.unprepare();

    return result.recordset;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.setRecipeFavorite = async function (user_id,recipe_id)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
      ps.input('uid',sql.VarChar(50));
      ps.input('rid',sql.Int);
  
      await ps.prepare('INSERT INTO dbo.users_favorites (user_id,recipe_id) VALUES (@uid,@rid)');
      let result = await ps.execute({uid:user_id,rid:recipe_id});
      await ps.unprepare();
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getFavoriteRecipeByUser = async function (user_id)
{
  try
  {
    await poolConnect;
    
    ps = new sql.PreparedStatement(pool);
    ps.input('id',sql.Int);

    await ps.prepare('SELECT recipe_id FROM dbo.users_favorites WHERE user_id=@id');
    let result = await ps.execute({id:user_id});
    await ps.unprepare();

    return result.recordset;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getFamilyRecipesByUser = async function (user_id) {
  try {
    await poolConnect;

    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);


    await ps.prepare('SELECT * FROM dbo.family_recipes fr WHERE fr.user_id = @user_id');

    let recipes = await ps.execute({ user_id: user_id });

    await ps.unprepare();

    recipesDB = recipes.recordset;

    recipes = [];

    recipesDB.map((recipe) => {
      recipes.push({
        recipe_id: recipe.recipe_id,
        name: recipe.name,
        image: recipe.image,
        readyInMinutes: recipe.readyInMinutes,
        vegetarian: recipe.vegetarian,
        vegan: recipe.vegan,
        glutenFree: recipe.glutenFree
      });

      // recipe.instructions = JSON.parse(recipe.instructions);
      // recipe.ingredients = JSON.parse(recipe.ingredients);
    }); //parse the recipes instructions and ingredients to JSON

    return recipes;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getMyRecipes = async function (user_id) {
  try {
    await poolConnect;

    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);


    await ps.prepare('SELECT * FROM dbo.my_recipes fr WHERE fr.user_id = @user_id');

    let recipes = await ps.execute({ user_id: user_id });

    await ps.unprepare();

    recipesDB = recipes.recordset;

    recipes =[];
    
    recipesDB.map((recipe) => {
      recipes.push({
        recipe_id: recipe.recipe_id,
        name: recipe.name,
        image: recipe.image,
        readyInMinutes: recipe.readyInMinutes,
        vegetarian: recipe.vegetarian,
        vegan: recipe.vegan,
        glutenFree: recipe.glutenFree
      });
    });

    return recipes;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getFamilyRecipe = async function (user_id, recipe_id) {
  try {
    await poolConnect;

    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);
    ps.input('recipe_id', sql.Int);


    await ps.prepare('SELECT * FROM dbo.family_recipes fr WHERE fr.user_id = @user_id AND fr.recipe_id = @recipe_id');

    let recipes = await ps.execute({ user_id: user_id, recipe_id: recipe_id });

    await ps.unprepare();

    recipesDB = recipes.recordset; //gets a list of one recipe by the recipe's id
    recipe = recipesDB[0];

    recipe.steps = JSON.parse(recipe.instructions); //make it compatable to the API
    delete recipe.instructions;
    recipe.ingredients = JSON.parse(recipe.ingredients);

    delete recipe.recipe_id;

    return recipe;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.getMyRecipe = async function (user_id, recipe_id) {
  try {
    await poolConnect;
    console.log("uid: " + user_id + " , rid: " + recipe_id);
    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);
    ps.input('recipe_id', sql.Int);


    await ps.prepare('SELECT * FROM dbo.my_recipes mr WHERE mr.user_id = @user_id AND mr.recipe_id = @recipe_id');
    console.log("t1");
    let recipes = await ps.execute({ user_id: user_id, recipe_id: recipe_id });
    console.log("t2");
    await ps.unprepare();

    recipesDB = recipes.recordset; //gets a list of one recipe by the recipe's id
    recipe = recipesDB[0];

    recipe.steps = JSON.parse(recipe.instructions); //make it compatable to the API
    delete recipe.instructions;
    recipe.ingredients = JSON.parse(recipe.ingredients);

    delete recipe.recipe_id;
    console.log(recipe);
    return recipe;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }
}

exports.addFamilyRecipes = async function(uid,auth,when,name,img,readyIn,veg,vegan,gluten,serv,instruct,ingred)
{
  try {
    await poolConnect;

    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);
    ps.input('author', sql.Text);
    ps.input('whenUsed', sql.Text);
    ps.input('name', sql.Text);
    ps.input('image', sql.Text);
    ps.input('readyInMinutes', sql.Int);
    ps.input('vegetarian', sql.Bit);
    ps.input('vegan', sql.Bit);
    ps.input('glutenFree', sql.Bit);
    ps.input('servings', sql.Int);
    ps.input('instructions', sql.VarChar(1000));
    ps.input('ingredients', sql.VarChar(1000));

    await ps.prepare('INSERT INTO dbo.family_recipes \
    (user_id,author,whenUsed,name,image,readyInMinutes,vegetarian,vegan,glutenFree,servings,instructions,ingredients)\
    VALUES (@user_id,@author,@whenUsed,@name,@image,@readyInMinutes,@vegetarian,@vegan,@glutenFree,@servings,@instructions,@ingredients)');

    let result = await ps.execute({user_id: uid,
       author: auth,
       whenUsed: when,
       name: name,
       image: img,
       readyInMinutes: readyIn,
       vegetarian: veg,
       vegan: vegan,
       glutenFree: gluten,
       servings: serv,
       instructions: JSON.stringify(instruct),
       ingredients: JSON.stringify(ingred)
    });

    await ps.unprepare();

    return;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }

}

exports.addMyRecipes = async function(uid,name,img,readyIn,veg,vegan,gluten,serv,instruct,ingred)
{
  try {
    await poolConnect;

    ps = new sql.PreparedStatement(pool);
    ps.input('user_id', sql.Int);
    ps.input('name', sql.Text);
    ps.input('image', sql.Text);
    ps.input('readyInMinutes', sql.Int);
    ps.input('vegetarian', sql.Bit);
    ps.input('vegan', sql.Bit);
    ps.input('glutenFree', sql.Bit);
    ps.input('servings', sql.Int);
    ps.input('instructions', sql.VarChar(1000));
    ps.input('ingredients', sql.VarChar(1000));

    await ps.prepare('INSERT INTO dbo.my_recipes \
    (user_id, name,image,readyInMinutes,vegetarian,vegan,glutenFree,servings,instructions,ingredients)\
    VALUES (@user_id,@name,@image,@readyInMinutes,@vegetarian,@vegan,@glutenFree,@servings,@instructions,@ingredients)');

    let result = await ps.execute({user_id: uid,
       name: name,
       image: img,
       readyInMinutes: readyIn,
       vegetarian: veg,
       vegan: vegan,
       glutenFree: gluten,
       servings: serv,
       instructions: JSON.stringify(instruct),
       ingredients: JSON.stringify(ingred)
    });

    await ps.unprepare();

    return;
  }
  catch (err) {
    console.error("SQL error", err);
    throw err;
  }

}