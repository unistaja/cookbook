import Rating from "@mui/material/Rating";
import Tooltip from "@mui/material/Tooltip";
import Alert from '@mui/material/Alert';
import CheckIcon from '@mui/icons-material/Check';
import { saveRating } from "../api";
import { useState } from "react";

const ratingLabels = [
  'Ei taha enam kunagi süüa',
  'Kui on võimalik midagi muud süüa, siis seda ei sööks',
  'Kõlbab süüa, aga väga tihti ei tahaks',
  'Üsna hea, päris iga päev ei sööks',
  'Väga hea, sööks kasvõi iga päev'
];

export default function EditableRating({value, onChange, recipeId, sx}) {
  const [hoverValue, setHoverValue] = useState(-1);
  const [ratingSaved, setRatingSaved] = useState(false);

  async function handleRatingClick(rating) {
    const actualRating = rating ?? Math.round(value);
    await saveRating(actualRating, recipeId);
    setRatingSaved(true);
    setTimeout(() => setRatingSaved(false), 2000);
    onChange(actualRating);
  }

  const ratingLabelIndex = hoverValue > 0 ? hoverValue - 1 : value - 1;

  return (
    <>
    <Tooltip title={ratingLabels[ratingLabelIndex]}>
      <Rating value={value} onChange={(event, newValue) => handleRatingClick(newValue)} onChangeActive={(event, newHover) => setHoverValue(newHover)} sx={sx}/>
    </Tooltip>
    {ratingSaved && (
      <Alert icon={<CheckIcon fontSize="inherit" />} severity="success" align="center">
        Hinnang salvestatud
      </Alert>
    )}
    </>
  );

}