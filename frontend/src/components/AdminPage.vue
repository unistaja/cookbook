<template>
    <div id="adminpage">
        <div id="createUser">
            <div v-if="message" :class="[error ? 'error' : 'success']">{{ message }}</div>
            <h2>Loo kasutaja: </h2>
            <input type=text v-model="username" placeholder="Kasutajanimi"/><br/>
            <input type=password v-model="password" @keyup.enter="savePassword()" placeholder="Parool"/>
            <div id="savebutton">
                <a id="save" @click="addUser()">Loo kasutaja</a>
            </div>
        </div>
    </div>
</template>
<style>
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
  import { addUser } from "../api.js";

  export default{
    name: "adminpage",
    data: function () {
      return {
        username: null,
        password: null,
        message: null,
        error: false
      };
    },
    methods: {
      addUser: function () {
        if (this.username === null || this.username.length === 0) {
          this.error = true;
          this.message = "Palun sisesta kasutajanimi.";
          return;
        }
        if (this.password === null || this.password.length === 0) {
          this.error = true;
          this.message = "Palun sisesta parool.";
          return;
        }
        addUser(this.username, this.password, (err) => {
          if (err) {
            this.error = true;
            this.message = err;
          } else {
            this.error = false;
            this.message = "Kasutaja salvestatud!";
          }
        });
      }
    }
  };
</script>
