<template>
  <b-container class="container">
    <h1 class="title text-center">Main Page</h1>
    <b-row>
      <b-col>
        <RandomRecipePreviewList class="RandomRecipes center" />
      </b-col>
      <b-col v-if="!$root.store.username">
        <login-page>
        </login-page>
      </b-col>
      <b-col v-else>
        <lastWatchedRecipesPreviewList class="LastWatchedRecipes center" />
      </b-col>
    </b-row>
    <!-- if the user is connected show him the login component. if not show him this next component-->

    <!-- <div
      style="position: absolute;top: 70%;left: 50%;transform: translate(-50%, -50%);"
    >
      Centeredasdasdad
    </div>-->
  </b-container>
</template>

<script>
import RandomRecipePreviewList from "../components/RandomRecipePreviewList";
import LastWatchedRecipesPreviewList from "../components/LastWatchedRecipesPreviewList";
import LoginPage from "../pages/LoginPage";

export default {
  components: {
    RandomRecipePreviewList,
    LastWatchedRecipesPreviewList,
    LoginPage
  }
};
</script>

<style lang="scss" scoped>
.RandomRecipes {
  margin: 10px 0 10px;
}
.LastWatchedRecipes {
  margin: 10px 0 10px;
}
.blur {
  -webkit-filter: blur(5px); /* Safari 6.0 - 9.0 */
  filter: blur(2px);
}
::v-deep .blur .recipe-preview {
  pointer-events: none;
  cursor: default;
}
</style>

