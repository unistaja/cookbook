<template>
    <md-app md-mode="fixed">

      <navbar slot="md-app-toolbar"></navbar>
      <md-app-content>
        <router-view></router-view>
      </md-app-content>
    </md-app>
</template>

<script>
import Navbar from "./components/Navbar";

export default {
  name: "app",
  components: {
    Navbar
  }
};
</script>


<style lang="scss">
  @import "~vue-material/dist/theme/engine";
  @include md-register-theme("default", (
    primary: md-get-palette-color(green, 500),
    accent: md-get-palette-color(lightgreen, A100)
  ));
  @import "~vue-material/dist/theme/all";

  .md-app {
    max-height: 100vh;
    min-height: 100vh;
  }
</style>

