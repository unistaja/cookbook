export default function RecipeImage({imgName, recipeId, size}) {
  const imgFolder = recipeId ?? 'temp';
  const imgNamePrefix = recipeId ? 'RecipePicture.' : '';
  let sizePrefix = 2;
  if (size === "medium") {
    sizePrefix = 1;
  }
  if (size === "large") {
    sizePrefix = 3;
  }

  return (
    <img src={`images/${imgFolder}/${sizePrefix}${imgNamePrefix}${imgName}`} alt=""/>
  );
}