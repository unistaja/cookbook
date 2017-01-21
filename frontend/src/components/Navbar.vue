<template>
  <div id="navbar">

    <router-link to="/recipelist">Retseptid</router-link>
    <router-link to="/addrecipe">Lisa retsept</router-link>
    <span>
      Tere, {{ user.username }}! (<a href="/logout" class="logout">Logi välja</a>)
    </span>
  </div>
</template>
<style scoped>
  #navbar{
    background-color:#009900;
    text-align: left;
    width: 100%;
    font-size: 0;
  }

  span {
    text-align: right;
    font-family: Verdana,Geneva,sans-serif;
    color: #ffffff;
    font-size: 20px;
    padding: 15px;
    position: absolute;
    right: 15px;
  }
  .logout {
    font-size:20px;
    padding: 0px;
  }

  .logout:hover {
    text-decoration: underline;
    background-color: inherit;
  }
  a {
    text-decoration: none;
    font-family: Verdana,Geneva,sans-serif;
    color: #ffffff;
    font-size: 30px;
    padding: 10px;
    display: inline-block;
  }

  a:hover {
    background-color:#005500;
  }

  .router-link-active {
    background-color:#007700;
  }
</style>
<script>
  import { store } from "../datastore.js";
  export default{
    name: "navbar",
    data: function () {
      console.log(store);
      return store;
    }
  };
</script>
