import Main from "./pages/MainPage";
import NotFound from "./pages/NotFoundPage";

const routes = [
  {
    path: "/",
    name: "main",
    component: Main,
  },
  {
    path: "/register",
    name: "register",
    component: () => import("./pages/RegisterPage"),
  },
  {
    path: "/login",
    name: "login",
    component: () => import("./pages/LoginPage"),
  },
  {
    path: "/logout",
    name: "logout",
    component: () => import("./pages/LogoutPage"),
  },
  {
    path: "/search",
    name: "search",
    component: () => import("./pages/SearchPage"),
  },
  {
    path: "/recipe/:recipeId",
    name: "recipe",
    component: () => import("./pages/RecipeViewPage"),
  },
  {
    path: "about",
    name: "about",
    component: () => import("./pages/AboutPage"),
  },
  {
    path: "myFamilyRecipes",
    name: "myFamilyRecipes",
    component: () => import("./pages/MyFamilyRecipes"),
  },
  {
    path: "myRecipes",
    name: "myRecipes",
    component: () => import("./pages/MyRecipes"),
  },
  {
    path: "myFavoriteRecipes",
    name: "myFavoriteRecipes",
    component: () => import("./pages/MyFavoriteRecipes"),
  },
  {
    path: "*",
    name: "notFound",
    component: NotFound,
  },
];

export default routes;
