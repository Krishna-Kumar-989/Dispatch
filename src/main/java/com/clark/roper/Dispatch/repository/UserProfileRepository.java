package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {

    boolean existsByUser(User user);
    Optional<UserProfile> findByUser(User user);
}
