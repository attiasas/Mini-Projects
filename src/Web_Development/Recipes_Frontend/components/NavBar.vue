<template>
  <b-navbar type="dark" variant="info">
    <router-link tag="b-navbar-brand" :to="{ name: 'main' }">Vue Recipes</router-link>|
    <b-navbar-nav>
      <router-link tag="b-nav-item" :to="{ name: 'search' }">Search</router-link>
      <router-link tag="b-nav-item" :to="{ name: 'about' }">About</router-link>
    </b-navbar-nav>

    <!-- Right aligned items -->
    <b-navbar-nav class="ml-auto">
      <b-nav-item-dropdown text="Hello Guest" v-if="!$root.store.username" right>
        <router-link tag="b-dropdown-item" :to="{ name: 'login' }">Login</router-link>
        <router-link tag="b-dropdown-item" :to="{ name: 'register' }">Register</router-link>
      </b-nav-item-dropdown>
      <b-nav-item-dropdown :text="$root.store.username" v-else right>
        <router-link tag="b-dropdown-item" :to="{ name: 'myFavoriteRecipes' }">My Favorite Recipes</router-link>
        <router-link tag="b-dropdown-item" :to="{ name: 'MyRecipes' }">My Recipes</router-link>
        <router-link tag="b-dropdown-item" :to="{ name: 'myFamilyRecipes' }">My Family Recipes</router-link>
        <router-link tag="b-dropdown-item" :to="{ name: 'logout' }">Logout</router-link>
      </b-nav-item-dropdown>
    </b-navbar-nav>



    <!-- <router-link :to="{ name: 'search' }">Search</router-link>
    |
    {{ !$root.store.username }}
    <span v-if="!$root.store.username">
      Guest:
      <router-link :to="{ name: 'register' }">Register</router-link>|
      <router-link :to="{ name: 'login' }">Login</router-link>|
    </span>
    <span v-else>
      {{ $root.store.username }}:
      <button @click="Logout">Logout</button>|
    </span>-->
  </b-navbar>
</template>

<script>
export default {};
</script>

<style>
</style>