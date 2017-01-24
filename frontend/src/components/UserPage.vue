<template>
  <div id="userpage">
    <div id="changepass">
      <div v-if="message" :class="[error ? 'error' : 'success']">{{ message }}</div>
      <h2>Muuda parool: </h2>
      <input type=password v-model="oldPassword" placeholder="Vana parool"/><br/>
      <input type=password v-model="newPassword" placeholder="Uus parool"/>
      <div id="savebutton">
        <a id="save" @click="savePassword()">Salvesta muudatused</a>
      </div>
    </div>


  </div>
</template>
<style scoped>
  a {
    display: inline-block;
  }
  input {
    margin: 5px;
  }
  #save {
    padding: 10px 15px;
    text-decoration: none;
    background: #4479BA;
    color: #FFF;
    border-radius: 4px;
    border: solid 1px #20538D;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.2);
    transition-duration: 0.2s;
    user-select:none;
    margin: 10px;
    margin-top: 5px;
    cursor: pointer;
  }
  #save:hover {
    background: #356094;
    border: solid 1px #2A4E77;
    text-decoration: none;
  }
  #save:active {
      box-shadow: inset 0 1px 4px rgba(0, 0, 0, 0.6);
      background: #2E5481;
      border: solid 1px #203E5F;
  }

  .error {
    color:red;
  }

  .success {
    color:green;
  }


</style>
<script>
  import { changePassword } from "../api.js";

  export default{
    name: "userpage",
    data: function () {
      return {
        oldPassword: null,
        newPassword: null,
        message: null,
        error: false
      };
    },
    methods: {
      savePassword: function () {
        changePassword(this.oldPassword, this.newPassword, (err) => {
          if (err) {
            this.error = true;
            this.message = err;
          } else {
            this.error = false;
            this.message = "Parool edukalt muudetud!";
          }
      });
      }
    }
  };
</script>
