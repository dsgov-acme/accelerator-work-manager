package io.nuvalence.workmanager.service.config;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for scheduling and locking.
 */
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ScheduleLockConfig {}
