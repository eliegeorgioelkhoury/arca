package com.arca.repo;

import com.arca.domain.JournalLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalLineRepository extends JpaRepository<JournalLine, Long> {
}
