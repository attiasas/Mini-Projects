<template>
  <div class="container">
    <div class="recipe-header mt-3 mb-4">
      <h1 >About The Site</h1>
    </div>
    <div class="card border-dark bg-light mb-3 ">
          <div class="card-header recipe-header">Site Information</div>
          <div>
            <h4 class="center">This Recipes Site Project was created as part of a 'Web Development Environments' course and was seperated to three assignments:</h4>
            <ol>
              <li>Creating API (in YMAL using the swaggerhub site) to establish communication between the Server and Clients</li>
              <li>Creating the Server, using NODE js with 'express' (routing),'client-sessions' (cookies),'mssql'(DB communication),'axios' (using spoonacular API for recipes information) libs</li>
              <li>Creating the client side using Vue (components,routers,vue-cli,bootstrap,vuelidate)</li>
            </ol>
            <h5 class="center">Authors: Assaf Attias, Amir Gabay</h5>
          </div>
          <div class="card-header recipe-header">Project Links</div>
          <div>
            <ul class="center">
              <li>API (Ver 1.0.2) - <a href="https://app.swaggerhub.com/apis-docs/attiasas93/Project/1.0.2">Assignment 3.1</a></li>
              <li>Server Side Git - <a href="https://github.com/SISE-Web-Development-Environments/assignment-3-2-assaf_amir_ass3">Assignment 3.2</a></li>
              <li>Client Size Git - <a href="https://github.com/SISE-Web-Development-Environments/assignment-3-3-assaf_amir_ass3">Assignment 3.3</a></li>
            </ul>
          </div>
        </div>
  </div>
</template>

<script>
export default {

}
</script>

<style>
.center {
  display: block;
  margin-left: auto;
  margin-top: 2%;
  margin-right: auto;
  width: 90%;
}
.recipe-header {
  text-align: center;
}
</style>