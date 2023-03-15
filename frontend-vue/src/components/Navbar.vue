<template>
  <!--<div id="navbar">-->
  <md-app-toolbar class="md-primary md-dense">
    <div class="md-toolbar-section-start">
      <md-tabs md-sync-route class="md-primary">
        <md-tab md-label="Avaleht" href="/"></md-tab>
        <md-tab md-label="Lisa retsept" to="/addrecipe"></md-tab>
        <md-tab md-label="Otsing" to="/search"></md-tab>
      </md-tabs>
    </div>
    <div class="md-toolbar-section-end">
      <md-menu md-direction="bottom-start">
        <md-button md-menu-trigger><md-icon md-src="/static/icons/baseline-account_circle-24px.svg"/>{{ user.username }}</md-button>
        <md-menu-content>
          <md-menu-item to="/user">Muuda parooli</md-menu-item>
          <md-menu-item v-if="user && user.isAdmin" to="/admin">Halda kasutajaid</md-menu-item>
          <md-menu-item href="/logout">Logi välja</md-menu-item>
        </md-menu-content>
      </md-menu>
    </div>

  </md-app-toolbar>
  <!--</div>-->
</template>
<style scoped lang="scss">
  /*.md-toolbar {*/
    /*position: fixed;*/
  /*}*/
  /*#navbar{*/
    /*background-color:#009900;*/
    /*text-align: left;*/
    /*width: 100%;*/
    /*font-size: 0;*/
  /*}*/

  /*span {*/
    /*text-align: right;*/
    /*font-family: Verdana,Geneva,sans-serif;*/
    /*color: #ffffff;*/
    /*font-size: 20px;*/
    /*padding: 15px;*/
    /*position: absolute;*/
    /*right: 15px;*/
  /*}*/
  /*.logout {*/
    /*font-size:20px;*/
    /*padding: 0px;*/
  /*}*/

  /*.logout:hover {*/
    /*text-decoration: underline;*/
    /*background-color: inherit;*/
  /*}*/
  /*a {*/
    /*text-decoration: none;*/
    /*font-family: Verdana,Geneva,sans-serif;*/
    /*color: #ffffff;*/
    /*font-size: 30px;*/
    /*padding: 10px;*/
    /*display: inline-block;*/
  /*}*/

  /*a:hover {*/
    /*background-color:#005500;*/
  /*}*/

  /*.router-link-active {*/
    /*background-color:#007700;*/
  /*}*/
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
