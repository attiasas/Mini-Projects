// Lib imports
require("dotenv").config();
const express = require("express");
const bodyParser = require("body-parser");
const morgan = require("morgan");
const session = require("client-sessions");
const cors = require("cors");
// routs import
const auth = require("./routes/auth");
const users = require("./routes/users");
const recipes = require("./routes/recipes");
// app config
const app = express();
const port = process.env.PORT;
app.use(bodyParser.urlencoded({extended: false})); // parse application/x-www-form-urlencoded
app.use(bodyParser.json()); // parse application/json

app.use(morgan(":method :url :status :response-time ms")); // print request logs

const corsConfig = {
    origin: true,
    credentials: true
};

app.use(cors(corsConfig));
app.options("*", cors(corsConfig));
  
app.use(
    session({
        cookieName: "session",              // cookie name
        secret: process.env.COOKIE_SECRET,  // encryption key
        duration: 10 * 60 * 10 * 1000,                // expired after 10 min
        activeDuration: 0,                   // if expiresIn < activeDuration
        cookie: {
            httpOnly: false
        }
    })
);

// -- Populate (Only For First Time) --
/*const populate = require("./scripts/Populate");
app.get("/populate", async (req,res,next) =>{
    try
    {
        await populate.populateDB();
        res.sendStatus(201);
    }
    catch(err)
    {
        console.log(err);
        next(err);
    }
});*/

// routing
app.use("/users",users);
app.use("/recipes",recipes);
app.use(auth);

// Catch Errors
app.use(function (err, req, res, next) {
    console.error(err);
    res.sendStatus(err.status || 500);
});

// Default Router
app.get((req,res) =>{
    res.sendStatus(404);
});

app.listen(port,() => {
    console.log(`App listening on port ${port}!`);
});