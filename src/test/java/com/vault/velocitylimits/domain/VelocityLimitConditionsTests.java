package com.vault.velocitylimits.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.repository.ILoadedCustomerFundsRepository;
import com.vault.velocitylimits.domain.repository.LoadedCustomerFundsEntity;
import com.vault.velocitylimits.domain.service.VelocityLimitException;
import com.vault.velocitylimits.domain.service.impl.LoadFundsService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@ActiveProfiles("test")
class VelocityLimitConditionsTests {
    @Value("${velocitylimits.perday.fund.limit}")
    private double VELOCITY_LIMITS_PER_DAY_FUND;
    @Value("${velocitylimits.perday.transactions.limit}")
    private int VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT;
    @Value("${velocitylimits.perweek.fund.limit}")
    private int VELOCITY_LIMITS_PER_WEEK_FUND;
    @Value("${load.funds.input.file}")
    private String INPUT_FILE;
    @Value("${load.funds.output.file}")
    private String OUTPUT_FILE;
    private LoadFundsService loadFundsService;
    @Mock
    private ILoadedCustomerFundsRepository loadedCustomerFundsRepository;
    @Mock
    private ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER_UTC =DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @BeforeEach
    public void setup() {
        loadFundsService = new LoadFundsService(loadedCustomerFundsRepository,
                objectMapper,
                VELOCITY_LIMITS_PER_DAY_FUND,
                VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT,
                VELOCITY_LIMITS_PER_WEEK_FUND,
                INPUT_FILE,
                OUTPUT_FILE);
    }

    @Test
    @DisplayName("Test valid load funds attempt inline with per day fund velocity limits")
    public void testLoadValidFundsAmountPerDay() {
        // setup
        Mockito.doReturn(new ArrayList<LoadedCustomerFundsEntity>()).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(any(), any(), any());

        // execute and verify
        Assertions.assertDoesNotThrow(() -> loadFundsService.verifyCustomerMaxLoadAmountLimitPerDay(1L, LocalDateTime.now(), 5000),
                "Fund Load Amount Limit per Day exceeded.");
    }

    @Test
    @DisplayName("Test Invalid load funds attempt not inline with per day fund velocity limits")
    public void testLoadInValidFundsAmountPerDay() {
        // setup
        Mockito.doReturn(new ArrayList<LoadedCustomerFundsEntity>()).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(any(), any(), any());

        // execute and verify
        Assertions.assertThrowsExactly(VelocityLimitException.class, () -> loadFundsService.verifyCustomerMaxLoadAmountLimitPerDay(2L, LocalDateTime.now(), 6000),
                "Fund Load Amount Limit per Day exceeded.");
    }

    @Test
    @DisplayName("Test valid load fund attempts inline with per day attempts velocity limits")
    public void testTestValidAttemptsPerDay() {
        // setup
        List<LoadedCustomerFundsEntity> loadedCustomerFundsEntityList = new ArrayList<>();
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(1L, 3L, 400, LocalDateTime.now()));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(2L, 3L, 400, LocalDateTime.now().plusHours(1)));
        Mockito.doReturn(loadedCustomerFundsEntityList).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(any(), any(), any());

        // execute and verify
        Assertions.assertDoesNotThrow( () -> loadFundsService.verifyCustomerMaxLoadAmountLimitPerDay(3L, LocalDateTime.now(), 400),
                "Fund Load Transactions Limit per Day exceeded.");
    }

    @Test
    @DisplayName("Test Invalid fund load attempts not inline with per day attempts velocity limits")
    public void testTestInValidAttemptsPerDay() {
        // setup
        List<LoadedCustomerFundsEntity> loadedCustomerFundsEntityList = new ArrayList<>();
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(1L, 3L, 400, LocalDateTime.now()));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(2L, 3L, 400, LocalDateTime.now().plusHours(1)));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(3L, 3L, 400, LocalDateTime.now().plusHours(2)));
        Mockito.doReturn(loadedCustomerFundsEntityList).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(any(), any(), any());

        // execute and verify
        Assertions.assertThrowsExactly(VelocityLimitException.class, () -> loadFundsService.verifyCustomerMaxLoadAmountLimitPerDay(3L, LocalDateTime.now(), 400),
                "Fund Load Transactions Limit per Day exceeded.");
    }

    @Test
    @DisplayName("Test valid load fund attempts per week inline with velocity limits")
    public void testValidLoadFundsPerWeek(){
        // setup
        List<LoadedCustomerFundsEntity> loadedCustomerFundsEntityList = new ArrayList<>();
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(1L, 3L, 10000, LocalDateTime.parse("2023-04-10T13:45:18Z", DATE_FORMATTER_UTC)));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(2L, 3L, 5000, LocalDateTime.parse("2023-04-11T12:45:18Z", DATE_FORMATTER_UTC)));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(3L, 3L, 2500, LocalDateTime.parse("2023-04-12T12:45:18Z", DATE_FORMATTER_UTC)));
        Mockito.doReturn(loadedCustomerFundsEntityList).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(eq(3L), any(), any());

        // execute and verify
        Assertions.assertDoesNotThrow(()->loadFundsService.verifyCustomerNoOfLoadsInAWeek(3L,LocalDateTime.parse("2023-04-14T13:45:18Z", DATE_FORMATTER_UTC),2500),"Fund Load Amount Limit per week exceeded.");
    }

    @Test
    @DisplayName("Test Invalid load fund attempts per week not inline with velocity limits")
    public void testInValidLoadFundsPerWeek(){
        // setup
        List<LoadedCustomerFundsEntity> loadedCustomerFundsEntityList = new ArrayList<>();
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(1L, 3L, 10000, LocalDateTime.parse("2023-04-10T13:45:18Z", DATE_FORMATTER_UTC)));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(2L, 3L, 5000, LocalDateTime.parse("2023-04-11T12:45:18Z", DATE_FORMATTER_UTC)));
        loadedCustomerFundsEntityList.add(new LoadedCustomerFundsEntity(3L, 3L, 5000, LocalDateTime.parse("2023-04-12T12:45:18Z", DATE_FORMATTER_UTC)));
        Mockito.doReturn(loadedCustomerFundsEntityList).when(loadedCustomerFundsRepository).findAllByCustomerIdAndTimeBetween(eq(3L), any(), any());

        // execute and verify
        Assertions.assertThrows(VelocityLimitException.class, ()->loadFundsService.verifyCustomerNoOfLoadsInAWeek(3L,LocalDateTime.parse("2023-04-14T13:45:18Z", DATE_FORMATTER_UTC),2500),"Fund Load Amount Limit per week exceeded.");
    }

}
