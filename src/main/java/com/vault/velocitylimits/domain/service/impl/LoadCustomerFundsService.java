package com.vault.velocitylimits.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.model.LoadCustomerFunds;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import com.vault.velocitylimits.domain.repository.ILoadCustomerFundsRepository;
import com.vault.velocitylimits.domain.repository.LoadCustomerFundsEntity;
import com.vault.velocitylimits.domain.service.ILoadCustomerFundsService;
import com.vault.velocitylimits.domain.service.LoadFundsException;
import com.vault.velocitylimits.domain.service.VelocityLimitException;
import com.vault.velocitylimits.domain.util.DateTimeUtil;
import com.vault.velocitylimits.domain.util.FileReaderUtil;
import com.vault.velocitylimits.domain.util.FileWriterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author harshagangavarapu
 */
@Service
public class LoadCustomerFundsService implements ILoadCustomerFundsService {
    @Value("${velocitylimits.perday.fund.limit}")
    private Double VELOCITY_LIMITS_PER_DAY_FUND;
    @Value("${velocitylimits.perday.transactions.limit}")
    private int VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT;
    @Value("${velocitylimits.perweek.fund.limit}")
    private int VELOCITY_LIMITS_PER_WEEK_FUND;
    @Value("${load.funds.input.file}")
    private String INPUT_FILE;
    @Value("${load.funds.output.file}")
    private String OUTPUT_FILE;
    private ILoadCustomerFundsRepository loadCustomerFundsRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public LoadCustomerFundsService(ILoadCustomerFundsRepository loadCustomerFundsRepository, ObjectMapper objectMapper) {
        this.loadCustomerFundsRepository = loadCustomerFundsRepository;
        this.objectMapper = objectMapper;
    }

    public void executeFundsLoadingToAccounts() {
        // read transactions data
        List<LoadCustomerFunds> loadCustomerFundsJSONList = readLoadFundTransactionData();

        // Iterate and verify velocity limits
        List<LoadFundsAttempt> loadFundsAttemptList = new ArrayList<>();
        for(LoadCustomerFunds loadCustomerFund: loadCustomerFundsJSONList){
            LoadFundsAttempt loadFundsAttempt = verifyVelocityLimits(loadCustomerFund);
            loadFundsAttemptList.add(loadFundsAttempt);
        }
        // write the fund loading attempts info into output file
        FileWriterUtil.writeLoadFundAttemptsToFile(OUTPUT_FILE, loadFundsAttemptList);
    }

    /**
     * This method verifies the velocity limits for each load fund transaction.
     * If velocity limits are satisfied then accept the transaction and save it in DB
     * else reject the transaction attempt
     *
     * @param loadCustomerFund
     * @return LoadFundsAttempt
     */
    private LoadFundsAttempt verifyVelocityLimits(LoadCustomerFunds loadCustomerFund) {
        LoadFundsAttempt loadFundsAttempt = objectMapper.convertValue(loadCustomerFund, LoadFundsAttempt.class);
        try{
            // verify velocity limits
            verifyCustomerMaxLoadAmountLimitPerDay(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime(), loadCustomerFund.getLoadAmount());
            verifyCustomerNoOfLoadsInAWeek(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime(), loadCustomerFund.getLoadAmount());

            // save load fund transaction in DB
            LoadCustomerFundsEntity loadCustomerFundsEntity = objectMapper.convertValue(loadCustomerFund, LoadCustomerFundsEntity.class);
            loadCustomerFundsRepository.saveAndFlush(loadCustomerFundsEntity);
            loadFundsAttempt.setAccepted(true);
        }
        catch(VelocityLimitException vle){
            loadFundsAttempt.setAccepted(false);
        }
        return loadFundsAttempt;
    }

    /**
     * This method verifies each load fund transaction satisfies the following velocity limits conditions: <p>
     *     1. A maximum of $5000 per day for each customer account. <p>
     *     2. A maximum of 3 load transaction per day for each customer account. <p>
     *
     * @param customerId
     * @param loadTime
     * @param loadAmount
     * @throws VelocityLimitException
     */
    public void verifyCustomerMaxLoadAmountLimitPerDay(Long customerId, LocalDateTime loadTime, double loadAmount) throws VelocityLimitException {
        LocalDateTime startDate = DateTimeUtil.getStartOfDay(loadTime);
        LocalDateTime endDate = DateTimeUtil.getEndOfDay(loadTime);
        List<LoadCustomerFundsEntity> customerPreviousLoadsInADayList = loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, startDate, endDate);
        if (customerPreviousLoadsInADayList.size() > VELOCITY_LIMITS_PER_DAY_TRANSACTIONS_LIMIT) {
            throw new VelocityLimitException("Fund Load Transactions Limit per Day exceeded.");
        }
        double totalLoadInADay = 0.0;
        for (LoadCustomerFundsEntity customerFunds : customerPreviousLoadsInADayList) {
            totalLoadInADay += customerFunds.getLoadAmount();
        }
        if (totalLoadInADay + loadAmount > VELOCITY_LIMITS_PER_DAY_FUND) {
            throw new VelocityLimitException("Fund Load Amount Limit per Day exceeded.");
        }
    }

    /**
     * This method verifies each load fund transaction satisfies the following velocity limits conditions: <p>
     *     1. A maximum of $20,000 per week for each customer account. <p>
     * @param customerId
     * @param loadTime
     * @param loadAmount
     * @throws VelocityLimitException
     */
    public void verifyCustomerNoOfLoadsInAWeek(Long customerId, LocalDateTime loadTime, double loadAmount) throws VelocityLimitException {
        LocalDateTime weekStart = DateTimeUtil.getStartOfWeek(loadTime);
        LocalDateTime weekEnd = DateTimeUtil.getEndOfWeek(loadTime);
        List<LoadCustomerFundsEntity> loadCustomerFundsWeekList = loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, weekStart, weekEnd);
        double totalLoadInAWeek = 0.0;
        for(LoadCustomerFundsEntity customerFunds: loadCustomerFundsWeekList){
            totalLoadInAWeek+= customerFunds.getLoadAmount();
        }
        if( totalLoadInAWeek+loadAmount>VELOCITY_LIMITS_PER_WEEK_FUND){
            throw new VelocityLimitException("Fund Load Amount Limit per week exceeded.");
        }
    }

    /**
     * This method read the `input.txt` load funds transactions data file utilizing `FileReaderUtil` and reads each of the
     * string data into `LoadCustomerFunds` POJO.
     *
     * @return List<LoadCustomerFunds>
     */
    private List<LoadCustomerFunds> readLoadFundTransactionData() {
        List<String> inputFileLinesList = FileReaderUtil.readCustomerLoadFundsFromInputFile(INPUT_FILE);
        return inputFileLinesList.stream().map(
                loadFundJsonTxt -> {
                    try {
                        return objectMapper.readValue(loadFundJsonTxt, LoadCustomerFunds.class);
                    } catch (JsonProcessingException jpe) {
                        throw new LoadFundsException("Unable to read transaction text into an object." + jpe.getMessage());
                    }
                }
        ).collect(Collectors.toList());
    }
}
