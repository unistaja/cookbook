import { useEffect, useState } from "react";
import { addRecipeSuggestionsConfiguration, getRecipeSuggestionsConfigurations } from "../api";
import RecipeSuggestionsSection from "./RecipeSuggestionSection";
import EditRecipeSuggestionConfigurationModal from "./EditRecipeSuggestionConfigurationModal";
import { Button } from "@mui/material";

export default function RecipeSuggestionsList({onAddRecipeToMenu}) {
  const [configurations, setConfigurations] = useState([]);
  const [isAddSectionOpen, setIsAddSectionOpen] = useState(false);

  async function handleAddSectionSubmit(newConfig) {
    const addedConfig = await addRecipeSuggestionsConfiguration(newConfig)
    setConfigurations([...configurations, addedConfig]); 
    setIsAddSectionOpen(false);
  }

  useEffect(() => {
    getRecipeSuggestionsConfigurations()
      .then(data => {
        setConfigurations(data);
      }).catch(error => {
        console.error("Error:", error);
      });
  }, [setConfigurations]);

  return (
    <>
      {configurations.map(config => (
        <RecipeSuggestionsSection key={config.id} suggestionConfiguration={config}  onAddRecipeToMenu = {onAddRecipeToMenu} />
      ))}
      <EditRecipeSuggestionConfigurationModal open={isAddSectionOpen} onClose={() => setIsAddSectionOpen(false)} onSubmit={handleAddSectionSubmit}/>
      <Button variant="contained" color="primary" onClick={() => setIsAddSectionOpen(true)} sx={{margin: "10px"}}>Lisa retseptisoovitaja</Button>
    </>
  );

}