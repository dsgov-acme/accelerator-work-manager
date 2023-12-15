package io.nuvalence.workmanager.service.service;

import io.nuvalence.workmanager.service.domain.profile.Address;
import io.nuvalence.workmanager.service.domain.profile.Employer;
import io.nuvalence.workmanager.service.models.EmployerFilters;
import io.nuvalence.workmanager.service.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository repository;

    public Page<Employer> getEmployersByFilters(final EmployerFilters filters) {
        return repository.findAll(
                filters.getEmployerProfileSpecification(), filters.getPageRequest());
    }

    public Optional<Employer> getEmployerById(final UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return repository.findById(id);
    }

    public Employer saveEmployer(final Employer employer) {
        if (employer.getMailingAddress() != null) {
            employer.getMailingAddress().setEmployerForMailing(employer);
        }

        if (employer.getLocations() != null) {
            for (Address location : employer.getLocations()) {
                location.setEmployerForLocations(employer);
            }
        }

        return repository.save(employer);
    }
}
