package com.maciek.home.applicationservice.sevice;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

import static com.maciek.home.applicationservice.model.State.ACCEPTED;
import static com.maciek.home.applicationservice.model.State.CREATED;
import static com.maciek.home.applicationservice.model.State.PUBLISHED;
import static com.maciek.home.applicationservice.model.State.REJECTED;
import static com.maciek.home.applicationservice.model.State.VERIFIED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ApplicationServiceImplTest {

    @MockBean
    private ApplicationRepository repository;

    @Autowired
    private ApplicationService underTest;// = new ApplicationServiceImpl(repository);

    private Application application;
    ArrayList<Application> applicationsList;

    @BeforeEach
    void setUp() {
        applicationsList = new ArrayList<>();
    }

    @Test
    void testFindById() {
        application = new Application(1L, "TestNameA", "Test Content", CREATED, "");
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(application));
        assertEquals(application, underTest.findById(1L));
        assertEquals("TestNameA", underTest.findById(1L).getName());
    }

    @Test
    void testFindAll() {
        applicationsList.add(new Application(1L, "TestNameB", "Test Content", CREATED, ""));
        applicationsList.add(new Application(2L, "TestNameC", "Test Content", CREATED, ""));
        applicationsList.add(new Application(3L, "TestNameA", "Test Content", CREATED, ""));
        when(repository.findAll()).thenReturn(applicationsList);

        List<Application> resultList = underTest.findAll();
        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals("TestNameA", resultList.get(2).getName());
    }

    @Test
    void testFindAllOrderByName() {
        applicationsList.add(new Application(2L, "TestNameC", "Test Content", CREATED, ""));
        applicationsList.add(new Application(1L, "TestNameB", "Test Content", CREATED, ""));
        applicationsList.add(new Application(3L, "TestNameA", "Test Content", CREATED, ""));
        when(repository.findAllByOrderByNameDesc(PageRequest.of(0, 10))).thenReturn(applicationsList);

        List<Application> resultList = underTest.findAllOrderByName("desc", 0);
        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals("TestNameA", resultList.get(2).getName());

    }

    @Test
    void testFindAllOrderByState() {
        applicationsList.add(new Application(2L, "TestNameC", "Test Content", CREATED, ""));
        applicationsList.add(new Application(3L, "TestNameA", "Test Content", CREATED, ""));
        applicationsList.add(new Application(1L, "TestNameB", "Test Content", REJECTED, ""));
        when(repository.findAllByOrderByStateAsc(PageRequest.of(0, 10))).thenReturn(applicationsList);

        List<Application> resultList = underTest.findAllOrderByState("asc", 0);
        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals(CREATED, resultList.get(0).getState());
        assertEquals(REJECTED, resultList.get(2).getState());
    }

    @Test
    void testCreateNew() throws ServerException {
        application = new Application(1L, "TestNameA", "Test Content", CREATED, "");
        assertTrue(underTest.createNew(application));
    }

    @Test
    void testCreateNewEmptyName() throws ServerException {
        application = new Application(1L, "", "Test Content", CREATED, "");
        assertFalse(underTest.createNew(application));
    }

    @Test
    void testDeleteById() {
        application = new Application(3L, "TestNameA", "Test Content", CREATED, "");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.deleteById(3L));
    }
    @Test
    void testDeleteByIdWrongState() {
        application = new Application(3L, "TestNameA", "Test Content", VERIFIED, "");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.deleteById(3L));
    }

    @Test
    void testUpdateById() {
        application = new Application(3L, "Updated Name", "Updated Content", VERIFIED, "");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.updateById(3L, application));
    }
    @Test
    void testUpdateByIdWrongState() {
        application = new Application(3L, "Updated Name", "Updated Content", REJECTED, "");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.updateById(3L, application));
    }

    @Test
    void testRejectById() {
        application = new Application(3L, "TestNameA", "", VERIFIED, "Application don't met requirements");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.rejectById(3L, application));
    }
    @Test
    void testRejectByIdWrongState() {
        application = new Application(3L, "", "", PUBLISHED, "Application don't met requirements");
        when(repository.findById(3L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.rejectById(3L, application));
    }

    @Test
    void testVerifyById() {
        application = new Application(2L, "TestNameA", "Test Content", CREATED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.verifyById(2L));
    }
    @Test
    void testVerifyByIdWrongState() {
        application = new Application(2L, "TestNameA", "Test Content", VERIFIED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.verifyById(2L));
    }

    @Test
    void testAcceptById() {
        application = new Application(2L, "TestNameA", "Test Content", VERIFIED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.acceptById(2L));
    }
    @Test
    void testAcceptByIdWrongState() {
        application = new Application(2L, "TestNameA", "Test Content", CREATED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.acceptById(2L));
    }

    @Test
    void testPublishById() {
        application = new Application(2L, "TestNameA", "Test Content", ACCEPTED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertTrue(underTest.publishById(2L));
    }

    @Test
    void testPublishByIdWrongState() {
        application = new Application(2L, "TestNameA", "Test Content", VERIFIED, "Application don't met requirements");
        when(repository.findById(2L)).thenReturn(java.util.Optional.of(application));
        assertFalse(underTest.publishById(2L));
    }
}