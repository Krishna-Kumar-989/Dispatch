package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Interests;
import com.clark.roper.Dispatch.entity.UserProfile;
import com.clark.roper.Dispatch.entity.UserProfileInterestsJunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileInterestsJunctionRepository extends JpaRepository<UserProfileInterestsJunction, Long> {

    List<UserProfileInterestsJunction> findByUserProfile(UserProfile userProfile);

    List<UserProfileInterestsJunction> findByInterests(Interests interests);

    void deleteByUserProfile(UserProfile userProfile);
}
