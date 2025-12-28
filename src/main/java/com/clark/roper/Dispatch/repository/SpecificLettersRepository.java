package com.clark.roper.Dispatch.repository;


import com.clark.roper.Dispatch.entity.SpecificLetters;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificLettersRepository extends JpaRepository<SpecificLetters,Long> {


    List<SpecificLetters> findByReceiver_Id(Long receiverId);
    List<SpecificLetters> findBySender_Id(Long senderId);



}
