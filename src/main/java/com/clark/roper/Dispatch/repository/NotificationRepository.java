package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Notification;
import com.clark.roper.Dispatch.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

  long countByUserAndReadFalse(User user);
}
