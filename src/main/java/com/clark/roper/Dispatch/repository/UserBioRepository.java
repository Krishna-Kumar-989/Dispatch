package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserBio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBioRepository extends JpaRepository<UserBio,Long> {


    Optional<UserBio> findByUser(User user);
}
