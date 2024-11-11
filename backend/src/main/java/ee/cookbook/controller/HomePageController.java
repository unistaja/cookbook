package ee.cookbook.controller;

import ee.cookbook.model.User;
import ee.cookbook.model.ViewedRecipe;
import ee.cookbook.service.ViewHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/widgets")
public class HomePageController {
    @Autowired
    private ViewHistoryService viewHistoryService;

    @RequestMapping(value = "/viewedRecipes", method = RequestMethod.GET)
    public List<ViewedRecipe> getViewedRecipes(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return viewHistoryService.getViewedRecipes(user.id);
    }
}
