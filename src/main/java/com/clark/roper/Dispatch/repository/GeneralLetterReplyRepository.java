package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.GeneralLetter;
import com.clark.roper.Dispatch.entity.GeneralLetterReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralLetterReplyRepository extends JpaRepository<GeneralLetterReply, Long> {

  Page<GeneralLetterReply> findByLetter(GeneralLetter letter, Pageable pageable);

  long countByLetter(GeneralLetter letter);
}
