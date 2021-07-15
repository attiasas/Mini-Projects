<template>
  <RecipePreviewList title="Last Watched Recipes" :numberInColumn="1" :recipes="recipes"></RecipePreviewList>
</template>

<script>
import RecipePreviewList from "./RecipePreviewList";

export default {
  components: {
    RecipePreviewList
  },
  data() {
    return {
      recipes: []
    };
  },
  mounted() {
    this.updateRecipes();
  },
  methods: {
    async updateRecipes() {
      try {
        // ------------------- Because of API limits - we will activate it after testing -------------------

        // const response = await this.axios.get(
        //   this.$root.store.base_url + "/users/lastwatch",
        //   {params: {numberOfResults: 3}}
        // );
        
        // const watchedIds = response.data;

        // // const watchedIds = ["633067", "633067"];

        // let recipes = [];
        // watchedIds.map(async (id) => {
        //   const recipe = await this.axios.get(this.$root.store.base_url + "/recipes/recipe/"+id+"/preview");
        //   recipes.push(recipe.data);
        //   console.log(recipe);
        // });

        // this.recipes = [];
        // this.recipes = recipes;
        


        //------------------- Because of API limits -------------------

        const recipes = [
          {
            recipeId: 633067,
            name: "Authentic Biscochitos",
            image: "https://spoonacular.com/recipeImages/633067-556x370.jpg",
            readyInMinutes: 45,
            likes: 11,
            vegetarian: true,
            vegan: false,
            glutenFree: false
          },
          {
            recipeId: 661340,
            name: "Spinach Salad with Strawberry Vinaigrette",
            image: "https://spoonacular.com/recipeImages/661340-556x370.jpg",
            readyInMinutes: 45,
            likes: 15,
            vegetarian: false,
            vegan: false,
            glutenFree: true
          },
          {
            recipeId: 641223,
            name: "Damn Good Corn Pudding ~from 12 Bones Smokehouse",
            image: "https://spoonacular.com/recipeImages/641223-556x370.jpg",
            readyInMinutes: 45,
            likes: 19,
            vegetarian: false,
            vegan: false,
            glutenFree: false
          }
        ];
        this.recipes = recipes;

        // ----------------------------------------------------------

        // console.log(recipes);
        // this.recipes = [];
        // this.recipes.push(...recipes);
        // console.log(this.recipes);
      } catch (error) {
        console.log(error);
      }
    }
  }
};
</script>

<style>
</style>