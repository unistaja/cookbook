<template>
  <div v-if="hasRating()">
    <md-icon v-for="n in fullStarCount()" md-src="/static/icons/baseline-star-24px.svg" class="rating-star-style"></md-icon><md-icon v-for="n in halfStarCount()" md-src="/static/icons/baseline-star_half-24px.svg" class="rating-star-style"></md-icon><md-icon v-for="n in emptyStarCount()" md-src="/static/icons/baseline-star_border-24px.svg" class="rating-star-style"></md-icon>
    <md-tooltip md-direction="bottom">
      <span v-if="this.userRating">Minu hinnang: {{this.userRating}},</span>
      <span>Keskmine hinnang: {{this.avgRating}}</span>
    </md-tooltip>
  </div>
  <div v-else class="md-caption">
    Hinnang puudub
  </div>
</template>
<style scoped>
 .rating-star-style {
   display: inline-flex;
 }
</style>
<script>
  export default {
    name: "rating-display",
    props: ["userRating", "avgRating"],
    methods: {
      fullStarCount: function () {
        if (this.userRating) {
          return this.userRating;
        }
        if (this.avgRating) {
          return Math.floor(this.avgRating);
        }
        return 0;
      },
      halfStarCount: function () {
        if (this.userRating) {
          return 0;
        }
        if (this.avgRating) {
          return Math.ceil(this.avgRating) - Math.floor(this.avgRating);
        }
        return 0;
      },
      emptyStarCount: function () {
        return 5 - this.fullStarCount() - this.halfStarCount();
      },
      hasRating: function () {
        return this.userRating || this.avgRating;
      }

    }
  };

</script>
