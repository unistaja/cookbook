package ee.cookbook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ee.cookbook.model.ParsedRemoteRecipe;
import ee.cookbook.service.RemoteRecipeService;

@RestController
@RequestMapping("/api/getRemoteRecipe")
public class RemoteRecipeParserController {
  @Autowired
  RemoteRecipeService remoteRecipeService;

  @RequestMapping(method = RequestMethod.POST)
  public ParsedRemoteRecipe getRemoteRecipe(@RequestParam String url) {
    return remoteRecipeService.parseRecipeFromUrl(url);
  }
  
}
