<template>
  <b-container>
    <h3>
      <span v-if="title">{{ title }}:</span>
      <slot></slot>
    </h3>
    <b-row :cols="numberInColumn">
      <b-col v-for="r in recipes" :key="r.id">
        <RecipePreview class="recipePreview" :recipe="r" />
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import RecipePreview from "./RecipePreview.vue";
export default {
  name: "RecipePreviewList",
  components: {
    RecipePreview
  },
  props: {
    title: {
      type: String,
      required: false
    },
    numberInColumn: {
      type: Number,
      required: true
    },
    recipes: {
      type: Array,
      required: true
    }
  },
  
};
</script>

<style lang="scss" scoped>
.container {
  min-height: 400px;
}
</style>
