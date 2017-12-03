package ee.cookbook;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.IOException;

@Component
class WebConfig extends WebMvcConfigurerAdapter {
  @Value("${imageFolder}")
  private String imageFolder;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    try {
      registry.addResourceHandler("images/**")
              .addResourceLocations("file:///" + new File(imageFolder).getCanonicalPath() + "/");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}