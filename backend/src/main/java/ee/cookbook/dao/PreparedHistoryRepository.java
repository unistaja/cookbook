package ee.cookbook.dao;

import ee.cookbook.model.PreparedHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface PreparedHistoryRepository extends JpaRepository<PreparedHistory, Date> {
}
