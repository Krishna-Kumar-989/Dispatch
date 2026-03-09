package com.clark.roper.Dispatch.repository;


import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SpecificLettersRepository extends JpaRepository<SpecificLetters,Long> {


    List<SpecificLetters> findByReceiver_Id(Long receiverId);
    List<SpecificLetters> findBySender_Id(Long senderId);
    List<SpecificLetters> findByStatus(SpecificLettersStatusEnum statusEnum);


    @Query("""
    SELECT sl FROM SpecificLetters sl
    WHERE (:senderId IS NULL OR sl.sender.id = :senderId)
      AND (:receiverId IS NULL OR sl.receiver.id = :receiverId)
      AND (:status IS NULL OR sl.status = :status)
      AND sl.createdAt >= COALESCE(:startDate, sl.createdAt)
      AND sl.createdAt <= COALESCE(:endDate, sl.createdAt)
""")
    List<SpecificLetters> filterLetters(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("status") SpecificLettersStatusEnum status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

}
