package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

  Optional<UserFollow> findByFollowerAndFollowed(User follower, User followed);

  boolean existsByFollowerAndFollowed(User follower, User followed);

  Page<UserFollow> findByFollower(User follower, Pageable pageable);

  Page<UserFollow> findByFollowed(User followed, Pageable pageable);

  long countByFollowed(User followed);

  long countByFollower(User follower);
}
