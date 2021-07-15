<template>
  <div class="container">
    <div v-if="recipe">
      <div class="recipe-header mt-3 mb-4">
        <div>
          <h1>{{ recipe.name }}</h1>
        </div>
        <img :src="recipe.image" class="center" />
        <div style="text-align: center;" class="mb-3">
          <div style="display: inline-block; text-align: left;">
            <div style="padding:3px;">
              <span>
                <img height="30px" width="30px" src="../pictures/servings.png" />
              </span>
              <span>Number of Servings: {{ recipe.servings }} </span>
            </div>
            <div style="padding:3px;">
              <span>
                <img height="30px" width="30px" src="../pictures/clock.png" />
              </span>
              <span>Ready in {{ recipe.readyInMinutes }} minutes</span>
            </div>
            <div style="padding:3px;">
              <span>
                <img height="30px" width="30px" src="../pictures/like.png" />
              </span>
              <span>{{ recipe.likes }} likes</span>
            </div>
            <div style="padding:3px;" v-if="recipe.vegetarian">
              <!-- Vegetarian = True -->
              <span>
                <img height="30px" width="30px" src="../pictures/vegetarian.png" />
              </span>
              <span>Vegetarian</span>
            </div>
            <div style="padding:3px;" v-if="recipe.vegan">
              <!-- Vegan = True -->
              <span>
                <img height="30px" width="30px" src="../pictures/vegan.png" />
              </span>
              <span>Vegan</span>
            </div>
            <div style="padding:3px;" v-if="recipe.glutenFree">
              <!-- GlutenFree = True -->
              <span>
                <img height="30px" width="30px" src="../pictures/glutenFree.png" />
              </span>
              <span>GlutenFree</span>
            </div>
          </div>
        </div>
      </div>

      <div class="recipe-body">
        <div class="card border-dark bg-light mb-3">
          <div class="card-header">Ingredients:</div>
          <div style="overflow: hidden;">
            <div
              v-for="(i, index) in recipe.ingredients"
              :key="index"
              style="float: left; display: flex; box-sizing: border-box; text-align: center;"
            >
              <div style="width: 100%; height: 100%; margin: 10px; padding:10px;">
                <div style="display: block;">{{i.name}}</div>
                <div stlye="vertical-align: center; position:relative;">
                  <img
                    style="height: 100px; width: 90px;"
                    :src="$root.store.img_base_url + i.image"
                  />
                </div>
                <div>{{Math.round((i.amount + Number.EPSILON) * 100)/100 + " " + i.unit}}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="card border-dark bg-light mb-3">
          <div class="card-header">Instructions:</div>
          <div>
            <ol>
              <li v-for="s in recipe.instructions" :key="s.number">{{ s }}</li>
            </ol>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      recipe: null
    };
  },
  async created() {
    try {
      let response;
      // response = this.$route.params.response;

      // ------------------- Because of API Limits -------------------
      // try {
      //   response = await this.axios.get(
      //     this.$root.store.base_url +
      //       "/recipes/recipe/" +
      //       this.$route.params.recipeId
      //   );
      //   // console.log("response.status", response.status);
      //   if (response.status !== 200) this.$router.replace("/NotFound");
      // } catch (error) {
      //   console.log(error);
      //   console.log("error.response.status", error.response.status);
      //   this.$router.replace("/NotFound");
      //   return;
      // }
      //---------------------------------------------------------------

      // ------------------------ only for not over using API ------------------------
      response = {};
      response.data = {
        recipeId: 655314,
        name: "Peanut butter ice cream",
        image: "https://spoonacular.com/recipeImages/655314-556x370.jpg",
        readyInMinutes: 45,
        likes: 24,
        vegetarian: true,
        vegan: false,
        glutenFree: true,
        servings: 8,
        ingredients: [
          {
            name: "milk",
            unit: "cups",
            amount: 1.25,
            image: "milk.png"
          },
          {
            name: "sugar",
            unit: "cup",
            amount: 0.75,
            image: "sugar-in-bowl.png"
          },
          {
            name: "salt",
            unit: "tsp",
            amount: 0.125,
            image: "salt.jpg"
          },
          {
            name: "salt",
            unit: "tsp",
            amount: 0.125,
            image: "salt.jpg"
          },
          {
            name: "salt",
            unit: "tsp",
            amount: 0.125,
            image: "salt.jpg"
          },
          {
            name: "eggs",
            unit: "",
            amount: 3,
            image: "egg.png"
          },
          {
            name: "smooth peanut butter",
            unit: "cup",
            amount: 0.3333333333333333,
            image: "peanut-butter.png"
          },
          {
            name: "smooth peanut butter",
            unit: "cup",
            amount: 0.3333333333333333,
            image: "peanut-butter.png"
          },
          {
            name: "smooth peanut butter",
            unit: "cup",
            amount: 0.3333333333333333,
            image: "peanut-butter.png"
          },
          {
            name: "smooth peanut butter",
            unit: "cup",
            amount: 0.3333333333333333,
            image: "peanut-butter.png"
          },
          {
            name: "heavy cream",
            unit: "cups",
            amount: 1.5,
            image: "fluid-cream.jpg"
          },
          {
            name: "vanilla extract",
            unit: "Tbs",
            amount: 1,
            image: "vanilla-extract.jpg"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "unsalted peanuts",
            unit: "Tbs",
            amount: 4,
            image: "peanuts.png"
          },
          {
            name: "bitter chocolate",
            unit: "serving",
            amount: 1,
            image: "baking-chocolate.jpg"
          }
        ],
        steps: [
          {
            step_number: 1,
            step_content:
              "Combine milk, sugar and salt in a saucepan; cook over medium heat, stirring occasionally until mixture almost boils. Reduce heat to low."
          },
          {
            step_number: 2,
            step_content:
              "Beat the eggs with a hand mixer or a whisk for 2 minutes until frothy."
          },
          {
            step_number: 3,
            step_content:
              "Gradually stir about  cup of hot milk mixture into beaten eggs."
          },
          {
            step_number: 4,
            step_content: "Add eggs to remaining milk mixture."
          },
          {
            step_number: 5,
            step_content:
              "Cook over low heat, stirring constantly until slightly thickened, about 2-3 minutes."
          },
          {
            step_number: 6,
            step_content: "Remove from heat"
          },
          {
            step_number: 7,
            step_content: "Whisk in peanut butter and mix well."
          },
          {
            step_number: 8,
            step_content: "Refrigerate at least 3 hours."
          },
          {
            step_number: 9,
            step_content:
              "Combine cream, vanilla extract, and chilled mixture, stirring with a whisk."
          },
          {
            step_number: 10,
            step_content:
              "Pour the mixture into an ice cream machine and churn until frozen."
          },
          {
            step_number: 11,
            step_content:
              "Transfer to plastic container and place in the freezer for an hour before serving."
          },
          {
            step_number: 12,
            step_content:
              "Serving: take it out of the freezer for 5-10 minutes before serving, or longer, so it comes to the right scooping temperature. Sprinkle with chopped peanuts and chocolate bits."
          },
          {
            step_number: 13,
            step_content:
              "Easy suggestion: ice cream is best served in chilled glass or porcelain bowls. Scoop it with a hot, but dry, ice-cream spoon (soak it in boiling water, then wipe dry)."
          }
        ]
      };

      // ------------------------------------------------------------------------------------------------

      // console.log(response.data);

      let {
        steps,
        ingredients,
        servings,
        vegetarian,
        vegan,
        glutenFree,
        likes,
        readyInMinutes,
        image,
        name
      } = response.data;

      let instructions = steps.map(fstep => {
        // fstep.steps[0].step = fstep.name + fstep.steps[0].step;
        fstep = fstep.step_content;
        return fstep;
      });
      // .reduce((a, b) => [...a, ...b], []);

      let _recipe = {
        instructions,
        ingredients,
        servings,
        vegetarian,
        vegan,
        glutenFree,
        likes,
        readyInMinutes,
        image,
        name
      };
      // console.log(_recipe);
      this.recipe = _recipe;
    } catch (error) {
      console.log(error);
    }
  }
};
</script>

<style scoped>
/* .wrapper {
  display: flex;
}
.wrapped {
  width: 50%;
} */
.center {
  display: block;
  margin-left: auto;
  margin-right: auto;
  width: 50%;
}
.recipe-header {
  text-align: center;
}
</style>
