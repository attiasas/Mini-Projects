---------------------------------- Users ----------------------------------
CREATE TABLE [dbo].[users](
	[id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[userName] [varchar](30) NOT NULL UNIQUE,
	[password] [varchar](300) NOT NULL,
    [firstName] [varchar](50) NOT NULL,
    [lastName] [varchar](50) NOT NULL,
    [country] [varchar](50) NOT NULL,
    [email] [varchar](50) NOT NULL,
    [img] [varchar](300) NOT NULL
);

CREATE TABLE [dbo].[users_favorites](
    [user_id] [int] NOT NULL,
    [recipe_id] [int] NOT NULL,
    PRIMARY KEY (user_id,recipe_id),
    FOREIGN KEY (user_id) REFERENCES [dbo].[users](id)
);

CREATE TABLE [dbo].[users_watched](
    [user_id] [int] NOT NULL,
    [recipe_id] [int] NOT NULL,
    [watchTime] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id,recipe_id),
    FOREIGN KEY (user_id) REFERENCES [dbo].[users](id)
);

---------------------------------- Recipes ----------------------------------
CREATE TABLE [dbo].[my_recipes](
    [user_id] [int] NOT NULL,
    [recipe_id] [int] IDENTITY(1,1) NOT NULL,
    [name] [varchar](300) NOT NULL,
    [image] [varchar](300) NOT NULL,
    [readyInMinutes] [varchar](50) NOT NULL,
    [vegetarian] [bit] NOT NULL,
    [vegan] [bit] NOT NULL,
    [glutenFree] [bit] NOT NULL,
    [servings] [int] NOT NULL,
    [instructions] [varchar](5000) NOT NULL,
    [ingredients] [varchar](5000) NOT NULL


    PRIMARY KEY (user_id,recipe_id),
    FOREIGN KEY (user_id) REFERENCES [dbo].[users](id)
);


CREATE TABLE [dbo].[family_recipes](
    [user_id] [int] NOT NULL,
    [recipe_id] [int] IDENTITY(1,1) NOT NULL,
    [author] [text] NOT NULL,
    [whenUsed] [text] NOT NULL,
    [name] [text] NOT NULL,
    [image] [text] NOT NULL,
    [readyInMinutes] [int] NOT NULL,
    [vegetarian] [bit] NOT NULL,
    [vegan] [bit] NOT NULL,
    [glutenFree] [bit] NOT NULL,
    [servings] [int] NOT NULL,
    [instructions] [varchar](5000) NOT NULL,
    [ingredients] [varchar](5000) NOT NULL

    PRIMARY KEY (user_id,recipe_id),
    FOREIGN KEY (user_id) REFERENCES [dbo].[users](id)
);