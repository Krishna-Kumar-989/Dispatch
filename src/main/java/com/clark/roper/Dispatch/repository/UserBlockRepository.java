package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

  Optional<UserBlock> findByBlockerAndBlocked(User blocker, User blocked);

  boolean existsByBlockerAndBlocked(User blocker, User blocked);

  List<UserBlock> findByBlocker(User blocker);

  @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM UserBlock b " +
      "WHERE (b.blocker = :user1 AND b.blocked = :user2) " +
      "OR (b.blocker = :user2 AND b.blocked = :user1)")
  boolean isBlockedEitherWay(User user1, User user2);
}
