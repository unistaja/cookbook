package ee.cookbook.service;

import ee.cookbook.dao.RecipeViewHistoryRepository;
import ee.cookbook.model.RecipeViewHistory;
import ee.cookbook.model.ViewedRecipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ViewHistoryService {
    private static final Integer MAX_HISTORY = 5;

    @Autowired
    RecipeViewHistoryRepository historyRepository;

    @Autowired
    private JdbcTemplate template;

    public void addRecipeView(Long userId, Long recipeId) {
        List<RecipeViewHistory> existingHistory = historyRepository.findAllByUserIdOrderByViewTimeAsc(userId);
        var existingViewOptional = existingHistory.stream().filter(item -> item.recipeId.equals(recipeId)).findFirst();
        if (existingViewOptional.isPresent()) {
            var existingView = existingViewOptional.get();
            existingView.viewTime = new Date();
            historyRepository.save(existingView);
        } else {
            if (existingHistory.size() >= MAX_HISTORY) {
                var oldestItems = existingHistory.subList(0, existingHistory.size() - MAX_HISTORY + 1);
                historyRepository.deleteAllInBatch(oldestItems);
            }
            var newViwHistory = new RecipeViewHistory();
            newViwHistory.userId = userId;
            newViwHistory.recipeId = recipeId;
            newViwHistory.viewTime = new Date();
            historyRepository.save(newViwHistory);
        }
    }

    public List<ViewedRecipe> getViewedRecipes(Long userId) {
        RowMapper<ViewedRecipe> mapper = (resultSet, index) -> {
            ViewedRecipe result = new ViewedRecipe();
            result.name = resultSet.getString("name");
            result.recipeId = resultSet.getLong("recipeId");
            result.pictureName = resultSet.getString("pictureName");
            return result;
        };
        return template.query(
                "SELECT r.id as recipeId, r.name, r.pictureName FROM RecipeViewHistory h JOIN Recipe r ON h.recipeId = r.id WHERE h.userId = ? ORDER BY h.viewTime DESC",
                mapper,
                userId
        );
    }
}
