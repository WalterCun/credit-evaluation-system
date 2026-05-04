package com.credit.orchestrator.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "cors")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface CorsConfig {
}
