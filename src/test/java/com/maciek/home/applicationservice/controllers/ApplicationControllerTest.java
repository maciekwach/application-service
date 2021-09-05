package com.maciek.home.applicationservice.controllers;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.model.State;
import com.maciek.home.applicationservice.sevice.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.maciek.home.applicationservice.model.State.CREATED;
import static com.maciek.home.applicationservice.model.State.PUBLISHED;
import static com.maciek.home.applicationservice.model.State.VERIFIED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
class ApplicationControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Mock
    ApplicationService service;

    @InjectMocks
    ApplicationController controller;

    private MockMvc mockMvc;

    List<Application> applicationList;
    Application application;

    @BeforeEach
    void setup() {
        applicationList = new ArrayList<>();
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        createApplication(22, "B", CREATED);
        createApplication(24, "A", PUBLISHED);
        createApplication(25, "C", VERIFIED);
        when(service.findAll()).thenReturn(applicationList);

        mockMvc.perform(get("/applications/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("TestB")))
                .andExpect(status().isOk());
    }

    @Test
    void testAllByName() throws Exception {
        createApplication(25, "C", VERIFIED);
        createApplication(22, "B", CREATED);
        createApplication(24, "A", PUBLISHED);
        when(service.findAllOrderByName("desc", 0)).thenReturn(applicationList);

        mockMvc.perform(get("/applications/name/desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("TestC")))
                .andExpect(status().isOk());
    }

    @Test
    void testAllByStateWithPage() throws Exception {
        createApplication(22, "B", CREATED);
        createApplication(21, "A", PUBLISHED);
        when(service.findAllOrderByState("desc", 1)).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/applications/state/desc/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    void testAllByStateWithoutPage() throws Exception {
        createApplication(22, "B", CREATED);
        createApplication(21, "A", PUBLISHED);
        when(service.findAllOrderByState("desc", 0)).thenReturn(applicationList);
        mockMvc.perform(get("/applications/state/desc/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name", is("TestB")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        createApplication(3, "A", PUBLISHED);
        when(service.findById(1L)).thenReturn(applicationList.get(0));
        mockMvc.perform(get("/applications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("TestA")))
                .andExpect(status().isOk());

    }

    @Test
    void testCreate() throws Exception {
        createApplication(22, "B", CREATED);
        when(service.createNew(applicationList.get(0))).thenReturn(true);
        mockMvc.perform(post("http://localhost:8088/applications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":22,\"name\":\"TestB\",\"content\":\"content of TestB\",\"state\":\"CREATED\",\"rejectionReason\":null}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateNull() throws Exception {
        mockMvc.perform(post("http://localhost:8088/applications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":22}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateById() throws Exception {
        createApplication(22, "B", CREATED);
        when(service.updateById(22L, applicationList.get(0))).thenReturn(true);
        mockMvc.perform(put("http://localhost:8088/applications/update/22")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":22,\"name\":\"Updated\",\"content\":\"Updated content\",\"rejectionReason\":null}"))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateByIdNotFound() throws Exception {
        createApplication(22, "B", CREATED);
        when(service.updateById(22L, applicationList.get(0))).thenReturn(false);
        mockMvc.perform(put("http://localhost:8088/applications/update/22")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":22,\"name\":\"Updated\",\"content\":\"Updated content\",\"rejectionReason\":null}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRejectById() throws Exception {
        createApplication(2, "A", CREATED);
        when(service.rejectById(2L, applicationList.get(0))).thenReturn(true);
        mockMvc.perform(put("http://localhost:8088/applications/reject/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2,\"name\":\"TestA\",\"content\":\"content of TestA\",\"state\":\"REJECTED\",\"rejectionReason\":\"Application don't met requirements\"}"))
                .andExpect(jsonPath("$.rejectionReason", is("Application don't met requirements")))
                .andExpect(jsonPath("$.state", is("REJECTED")))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyById() throws Exception {
//        createApplication(2, "A", CREATED);
        when(service.verifyById(2L)).thenReturn(true);
        mockMvc.perform(put("http://localhost:8088/applications/verify/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testDeleteById() throws Exception {
        when(service.deleteById(2L)).thenReturn(true);
        mockMvc.perform(delete("http://localhost:8088/applications/remove/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAcceptById() throws Exception {
        when(service.acceptById(2L)).thenReturn(true);
        mockMvc.perform(put("http://localhost:8088/applications/accept/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPublishById() throws Exception {
        when(service.publishById(2L)).thenReturn(true);
        mockMvc.perform(put("http://localhost:8088/applications/publish/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void createApplication(int id, String appSuffix, State state) {
        applicationList.add(Application.builder()
                .id((long) id)
                .name("Test" + appSuffix)
                .content("content of application Test" + appSuffix)
                .state(state)
                .build()
        );
    }

}