package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.GeneralLetter;
import com.clark.roper.Dispatch.entity.GeneralLetterLike;
import com.clark.roper.Dispatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralLetterLikeRepository extends JpaRepository<GeneralLetterLike, Long> {

  Optional<GeneralLetterLike> findByLetterAndUser(GeneralLetter letter, User user);

  boolean existsByLetterAndUser(GeneralLetter letter, User user);

  long countByLetter(GeneralLetter letter);
}
