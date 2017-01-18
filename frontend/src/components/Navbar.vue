<template>
  <div id="navbar">

    <router-link to="/recipelist">Retseptid</router-link>
    <router-link to="/addrecipe">Lisa retsept</router-link>
  </div>
</template>
<style scoped>
  #navbar{
    background-color:#009900;
    text-align: left;
    width: 100%;
    font-size: 0;
  }
  a {
    text-decoration: none;
    font-family: Verdana,Geneva,sans-serif;
    color: #ffffff;
    font-size: 5vh;
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
  export default{
    name: 'navbar'
  }
</script>
