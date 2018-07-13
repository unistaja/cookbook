// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import VueRouter from "vue-router";
import App from "./App";

Vue.use(VueRouter);

const routes = [
  {path: "/addrecipe", component: resolve => require(["./components/AddRecipe.vue"], resolve)},
  {path: "/editrecipe", component: resolve => require(["./components/AddRecipe.vue"], resolve)},
  {path: "/recipe/:id", component: resolve => require(["./components/ViewRecipe.vue"], resolve)},
  {path: "/user", component: resolve => require(["./components/UserPage.vue"], resolve)},
  {path: "/search", component: resolve => require(["./components/SearchPage.vue"], resolve)}
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
