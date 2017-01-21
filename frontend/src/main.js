// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import VueRouter from "vue-router";
import App from "./App";

Vue.use(VueRouter);

const routes = [
  {path: "/recipelist", component: resolve => require(["./components/SearchRecipes.vue"], resolve)},
  {path: "/addrecipe", component: resolve => require(["./components/AddRecipe.vue"], resolve)},
  {path: "/editrecipe", component: resolve => require(["./components/AddRecipe.vue"], resolve)},
  {path: "/recipe/:id", component: resolve => require(["./components/ViewRecipe.vue"], resolve)}
];

const router = new VueRouter({
  routes: routes
});

/* eslint-disable no-new */
new Vue({
  el: "#app",
  template: "<App/>",
  components: { App },
  router: router
});
