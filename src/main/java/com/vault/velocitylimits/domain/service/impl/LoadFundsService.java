package com.vault.velocitylimits.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.model.LoadFunds;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import com.vault.velocitylimits.domain.repository.ILoadedCustomerFundsRepository;
import com.vault.velocitylimits.domain.repository.LoadedCustomerFundsEntity;
import com.vault.velocitylimits.domain.service.ILoadFundsService;
import com.vault.velocitylimits.domain.service.LoadFundsException;
import com.vault.velocitylimits.domain.service.VelocityLimitException;
import com.vault.velocitylimits.domain.util.DateTimeUtil;
import com.vault.velocitylimits.domain.util.FileReaderUtil;
import com.vault.velocitylimits.domain.util.FileWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vault.velocitylimits.domain.util.FileWriterUtil.SLASH;

/**
 * @author harshagangavarapu
 */
@Service
public class LoadFundsService implements ILoadFundsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFundsService.class);
    private double VELOCITY_LIMITS_PER_DAY_FUND;
    private int VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT;
    private int VELOCITY_LIMITS_PER_WEEK_FUND;
    private String INPUT_FILE;
    private String OUTPUT_FILE;
    private ILoadedCustomerFundsRepository loadCustomerFundsRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public LoadFundsService(ILoadedCustomerFundsRepository loadCustomerFundsRepository,
                            ObjectMapper objectMapper,
                            @Value("${velocitylimits.perday.fund.limit}") double VELOCITY_LIMITS_PER_DAY_FUND,
                            @Value("${velocitylimits.perday.transactions.limit}") int VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT,
                            @Value("${velocitylimits.perweek.fund.limit}") int VELOCITY_LIMITS_PER_WEEK_FUND,
                            @Value("${load.funds.input.file}") String INPUT_FILE,
                            @Value("${load.funds.output.file}") String OUTPUT_FILE
    ) {
        this.loadCustomerFundsRepository = loadCustomerFundsRepository;
        this.objectMapper = objectMapper;
        this.VELOCITY_LIMITS_PER_DAY_FUND = VELOCITY_LIMITS_PER_DAY_FUND;
        this.VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT = VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT;
        this.VELOCITY_LIMITS_PER_WEEK_FUND = VELOCITY_LIMITS_PER_WEEK_FUND;
        this.INPUT_FILE = INPUT_FILE;
        this.OUTPUT_FILE = OUTPUT_FILE;
    }

    public void executeFundsLoadingToAccounts() {
        LOGGER.info("Started executing fund loading into accounts.");
        // read transactions data
        List<LoadFunds> loadFundsList = readLoadFundTransactionData();

        // Iterate and verify velocity limits
        List<LoadFundsAttempt> loadFundsAttemptList = new ArrayList<>();
        LOGGER.info("Verifying velocity limits for loads funds transactions data of size: {}", loadFundsList.size());
        for (LoadFunds loadFund : loadFundsList) {
            LoadFundsAttempt loadFundsAttempt = verifyVelocityLimits(loadFund);
            loadFundsAttemptList.add(loadFundsAttempt);
        }
        // write the fund loading attempts info into output file
        FileWriterUtil.writeLoadFundAttemptsToFile(OUTPUT_FILE, loadFundsAttemptList);
    }

    /**
     * This method verifies the velocity limits for each load fund transaction.
     * If the velocity limits are met, the transaction is accepted and saved in the database.
     * If the velocity limits are not met, the transaction attempt is rejected.
     *
     * @param loadCustomerFund - the load customer fund information for the transaction
     * @return LoadFundsAttempt - the load funds attempt for the transaction
     */
    private LoadFundsAttempt verifyVelocityLimits(LoadFunds loadCustomerFund) {
        LoadFundsAttempt loadFundsAttempt = objectMapper.convertValue(loadCustomerFund, LoadFundsAttempt.class);
        try {
            // verify velocity limits
            verifyCustomerMaxLoadAmountLimitPerDay(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime(), loadCustomerFund.getLoadAmount());
            verifyCustomerNoOfLoadsInAWeek(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime(), loadCustomerFund.getLoadAmount());

            // save loaded fund transaction in DB
            LoadedCustomerFundsEntity loadedCustomerFundsEntity = objectMapper.convertValue(loadCustomerFund, LoadedCustomerFundsEntity.class);
            loadCustomerFundsRepository.save(loadedCustomerFundsEntity);
            loadFundsAttempt.setAccepted(true);
            LOGGER.info("Load fund transaction with id: {} has been accepted. \nAttempt Output: {}", loadCustomerFund.getId(), FileWriterUtil.getJSONStringFromObj(loadFundsAttempt));
        } catch (VelocityLimitException vle) {
            loadFundsAttempt.setAccepted(false);
            LOGGER.info("Load fund transaction with id: {} has been rejected. \nAttempt Output: {}", loadCustomerFund.getId(), FileWriterUtil.getJSONStringFromObj(loadFundsAttempt));
        }
        return loadFundsAttempt;
    }

    /**
     * This method verifies that each load fund transaction satisfies the following velocity limits conditions: <p>
     * 1. A maximum of $5000 per day for each customer account. <p>
     * 2. A maximum of 3 load transactions per day for each customer account.
     *
     * @param customerId - the ID of the customer for the transaction
     * @param loadTime   - the time of the load transaction
     * @param loadAmount - the amount of the load transaction
     * @throws VelocityLimitException - an exception thrown when the velocity limits are not satisfied
     */
    public void verifyCustomerMaxLoadAmountLimitPerDay(Long customerId, LocalDateTime loadTime, double loadAmount) throws VelocityLimitException {
        LocalDateTime startDate = DateTimeUtil.getStartOfDay(loadTime);
        LocalDateTime endDate = DateTimeUtil.getEndOfDay(loadTime);
        List<LoadedCustomerFundsEntity> customerPreviousLoadsInADayList = loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, startDate, endDate);
        if (customerPreviousLoadsInADayList.size() >= VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT) {
            LOGGER.error("Fund Load Transactions Limit per Day exceeded for customerId: {}", customerId);
            throw new VelocityLimitException("Fund Load Transactions Limit per Day exceeded.");
        }
        double totalLoadInADay = 0.0;
        for (LoadedCustomerFundsEntity customerFunds : customerPreviousLoadsInADayList) {
            totalLoadInADay += customerFunds.getLoadAmount();
        }
        if (totalLoadInADay + loadAmount > VELOCITY_LIMITS_PER_DAY_FUND) {
            LOGGER.error("Fund Load Amount Limit per Day exceeded for customerId: {}", customerId);
            throw new VelocityLimitException("Fund Load Amount Limit per Day exceeded.");
        }
    }

    /**
     * This method verifies that each load fund transaction satisfies the following velocity limits conditions: <p>
     * 1. A maximum of $20,000 per week for each customer account.
     *
     * @param customerId - the ID of the customer for the transaction
     * @param loadTime   - the time of the load transaction
     * @param loadAmount - the amount of the load transaction
     * @throws VelocityLimitException - an exception thrown when the velocity limits are not satisfied
     */
    public void verifyCustomerNoOfLoadsInAWeek(Long customerId, LocalDateTime loadTime, double loadAmount) throws VelocityLimitException {
        LocalDateTime weekStart = DateTimeUtil.getStartOfWeek(loadTime);
        LocalDateTime weekEnd = DateTimeUtil.getEndOfWeek(loadTime);
        List<LoadedCustomerFundsEntity> loadCustomerFundsWeekList =
                loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, weekStart, weekEnd);
        double totalLoadInAWeek = 0.0;
        for (LoadedCustomerFundsEntity customerFunds : loadCustomerFundsWeekList) {
            totalLoadInAWeek += customerFunds.getLoadAmount();
        }
        if (totalLoadInAWeek + loadAmount > VELOCITY_LIMITS_PER_WEEK_FUND) {
            LOGGER.error("Fund Load Amount Limit per week exceeded for customerId: {}", customerId);
            throw new VelocityLimitException("Fund Load Amount Limit per week exceeded.");
        }
    }

    /**
     * This method reads the `input.txt` load funds transactions data file using the `FileReaderUtil`,
     * and reads each of the string data into `LoadFunds` POJOs by filtering only the first occurrence
     * of `CustomerId` and `LoadId`.
     *
     * @return List<LoadFunds> - a list of LoadFunds POJOs containing the filtered load funds transactions
     */
    private List<LoadFunds> readLoadFundTransactionData() {
        LOGGER.info("Reading and converting load funds transactions data.");
        List<String> inputFileLinesList = FileReaderUtil.readCustomerLoadFundsFromInputFile(INPUT_FILE);
        Set<String> customerLoadIdsSet = new HashSet<>();
        return inputFileLinesList.stream().map(
                loadFundJsonTxt -> {
                    try {
                        return objectMapper.readValue(loadFundJsonTxt, LoadFunds.class);
                    } catch (JsonProcessingException jpe) {
                        LOGGER.error("Failed to readValue into LoadFunds object for following data: {}", loadFundJsonTxt);
                        throw new LoadFundsException("Unable to read transaction text into an object." + jpe.getMessage());
                    }
                }
        ).filter(loadFunds -> customerLoadIdsSet.add(loadFunds.getCustomerId() + SLASH + loadFunds.getId())).collect(Collectors.toList());
    }
}
