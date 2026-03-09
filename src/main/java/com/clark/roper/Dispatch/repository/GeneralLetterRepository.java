package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.GeneralLetter;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralLetterRepository extends JpaRepository<GeneralLetter, Long> {

        /**
         * Acquires a pessimistic write lock (SELECT ... FOR UPDATE) on the letter row.
         * Used by toggleLike to serialize concurrent like/unlike operations on the same letter
         */
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT l FROM GeneralLetter l WHERE l.id = :id")
        Optional<GeneralLetter> findByIdForUpdate(@Param("id") Long id);

        Page<GeneralLetter> findAll(Pageable pageable);

        Page<GeneralLetter> findByAuthor(User author, Pageable pageable);

        Page<GeneralLetter> findByStatus(SpecificLettersStatusEnum status, Pageable pageable);

        // Search by keyword in title or content
        @Query("SELECT l FROM GeneralLetter l WHERE l.status = 'SENT' " +
                        "AND (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(l.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<GeneralLetter> searchByKeyword(String keyword, Pageable pageable);

        // Search by tag name
        @Query("SELECT l FROM GeneralLetter l JOIN LetterTag lt ON lt.letter = l " +
                        "JOIN Tag t ON lt.tag = t WHERE t.name = :tagName AND l.status = 'SENT'")
        Page<GeneralLetter> findByTagName(String tagName, Pageable pageable);

        // Find scheduled letters that are due
        @Query("SELECT l FROM GeneralLetter l WHERE l.status = 'DRAFT' " +
                        "AND l.scheduledAt IS NOT NULL AND l.scheduledAt <= :now")
        List<GeneralLetter> findDueScheduledLetters(Instant now);

        @Modifying
        @Query("UPDATE GeneralLetter l SET l.likeCount = l.likeCount + 1 WHERE l.id = :id")
        void incrementLikeCount(@Param("id") Long id);

        @Modifying
        @Query("UPDATE GeneralLetter l SET l.likeCount = CASE WHEN l.likeCount > 0 THEN l.likeCount - 1 ELSE 0 END WHERE l.id = :id")
        void decrementLikeCount(@Param("id") Long id);

        @Modifying
        @Query("UPDATE GeneralLetter l SET l.replyCount = l.replyCount + 1 WHERE l.id = :id")
        void incrementReplyCount(@Param("id") Long id);

        // ─── Counter Reconciliation ───────────────────────────────────────────
        // Bulk-syncs denormalized counters with actual row counts.

        @Modifying
        @Query("UPDATE GeneralLetter l SET l.likeCount = " +
                        "(SELECT COUNT(gl) FROM GeneralLetterLike gl WHERE gl.letter = l) " +
                        "WHERE l.likeCount <> (SELECT COUNT(gl) FROM GeneralLetterLike gl WHERE gl.letter = l)")
        int reconcileLikeCounts();

        @Modifying
        @Query("UPDATE GeneralLetter l SET l.replyCount = " +
                        "(SELECT COUNT(r) FROM GeneralLetterReply r WHERE r.letter = l) " +
                        "WHERE l.replyCount <> (SELECT COUNT(r) FROM GeneralLetterReply r WHERE r.letter = l)")
        int reconcileReplyCounts();
}
