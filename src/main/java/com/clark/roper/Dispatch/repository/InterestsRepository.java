package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Interests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestsRepository extends JpaRepository<Interests,Long> {

    Optional<Interests> findByInterest(String interest);
}
