package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserConnections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConnectionsRepository extends JpaRepository<UserConnections,Long> {

    @Query("""
        SELECT uc FROM UserConnections uc
        WHERE (uc.user1 = :userA AND uc.user2 = :userB)
           OR (uc.user1 = :userB AND uc.user2 = :userA)
    """)
    Optional<UserConnections> findConnectionBetweenUsers(
            @Param("userA") User userA,
            @Param("userB") User userB
    );
}
