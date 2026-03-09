package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Languages;
import com.clark.roper.Dispatch.entity.UserProfile;
import com.clark.roper.Dispatch.entity.UserProfileLanguagesJunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileLanguagesJunctionRepository extends JpaRepository<UserProfileLanguagesJunction, Long> {

    List<UserProfileLanguagesJunction> findByUserProfile(UserProfile userProfile);

    List<UserProfileLanguagesJunction> findByLanguages(Languages languages);

    void deleteByUserProfile(UserProfile userProfile);
}
