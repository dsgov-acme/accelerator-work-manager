package io.nuvalence.workmanager.service.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nuvalence.auth.access.AuthorizationHandler;
import io.nuvalence.workmanager.service.config.SpringConfig;
import io.nuvalence.workmanager.service.domain.profile.Address;
import io.nuvalence.workmanager.service.domain.profile.Employer;
import io.nuvalence.workmanager.service.generated.models.AddressModel;
import io.nuvalence.workmanager.service.generated.models.EmployerProfileCreateModel;
import io.nuvalence.workmanager.service.generated.models.EmployerProfileUpdateModel;
import io.nuvalence.workmanager.service.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.core.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@WithMockUser(authorities = {"wm:employer_profile_admin"})
public class EmployerProfilesApiDelegateImplTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AuthorizationHandler authorizationHandler;

    @MockBean private EmployerRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        when(authorizationHandler.isAllowed(any(), (Class<?>) any())).thenReturn(true);
        when(authorizationHandler.isAllowed(any(), (String) any())).thenReturn(true);
        when(authorizationHandler.isAllowedForInstance(any(), any())).thenReturn(true);
        when(authorizationHandler.getAuthFilter(any(), any())).thenReturn(element -> true);

        this.objectMapper = SpringConfig.getMapper();
    }

    @Test
    void getEmployerProfile_Success() throws Exception {
        Employer employer = createEmployer();
        UUID profileId = employer.getId();

        when(repository.findById(profileId)).thenReturn(Optional.of(employer));

        mockMvc.perform(get("/api/v1/employer-profiles/" + profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()))
                .andExpect(jsonPath("$.fein").value(employer.getFein()))
                .andExpect(jsonPath("$.legalName").value(employer.getLegalName()))
                .andExpect(jsonPath("$.otherNames", hasSize(1)))
                .andExpect(jsonPath("$.otherNames[0]").value(employer.getOtherNames().get(0)))
                .andExpect(jsonPath("$.type").value(employer.getType()))
                .andExpect(jsonPath("$.industry").value(employer.getIndustry()))
                .andExpect(jsonPath("$.summaryOfBusiness").value(employer.getSummaryOfBusiness()))
                .andExpect(
                        jsonPath("$.mailingAddress.address1")
                                .value(employer.getMailingAddress().getAddress1()))
                .andExpect(jsonPath("$.locations", hasSize(1)))
                .andExpect(
                        jsonPath("$.locations[0].address1")
                                .value(employer.getLocations().get(0).getAddress1()));
    }

    @Test
    void getEmployerProfile_NotFound() throws Exception {
        Employer employer = createEmployer();
        UUID profileId = employer.getId();
        when(repository.findById(profileId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employer-profiles/" + profileId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"messages\":[\"Employer profile not found\"]}"));
    }

    @Test
    void getEmployerProfiles() throws Exception {
        Employer employer = createEmployer();
        Page<Employer> employerPage = new PageImpl<>(Collections.singletonList(employer));
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(employerPage);

        mockMvc.perform(get("/api/v1/employer-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id").value(employer.getId().toString()))
                .andExpect(jsonPath("$.items[0].fein").value(employer.getFein()))
                .andExpect(jsonPath("$.items[0].legalName").value(employer.getLegalName()))
                .andExpect(jsonPath("$.items[0].otherNames", hasSize(1)))
                .andExpect(
                        jsonPath("$.items[0].otherNames[0]").value(employer.getOtherNames().get(0)))
                .andExpect(jsonPath("$.items[0].type").value(employer.getType()))
                .andExpect(jsonPath("$.items[0].industry").value(employer.getIndustry()))
                .andExpect(
                        jsonPath("$.items[0].summaryOfBusiness")
                                .value(employer.getSummaryOfBusiness()))
                .andExpect(
                        jsonPath("$.items[0].mailingAddress.address1")
                                .value(employer.getMailingAddress().getAddress1()))
                .andExpect(jsonPath("$.items[0].locations", hasSize(1)))
                .andExpect(
                        jsonPath("$.items[0].locations[0].address1")
                                .value(employer.getLocations().get(0).getAddress1()))
                .andExpect(jsonPath("$.pagingMetadata.totalCount").value(1))
                .andExpect(jsonPath("$.pagingMetadata.pageNumber").value(0));
    }

    @Test
    void postEmployerProfile() throws Exception {
        EmployerProfileCreateModel employer = profileCreateModel();

        String requestBodyJson = objectMapper.writeValueAsString(employer);

        when(repository.save(any(Employer.class))).thenReturn(createEmployer());

        mockMvc.perform(
                        post("/api/v1/employer-profiles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fein").value(employer.getFein()))
                .andExpect(jsonPath("$.legalName").value(employer.getLegalName()))
                .andExpect(jsonPath("$.otherNames", hasSize(1)))
                .andExpect(jsonPath("$.otherNames[0]").value(employer.getOtherNames().get(0)))
                .andExpect(jsonPath("$.type").value(employer.getType()))
                .andExpect(jsonPath("$.industry").value(employer.getIndustry()))
                .andExpect(jsonPath("$.summaryOfBusiness").value(employer.getSummaryOfBusiness()))
                .andExpect(
                        jsonPath("$.mailingAddress.address1")
                                .value(employer.getMailingAddress().getAddress1()))
                .andExpect(jsonPath("$.locations", hasSize(1)))
                .andExpect(
                        jsonPath("$.locations[0].address1")
                                .value(employer.getLocations().get(0).getAddress1()));
    }

    @Test
    void updateEmployerProfile_Success() throws Exception {
        EmployerProfileUpdateModel employer = profileUpdateModel();

        Employer modifiedEmployer =
                Employer.builder()
                        .id(UUID.randomUUID())
                        .fein("fein - changed")
                        .legalName("legalName - changed")
                        .otherNames(Collections.singletonList("otherNames - changed"))
                        .type("LLC")
                        .industry("industry - changed")
                        .summaryOfBusiness("summaryOfBusiness - changed")
                        .businessPhone("businessPhone - changed")
                        .mailingAddress(createAddress())
                        .locations(List.of(createAddress()))
                        .build();

        when(repository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createEmployer()))
                .thenReturn(Optional.of(modifiedEmployer));

        when(repository.save(any(Employer.class))).thenReturn(modifiedEmployer);

        String requestBodyJson = objectMapper.writeValueAsString(employer);

        mockMvc.perform(
                        put("/api/v1/employer-profiles/" + modifiedEmployer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fein").value(employer.getFein()))
                .andExpect(jsonPath("$.legalName").value(employer.getLegalName()))
                .andExpect(jsonPath("$.otherNames", hasSize(1)))
                .andExpect(jsonPath("$.otherNames[0]").value(employer.getOtherNames().get(0)))
                .andExpect(jsonPath("$.type").value(employer.getType()))
                .andExpect(jsonPath("$.industry").value(employer.getIndustry()))
                .andExpect(jsonPath("$.summaryOfBusiness").value(employer.getSummaryOfBusiness()))
                .andExpect(
                        jsonPath("$.mailingAddress.address1")
                                .value(employer.getMailingAddress().getAddress1()))
                .andExpect(jsonPath("$.locations", hasSize(1)))
                .andExpect(
                        jsonPath("$.locations[0].address1")
                                .value(employer.getLocations().get(0).getAddress1()));
    }

    @Test
    void updateEmployerProfile_NotFound() throws Exception {
        EmployerProfileUpdateModel employer = profileUpdateModel();

        String requestBodyJson = objectMapper.writeValueAsString(employer);

        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(
                        put("/api/v1/employer-profiles/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"messages\":[\"Employer profile not found\"]}"));
    }

    private Address createAddress() {
        return Address.builder()
                .address1("address1")
                .address2("address2")
                .city("city")
                .state("state")
                .postalCode("postalCode")
                .country("country")
                .county("county")
                .build();
    }

    private Employer createEmployer() {
        return Employer.builder()
                .id(UUID.randomUUID())
                .fein("fein")
                .legalName("legalName")
                .otherNames(Collections.singletonList("otherNames"))
                .type("LLC")
                .industry("industry")
                .summaryOfBusiness("summaryOfBusiness")
                .businessPhone("businessPhone")
                .mailingAddress(createAddress())
                .locations(List.of(createAddress()))
                .build();
    }

    private AddressModel createAddressModel() {
        AddressModel addressModel = new AddressModel();
        addressModel.address1("address1");
        addressModel.address2("address2");
        addressModel.city("city");
        addressModel.state("state");
        addressModel.postalCode("postalCode");
        addressModel.country("country");
        addressModel.county("county");

        return addressModel;
    }

    private EmployerProfileCreateModel profileCreateModel() {
        EmployerProfileCreateModel employerProfileCreateModel = new EmployerProfileCreateModel();
        employerProfileCreateModel.setFein("fein");
        employerProfileCreateModel.setLegalName("legalName");
        employerProfileCreateModel.setOtherNames(Collections.singletonList("otherNames"));
        employerProfileCreateModel.setType("LLC");
        employerProfileCreateModel.setIndustry("industry");
        employerProfileCreateModel.setSummaryOfBusiness("summaryOfBusiness");
        employerProfileCreateModel.setMailingAddress(createAddressModel());
        employerProfileCreateModel.setLocations(List.of(createAddressModel()));
        employerProfileCreateModel.businessPhone("businessPhone");

        return employerProfileCreateModel;
    }

    private EmployerProfileUpdateModel profileUpdateModel() {
        EmployerProfileUpdateModel employerProfileUpdateModel = new EmployerProfileUpdateModel();
        employerProfileUpdateModel.setFein("fein - changed");
        employerProfileUpdateModel.setLegalName("legalName - changed");
        employerProfileUpdateModel.setOtherNames(Collections.singletonList("otherNames - changed"));
        employerProfileUpdateModel.setType("LLC");
        employerProfileUpdateModel.setIndustry("industry - changed");
        employerProfileUpdateModel.setSummaryOfBusiness("summaryOfBusiness - changed");
        employerProfileUpdateModel.setMailingAddress(createAddressModel());
        employerProfileUpdateModel.setLocations(List.of(createAddressModel()));
        employerProfileUpdateModel.businessPhone("businessPhone - changed");

        return employerProfileUpdateModel;
    }
}
