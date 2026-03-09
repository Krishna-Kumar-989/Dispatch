package com.clark.roper.Dispatch.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


 //In memory limiter , limits by ip per window


@Component
public class RateLimitFilter implements Filter {

  private static final int MAX_REQUESTS = 100; // max requests per window
  private static final long WINDOW_MS = 60_000; // 1 minute window
  private static final int MAX_BUCKETS = 10_000; // max tracked IPs

  private final Map<String, RateBucket> buckets = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    // Periodically evict stale buckets to prevent memory leak
    if (buckets.size() > MAX_BUCKETS) {
      evictStaleBuckets();
    }

    String clientIp = request.getRemoteAddr();
    RateBucket bucket = buckets.computeIfAbsent(clientIp, k -> new RateBucket());

    if (bucket.allowRequest()) {
      chain.doFilter(request, response);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(429);
      httpResponse.setContentType("application/json");
      httpResponse.getWriter().write(
          "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again later.\"}");
    }
  }


   //Remove buckets that have not received requests within the last 2 windows.

  private void evictStaleBuckets() {
    long cutoff = System.currentTimeMillis() - (WINDOW_MS * 2);
    Iterator<Map.Entry<String, RateBucket>> it = buckets.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, RateBucket> entry = it.next();
      if (entry.getValue().windowStart.get() < cutoff) {
        it.remove();
      }
    }
  }

  private static class RateBucket {
    private final AtomicInteger count = new AtomicInteger(0);
    final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());

    boolean allowRequest() {
      long now = System.currentTimeMillis();
      if (now - windowStart.get() > WINDOW_MS) {
        // Reset window
        windowStart.set(now);
        count.set(1);
        return true;
      }
      return count.incrementAndGet() <= MAX_REQUESTS;
    }
  }
}
