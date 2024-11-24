package ee.cookbook.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.cookbook.model.SavedMenu;

public interface SavedMenuRepository extends JpaRepository<SavedMenu, SavedMenu>{
}
