function showDiv(arg)
{
    if(arg == "about")
    {
        window.open();
    }
    else
    {
        document.getElementById("welcome").hidden = true;
        document.getElementById("signUp").hidden = true;
        document.getElementById("logIn").hidden = true;
        document.getElementById("settings").hidden = true;
        document.getElementById("gameState").hidden = true;

        document.getElementById(arg).hidden = false;
        music.pause();
        window.clearInterval(interval);
    }
}
var loggedUser = null;

function initUserList()
{
    //userList = [];
    // create default user
    //addUser("p","p","default user","default","default");
}

class User
{
    constructor(userName,password,name,email,birthDate)
    {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }
}

var userList = [new User("p","p","default user","default","default")];

function addUser(userName,password,name,email,birthDate)
{
    let user = new User(userName,password,name,email,birthDate);

    userList.push(user);

    return user;
}


$(document).ready(function() 
{
    // read/create user list
    initUserList();

    // modal dialog init
    let modal = document.getElementById("about");
    document.getElementById("aboutBtn").onclick = function(){ modal.style.display = "block"; };
    document.getElementById("aboutClose").onclick = function(){ modal.style.display = "none"; };
    window.onclick = function(event) {
        if (event.target == modal) {
          modal.style.display = "none";
        }
    }
    addEventListener(
		"keydown",
		function(e) {
            if(e.keyCode == 27 && modal.style.display != "none")
            {
                modal.style.display = "none";
            }
		},
		false
	);

    // signUp validation
    $.validator.addMethod("alphaDigit",function(value)
    {
        return (/^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$/).test(value) && value.length >= 6;
    }, "At least 6 characters and at least one digit and one alphabet character");

    $.validator.addMethod("onlyAlpha",function(value)
    {
        return (/^[a-z ,.'-]+$/).test(value);
    }, "this field is required and cannot contain digits.");
    
    $("#signUpForm").validate({
        rules: {
            signUpUserName: "required",
            signUpPassword: "alphaDigit",
            signUpFullName: "onlyAlpha",
            signUpEmail: {required: true, email: true},
            signUpDate: "required"
        },
        submitHandler: function() {
            
            loggedUser = addUser($("#signUpUserName").val(),$("#signUpPassword").val(),$("#signUpFullName").val(),$("#signUpEmail").val(),$("#signUpDate").val());
            
            showDiv('settings');
		}
    });
    
    $("#logInForm").validate({
        submitHandler: function() {
            let user = {
                userName: $("#logInUserName").val(),
                password: $("#logInPassword").val()
            };

            for(let i = 0; i < userList.length && loggedUser == null; i++)
            {
                if(user.userName == userList[i].userName && user.password == userList[i].password) loggedUser = userList[i];
            }

            if(loggedUser != null)
            {
                showDiv('settings');
            }
		}
    });
    
});
