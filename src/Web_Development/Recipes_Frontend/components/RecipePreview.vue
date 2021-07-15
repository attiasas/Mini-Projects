<template>
  <router-link
    :to="{ name: 'recipe', params: { recipeId: recipe.recipeId } }"
    class="recipe-preview"
  >
    <!-- <div class="recipe-body">
      <img v-if="image_load" :src="recipe.image" class="recipe-image" />
    </div>
    <div class="recipe-footer">
      <div :title="recipe.name" class="recipe-title">
        {{ recipe.name }}
      </div>
      <ul class="recipe-overview">
        <li>{{ recipe.readyInMinutes }} minutes</li>
        <li>{{ recipe.likes }} likes</li>
      </ul>
    </div>-->
    <b-card
      :title="recipe.name"
      :img-src="recipe.image"
      img-top
      tag="article"
      style="width: 100%; height:98%;"
      class="mb-2"
    >
      <b-card-text>
        <div style="display: inline-block; text-align: left;">
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
      </b-card-text>
    </b-card>
  </router-link>
</template>

<script>
export default {
  // mounted() {
  //   this.axios.get(this.recipe.image).then(i => {
  //     this.image_load = true;
  //   });
  // },
  data() {
    return {
      // image_load: false
    };
  },
  props: {
    recipe: {
      type: Object,
      required: true
    }

    // id: {
    //   type: Number,
    //   required: true
    // },
    // title: {
    //   type: String,
    //   required: true
    // },
    // readyInMinutes: {
    //   type: Number,
    //   required: true
    // },
    // image: {
    //   type: String,
    //   required: true
    // },
    // aggregateLikes: {
    //   type: Number,
    //   required: false,
    //   default() {
    //     return undefined;
    //   }
    // }
  }
};
</script>

<style scoped>
.recipe-preview {
  display: inline-block;
  width: 90%;
  height: 100%;
  position: relative;
  margin: 10px 10px;
}
.recipe-preview > .recipe-body {
  width: 100%;
  height: 200px;
  position: relative;
}

.recipe-preview .recipe-body .recipe-image {
  margin-left: auto;
  margin-right: auto;
  margin-top: auto;
  margin-bottom: auto;
  display: block;
  width: 98%;
  height: auto;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  background-size: cover;
}

.recipe-preview .recipe-footer {
  width: 100%;
  height: 50%;
  overflow: hidden;
}

.recipe-preview .recipe-footer .recipe-title {
  padding: 10px 10px;
  width: 100%;
  font-size: 12pt;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  -o-text-overflow: ellipsis;
  text-overflow: ellipsis;
}

.recipe-preview .recipe-footer ul.recipe-overview {
  padding: 5px 10px;
  width: 100%;
  display: -webkit-box;
  display: -moz-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-flex: 1;
  -moz-box-flex: 1;
  -o-box-flex: 1;
  box-flex: 1;
  -webkit-flex: 1 auto;
  -ms-flex: 1 auto;
  flex: 1 auto;
  table-layout: fixed;
  margin-bottom: 0px;
}

.recipe-preview .recipe-footer ul.recipe-overview li {
  -webkit-box-flex: 1;
  -moz-box-flex: 1;
  -o-box-flex: 1;
  -ms-box-flex: 1;
  box-flex: 1;
  -webkit-flex-grow: 1;
  flex-grow: 1;
  width: 90px;
  display: table-cell;
  text-align: center;
}
</style>
