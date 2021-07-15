var express = require("express");
const session = require("client-sessions");
const bcrypt = require("bcryptjs");
const DB = require("../utils//DB_utils");

var router = express.Router();

router.post("/signUp", async function(req,res,next)
{
    let user_data = req.body;
    console.log(user_data);

    try 
    {
        // parameters exists
        if(!user_data.userName || !user_data.password || !user_data.firstName || !user_data.lastName || !user_data.country || !user_data.email)
            throw { status: 400, message: "Request Body Not Following The API" };

        // valid parameters
        validateRegisterData(user_data);

        // username exists
        const users = await DB.searchUserByUserName(user_data.userName);
        console.log(users);
    
        if (users.length > 0)
           throw { status: 409, message: "Username taken" };
    
        // add the new username
        user_data.password = bcrypt.hashSync(user_data.password, 14);
        await DB.addUser(user_data);
        
        res.status(201).send("user created");
    } 
    catch (error) 
    {
        next(error);
    }
});

router.post("/logIn",async function(req,res,next)
{
    let user_data = req.body;

    try 
    {
        // parameters exists
        if(!user_data.userName || !user_data.password)
            throw { status: 400, message: "Request Body Not Following The API" };

        // valid parameters
        const users = await DB.searchUserByUserName(user_data.userName);

        if (users.length != 1)
           throw { status: 409, message: "Authentication failed" };

        let user = users[0];
        
        if(!bcrypt.compareSync(user_data.password, user.password))
            throw { status: 409, message: "Authentication failed" };

        // set cookie
        req.session.id = user.id;

        res.sendStatus(200);
    }
    catch (error) 
    {
        next(error);
    }
});

function validateRegisterData(user_data)
{
    // validate full information is provided
    if(!/^[a-zA-Z]{3,8}$/.test(user_data.userName))
        throw { status: 409, message: "Parameter 'userName' Not Provided According to the API" };

    if(!/^(?=.{5,10})(?=.*[0-9])(?=.*[@#$%^&+=]).*$/.test(user_data.password))
        throw { status: 409, message: "Parameter 'password' Not Provided According to the API" };

    //if(!/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(user_data.email))
    //    throw { status: 409, message: "Parameter 'email' Not Provided According to the API" };
}

module.exports = router;