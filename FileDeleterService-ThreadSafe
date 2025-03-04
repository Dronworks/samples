package com.attensity.utils.satellitemodeltoolbox.service.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class FileDeleterService {
    public static final int DELETION_TIMEOUT = 10;
    public static final int THREAD_LOCK_TIMEOUT = DELETION_TIMEOUT + 1;
    private final ConcurrentHashMap<Path, ReentrantLock> fileLocks = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final FileHelper fileHelper;

    public FileDeleterService(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public boolean deleteFile(Path filePath) {
        ReentrantLock lock = fileLocks.computeIfAbsent(filePath, k -> new ReentrantLock());
        boolean acquired = false;
        try {
            acquired = lock.tryLock(THREAD_LOCK_TIMEOUT, TimeUnit.SECONDS); // Acquire lock with timeout
            if (!acquired) {
                log.warn("Could not acquire lock to delete file {} within timeout.", filePath);
                return false;
            }
            return tryToDeleteFileWithTimeout(filePath);
        } catch (TimeoutException e) {
            log.error("File deletion timed out for {}", filePath);
            return false;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while deleting file {}", filePath, e);
            return false;
        } finally {
            releaseLock(filePath, acquired, lock);
        }
    }

    private void releaseLock(Path filePath, boolean acquired, ReentrantLock lock) {
        if (acquired) {
            lock.unlock();
            fileLocks.compute(filePath, (key, existingLock) ->
                    (existingLock != null && existingLock.hasQueuedThreads()) ? existingLock : null);
        }
    }

    private Boolean tryToDeleteFileWithTimeout(Path filePath) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Boolean> deletionTask = executor.submit(() -> {
            try {
                if (Files.deleteIfExists(filePath)) {
                    log.info("File {} deleted successfully.", filePath);
                    return true;
                } else {
                    log.info("File {} was not found, nothing to delete.", filePath);
                    return false;
                }
            } catch (IOException e) {
                log.error("Failed to delete file {}.", filePath, e);
                return false;
            }
        });

        return deletionTask.get(DELETION_TIMEOUT, TimeUnit.SECONDS);
    }
}
