var recipe=require("../../src/datastore.js");
describe ("appi", function() {
  var test=recipe.getNewRecipe();
  it("oeh", function(){
    expect(test.name).toBe(null);
  });
});
