package com.clark.roper.Dispatch.configuration;

import com.clark.roper.Dispatch.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Updates user's lastActive timestamp on every authenticated request.


@Component
public class LastActiveFilter implements Filter {

  private static final Logger log = LoggerFactory.getLogger(LastActiveFilter.class);

  // Buffer holding username -> latest activity time
  private final ConcurrentHashMap<String, Instant> pendingUpdates = new ConcurrentHashMap<>();

  private final UserRepository userRepository;

  public LastActiveFilter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    chain.doFilter(request, response);

    // After the request is processed, record the activity in memory
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
        String username = auth.getName();
        pendingUpdates.put(username, Instant.now());
      }
    } catch (Exception e) {
      log.warn("Failed to record lastActive for request: {}", e.getMessage(), e);
    }
  }

  // Flushes the buffer every 30 seconds.

  @Scheduled(fixedRateString = "${app.last-active.flush-rate:30000}")
  @Transactional
  public void flushLastActive() {
    if (pendingUpdates.isEmpty())
      return;

    // Snapshot the current updates and clear the buffer
    Map<String, Instant> snapshot = new HashMap<>(pendingUpdates);
    pendingUpdates.clear();

    log.debug("Flushing {} lastActive updates to database", snapshot.size());

    snapshot.forEach((username, time) -> {
      try {
        userRepository.updateLastActive(username, time);
      } catch (Exception e) {
        log.error("Failed to update lastActive for user {}", username, e);
      }
    });
  }
}
