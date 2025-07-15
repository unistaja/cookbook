import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import { Fragment } from "react";

export default function IngredientListSection({ ingredientLists, amountMultiplier = 1 }) {
  return (
    <Stack align="left">
      {ingredientLists.map((list, listIdx) => (
        <Fragment key={`list-${listIdx}`}>
          <Typography key={`list-${listIdx}`} variant="subtitle1" sx={{fontWeight: "bold"}}>{list.name}:</Typography>
          {list.ingredientLines.map((line, lineIdx) => (
            <Typography key={`list-${listIdx}-line-${lineIdx}`}>
              {calculateAmount(line.amount, amountMultiplier)} {line.unit} {line.ingredient} {line.alternateLines.length > 0 && "või "} {line.alternateLines.map((altLine) => `${calculateAmount(altLine.amount, amountMultiplier) ?? ''} ${altLine.unit ?? ''} ${altLine.ingredient}`).join(" või ")}
            </Typography>
          ))}
        </Fragment>
      ))}
      </Stack>
  )
}

function calculateAmount(amountString, multiplier) {
  if (!amountString) {
    return "";
  }
  if (multiplier === 1) {
    return amountString;
  }
  const numberParts = amountString.split("-");
  return numberParts.map((part) => {
    if (part.includes("/")) {
      const [numerator, denominator] = part.split("/");
      return `${parseFloat(numerator) * multiplier }/${denominator}`;
    }
    return +(parseFloat(part.replace(",", ".")) * multiplier).toFixed(3);

  }).join("-");
}