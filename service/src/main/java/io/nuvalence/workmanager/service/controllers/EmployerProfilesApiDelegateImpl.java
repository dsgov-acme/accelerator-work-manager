package io.nuvalence.workmanager.service.controllers;

import io.nuvalence.auth.access.AuthorizationHandler;
import io.nuvalence.workmanager.service.config.exceptions.UnexpectedException;
import io.nuvalence.workmanager.service.domain.profile.Employer;
import io.nuvalence.workmanager.service.generated.controllers.EmployerProfilesApiDelegate;
import io.nuvalence.workmanager.service.generated.models.EmployerProfileCreateModel;
import io.nuvalence.workmanager.service.generated.models.EmployerProfileResponseModel;
import io.nuvalence.workmanager.service.generated.models.EmployerProfileUpdateModel;
import io.nuvalence.workmanager.service.generated.models.PageEmployerProfileResponseModel;
import io.nuvalence.workmanager.service.mapper.EmployerMapper;
import io.nuvalence.workmanager.service.mapper.PagingMetadataMapper;
import io.nuvalence.workmanager.service.models.EmployerFilters;
import io.nuvalence.workmanager.service.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

@Service
@RequiredArgsConstructor
public class EmployerProfilesApiDelegateImpl implements EmployerProfilesApiDelegate {
    private final AuthorizationHandler authorizationHandler;
    private final EmployerService employerService;
    private final EmployerMapper employerMapper;
    private final PagingMetadataMapper pagingMetadataMapper;

    @Override
    public ResponseEntity<EmployerProfileResponseModel> getEmployerProfile(UUID profileId) {
        final EmployerProfileResponseModel employerProfileResponseModel =
                employerService
                        .getEmployerById(profileId)
                        .filter(
                                employerInstance ->
                                        authorizationHandler.isAllowedForInstance(
                                                "view", employerInstance))
                        .map(employerMapper::employerToResponseModel)
                        .orElseThrow(() -> new NotFoundException("Employer profile not found"));

        return ResponseEntity.status(200).body(employerProfileResponseModel);
    }

    @Override
    public ResponseEntity<PageEmployerProfileResponseModel> getEmployerProfiles(
            String fein,
            String name,
            String type,
            String industry,
            String sortBy,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize) {
        if (!authorizationHandler.isAllowed("view", Employer.class)) {
            throw new ForbiddenException();
        }

        Page<EmployerProfileResponseModel> results =
                employerService
                        .getEmployersByFilters(
                                new EmployerFilters(
                                        sortBy,
                                        sortOrder,
                                        pageNumber,
                                        pageSize,
                                        fein,
                                        name,
                                        type,
                                        industry))
                        .map(employerMapper::employerToResponseModel);

        PageEmployerProfileResponseModel response = new PageEmployerProfileResponseModel();
        response.items(results.toList());
        response.setPagingMetadata(pagingMetadataMapper.toPagingMetadata(results));

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<EmployerProfileResponseModel> postEmployerProfile(
            EmployerProfileCreateModel employerProfileCreateModel) {
        if (!authorizationHandler.isAllowed("create", Employer.class)) {
            throw new ForbiddenException();
        }

        Employer employer =
                employerService.saveEmployer(
                        employerMapper.createModelToEmployer(employerProfileCreateModel));

        EmployerProfileResponseModel employerProfileResponseModel =
                employerMapper.employerToResponseModel(employer);

        return ResponseEntity.status(200).body(employerProfileResponseModel);
    }

    @Override
    public ResponseEntity<EmployerProfileResponseModel> updateEmployerProfile(
            UUID profileId, EmployerProfileUpdateModel employerProfileUpdateModel) {
        if (!authorizationHandler.isAllowed("update", Employer.class)) {
            throw new ForbiddenException();
        }

        Optional<Employer> optionalEmployer = employerService.getEmployerById(profileId);
        if (optionalEmployer.isEmpty()) {
            throw new NotFoundException("Employer profile not found");
        }
        Employer existingEmployer = optionalEmployer.get();

        Employer employerToBeSaved =
                employerMapper.updateModelToEmployer(employerProfileUpdateModel);
        employerToBeSaved.setId(profileId);
        employerToBeSaved.setCreatedBy(existingEmployer.getCreatedBy());
        employerToBeSaved.setCreatedTimestamp(existingEmployer.getCreatedTimestamp());

        employerService.saveEmployer(employerToBeSaved);

        Employer savedEmployer =
                employerService
                        .getEmployerById(profileId)
                        .orElseThrow(
                                () ->
                                        new UnexpectedException(
                                                "Employer profile not found after saving"));

        return ResponseEntity.status(200)
                .body(employerMapper.employerToResponseModel(savedEmployer));
    }
}
