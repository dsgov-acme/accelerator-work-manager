package io.nuvalence.workmanager.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.nuvalence.workmanager.service.domain.profile.Employer;
import io.nuvalence.workmanager.service.models.EmployerFilters;
import io.nuvalence.workmanager.service.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class EmployerServiceTest {

    @Mock private EmployerRepository repository;

    private EmployerService service;

    @BeforeEach
    public void setUp() {
        service = new EmployerService(repository);
    }

    @Test
    void getEmployersByFilters() {
        Employer employer = Employer.builder().id(UUID.randomUUID()).build();
        Page<Employer> employerPageExpected = new PageImpl<>(Collections.singletonList(employer));

        when(repository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(employerPageExpected);

        Page<Employer> employerPageResult =
                service.getEmployersByFilters(
                        EmployerFilters.builder()
                                .sortBy("legalName")
                                .sortOrder("ASC")
                                .pageNumber(0)
                                .pageSize(10)
                                .fein("fein")
                                .name("name")
                                .type("LLC")
                                .industry("industry")
                                .build());

        assertEquals(employerPageExpected, employerPageResult);
    }

    @Test
    void getEmployerById_Success() {
        Employer employer = Employer.builder().id(UUID.randomUUID()).build();

        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(employer));

        Optional<Employer> employerResult = service.getEmployerById(UUID.randomUUID());

        assertTrue(employerResult.isPresent());
        assertEquals(employer, employerResult.get());
    }

    @Test
    void getEmployerById_Null() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Employer> employerResult = service.getEmployerById(UUID.randomUUID());

        assertTrue(employerResult.isEmpty());
    }

    @Test
    void saveEmployer() {
        Employer employer = Employer.builder().id(UUID.randomUUID()).build();

        when(repository.save(any(Employer.class))).thenReturn(employer);

        Employer employerResult = service.saveEmployer(employer);

        assertEquals(employer, employerResult);
    }
}
