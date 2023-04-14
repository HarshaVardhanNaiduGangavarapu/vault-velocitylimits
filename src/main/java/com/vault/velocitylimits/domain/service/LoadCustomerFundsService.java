package com.vault.velocitylimits.domain.service;

import com.vault.velocitylimits.domain.model.LoadCustomerFunds;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import com.vault.velocitylimits.domain.repository.ILoadCustomerFundsRepository;
import com.vault.velocitylimits.domain.repository.LoadCustomerFundsEntity;
import com.vault.velocitylimits.domain.util.FileReaderUtil;
import com.vault.velocitylimits.domain.util.FileWriterUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author harshavardhannaidugangavarapu
 */
@Service
public class LoadCustomerFundsService {
    private ILoadCustomerFundsRepository loadCustomerFundsRepository;
    private ModelMapper modelMapper;

    @Autowired
    public LoadCustomerFundsService(ILoadCustomerFundsRepository loadCustomerFundsRepository, ModelMapper modelMapper) {
        this.loadCustomerFundsRepository = loadCustomerFundsRepository;
        this.modelMapper = modelMapper;
    }

    public LoadCustomerFundsService() {
    }

    public void loadFunds() {
        List<LoadCustomerFunds> loadCustomerFundsJSONList = getCustomerLoadFundsData();
        List<LoadFundsAttempt> loadFundsAttemptList = new ArrayList<>();
        for(LoadCustomerFunds loadCustomerFund: loadCustomerFundsJSONList){
            LoadFundsAttempt loadFundsAttempt = verifyVelocityLimits(loadCustomerFund);
            loadFundsAttemptList.add(loadFundsAttempt);
        }
        FileWriterUtil.writeLoadFundAttemptsToFile(loadFundsAttemptList);
    }

    private LoadFundsAttempt verifyVelocityLimits(LoadCustomerFunds loadCustomerFund) {
       boolean isLoadInlineWithVelocityLimits= verifyCustomerMaxLoadAmountLimitPerDay(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime())
               && verifyCustomerNoOfLoadsInAWeek(loadCustomerFund.getCustomerId(), loadCustomerFund.getTime());
        LoadFundsAttempt loadFundsAttempt = modelMapper.map(loadCustomerFund, LoadFundsAttempt.class);
       if(isLoadInlineWithVelocityLimits){
           LoadCustomerFundsEntity loadCustomerFundsEntity = modelMapper.map(loadCustomerFund, LoadCustomerFundsEntity.class);
           loadCustomerFundsRepository.save(loadCustomerFundsEntity);
           loadFundsAttempt.setAccepted(true);
       }else{
           //log
           loadFundsAttempt.setAccepted(false);
       }
       return loadFundsAttempt;
    }

    public boolean verifyCustomerMaxLoadAmountLimitPerDay(Long customerId, LocalDateTime time) {
        LocalDate localDate = time.toLocalDate();
        LocalDateTime startDate = LocalDateTime.of(localDate, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime endDate = LocalDateTime.of(localDate, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
        List<LoadCustomerFunds> customerPreviousLoadsInADayList = loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, startDate, endDate);
        if(customerPreviousLoadsInADayList.size()>3){
            return false;
        }
        double totalLoadInADay = 0.0;
        for(LoadCustomerFunds customerFunds: customerPreviousLoadsInADayList){
            totalLoadInADay+= customerFunds.getLoadAmount();
        }
        return totalLoadInADay<=5000;
    }

    public boolean verifyCustomerNoOfLoadsInAWeek(Long customerId, LocalDateTime time){
        LocalDate localDate = time.toLocalDate();
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime weekStart = LocalDateTime.of(monday, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime weekEnd = LocalDateTime.of(sunday, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
        List<LoadCustomerFunds> loadCustomerFundsWeekList = loadCustomerFundsRepository.findAllByCustomerIdAndTimeBetween(customerId, weekStart, weekEnd);
        double totalLoadInAWeek = 0.0;
        for(LoadCustomerFunds customerFunds: loadCustomerFundsWeekList){
            totalLoadInAWeek+= customerFunds.getLoadAmount();
        }
        return totalLoadInAWeek<=20000;
    }

    private List<LoadCustomerFunds> getCustomerLoadFundsData() {
        List<String> inputFileLinesList = FileReaderUtil.readCustomerLoadFundsFromInputFile();
        return inputFileLinesList.stream().map(
                loadFundJsonTxt -> modelMapper.map(loadFundJsonTxt, LoadCustomerFunds.class)
        ).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LocalDate localDate = LocalDate.parse("2000-01-01T00:00:00Z", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime startDate = LocalDateTime.of(localDate, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime endDate = LocalDateTime.of(localDate, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime weekStart = LocalDateTime.of(monday, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime weekEnd = LocalDateTime.of(sunday, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
        System.out.println("StartDate: "+startDate);
        System.out.println("EndDate: "+endDate);
        System.out.println("WeekStart: "+weekStart);
        System.out.println("WeekEnd: "+weekEnd);
    }

}
