<template>
  <div class="container">
    <h1 class="title">Search Page</h1>

    <div style="display: flex;">
      <!-- Search area -->
      <b-form-input
        id="search"
        v-model="searchContent"
        placeholder="What's your desire?"
        type="search"
        list="search-options-list"
        style="width: 50%; padding: 5px;"
      ></b-form-input>
      <select
        v-model="cuisinesInput"
        name="cuisines"
        id="cuisines"
        style="margin-left: 5px; width: 14%;"
      >
        <option disabled selected value>Select Cuisine</option>
        <option v-for="(cuisine, index) in cuisines" :value="cuisine" :key="index">{{cuisine}}</option>
      </select>
      <select v-model="dietsInput" name="diets" id="diets" style="margin-left: 5px; width: 14%;">
        <option disabled selected value>Select Diet</option>
        <option v-for="(diet, index) in diets" :value="diet" :key="index">{{diet}}</option>
      </select>
      <select
        v-model="intolerancesInput"
        name="intolerances"
        id="intolerances"
        style="margin-left: 5px; width: 14%;"
      >
        <option disabled selected value>Select Intolerance</option>
        <option
          v-for="(intolerance, index) in intolerances"
          :value="intolerance"
          :key="index"
        >{{intolerance}}</option>
      </select>

      <button
        type="button"
        class="btn btn-primary"
        style="margin-left: 5px;"
        @click="search()"
      >Search</button>
    </div>
    <div v-if="this.lastSearchTerm">
      Your last search was: {{this.lastSearchTerm}}
    </div>

    <RecipePreviewList :numberInColumn="3" :recipes="recipes"></RecipePreviewList>

    <!-- <div v-if="this.recipes.length!==0"> -->
    <!-- Show Recipes Returned -->
    <!-- <b-row v-for="(recipesGroup, indexGroup) in this.recipes" :key="indexGroup">
        <b-col v-for="(r, index) in recipesGroup" :key="index" >
          <RecipePreview class="recipePreview" :recipe="r" />
        </b-col>
      </b-row>
    </div>-->

    <!-- <recipe-preview class="recipePreview" :recipe="this.recipes[0][0]" /> -->

    <!-- for dynamic recipes from server -->
    <!-- <datalist id="search-options-list">
      <option v-for="(size, index) in sizes" :key="index" >{{ size }}</option>
    </datalist>-->
  </div>
</template>

<script>
import RecipePreviewList from "../components/RecipePreviewList";
export default {
  components: {
    RecipePreviewList
  },

  data() {
    return {
      cuisines: [
        "African",
        "American",
        "British",
        "Cajun",
        "Caribbean",
        "Chinese",
        "Eastern European",
        "European",
        "French",
        "German",
        "Greek",
        "Indian",
        "Irish",
        "Italian",
        "Japanese",
        "Jewish",
        "Korean",
        "Latin American",
        "Mediterranean",
        "Mexican",
        "Middle Eastern",
        "Nordic",
        "Southern",
        "Spanish",
        "Thai",
        "Vietnamese"
      ],
      diets: [
        "Gluten Free",
        "Ketogenic",
        "Vegetarian",
        "Lacto-Vegetarian",
        "Ovo-Vegetarian",
        "Vegan",
        "Pescetarian",
        "Paleo",
        "Primal",
        "Whole30"
      ],
      intolerances: [
        "Dairy",
        "Egg",
        "Gluten",
        "Grain",
        "Peanut",
        "Seafood",
        "Sesame",
        "Shellfish",
        "Soy",
        "Sulfite",
        "Tree Nut",
        "Wheat"
      ],
      searchContent: "",
      cuisinesInput: "",
      dietsInput: "",
      intolerancesInput: "",
      recipes: [],
      lastSearchTerm: ""
    };
  },
  mounted() {
    this.lastSearchTerm = localStorage.getItem("lastSearchTerm");
  },
  methods: {
    update(prefix) {},
    async search() {
      // console.log(this.searchContent);
      // console.log(this.cuisinesInput);
      // console.log(this.dietsInput);
      // console.log(this.intolerancesInput);
      // console.log(
      //   this.$root.store.base_url +
      //     "/recipes/search/query/" +
      //     this.searchContent +
      //     "/amount/5"
      // );

      //-------------------------- API Limits Fix --------------------------
      // const searchResponse = await this.axios.get(
      //   this.$root.store.base_url +
      //     "/recipes/search/query/" +
      //     this.searchContent +
      //     "/amount/5",

      // );
      // returnedRecipes = searchResponse.data;
      //---------------------------------------------------------------------
      // console.log(searchResponse);

      localStorage.setItem("lastSearchTerm", this.searchContent);
      this.lastSearchTerm = this.searchContent;

      let returnedRecipes = [
        {
          recipeId: 492564,
          name: "Falafel Burgers with Feta Cucumber Sauce",
          image: "https://spoonacular.com/recipeImages/492564-556x370.jpg",
          readyInMinutes: 50,
          likes: 6395,
          vegetarian: false,
          vegan: false,
          glutenFree: false
        },
        {
          recipeId: 246916,
          name: "Bison Burger",
          image: "https://spoonacular.com/recipeImages/246916-556x370.jpg",
          readyInMinutes: 45,
          likes: 5345,
          vegetarian: false,
          vegan: false,
          glutenFree: true
        },
        {
          recipeId: 245166,
          name: "Hawaiian Pork Burger",
          image: "https://spoonacular.com/recipeImages/245166-556x370.jpg",
          readyInMinutes: 40,
          likes: 1158,
          vegetarian: false,
          vegan: false,
          glutenFree: false
        },
        {
          recipeId: 246009,
          name: "Blue Cheese Burgers",
          image: "https://spoonacular.com/recipeImages/246009-556x370.jpg",
          readyInMinutes: 55,
          likes: 1019,
          vegetarian: false,
          vegan: false,
          glutenFree: true
        },
        {
          recipeId: 219957,
          name: "Carrot & sesame burgers",
          image: "https://spoonacular.com/recipeImages/219957-556x370.jpg",
          readyInMinutes: 50,
          likes: 910,
          vegetarian: false,
          vegan: false,
          glutenFree: false
        }
      ];
      this.recipes = [];
      this.recipes.push(...returnedRecipes);
    }
  }
};
</script>

<style>
</style>