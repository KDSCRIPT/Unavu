package com.unavu.reviews.audit;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<String> {

    /**
     * @return the current auditor
     */
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        return Optional.of("REVIEWS_MS");
    }
}
