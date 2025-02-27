package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

@Service
@Slf4j
public class SyncService {

  @Value("${probeCommandLockTimeoutSec}")
  private int probeCommandLockTimeoutSec;

  private final ConcurrentHashMap<Integer, ReentrantLock> probeLocks = new ConcurrentHashMap<>();

  public <T, U> void synchronize(int probeId, BiConsumer<T, U> action, T arg1, U arg2) {

    ReentrantLock lock = probeLocks.computeIfAbsent(probeId, id -> new ReentrantLock());
    boolean lockAcquired = false;
    try {
      lockAcquired =
        lock.tryLock(probeCommandLockTimeoutSec, java.util.concurrent.TimeUnit.SECONDS);
      if (lockAcquired) {
        action.accept(arg1, arg2);
      } else {
        throw new IllegalStateException("Could not acquire lock for probeId " + probeId + " within timeout");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Thread interrupted while waiting for lock for probeId=" + probeId + ", exception=" + e.getMessage());
    } finally {
      if (lockAcquired) {
        lock.unlock();
      }
    }
  }

  private ResponseEntity<Void> synchronizeProbeCommandExecution(int probeId, String command) {

    return null;
  }
}
