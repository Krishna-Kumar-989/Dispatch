package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.GeneralLetter;
import com.clark.roper.Dispatch.entity.LetterTag;
import com.clark.roper.Dispatch.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterTagRepository extends JpaRepository<LetterTag, Long> {

  List<LetterTag> findByLetter(GeneralLetter letter);

  List<LetterTag> findByTag(Tag tag);

  void deleteByLetter(GeneralLetter letter);
}
