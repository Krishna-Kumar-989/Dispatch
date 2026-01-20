package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Languages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguagesRepository extends JpaRepository<Languages,Long> {

    Optional<Languages> findByLanguage(String language);

}
