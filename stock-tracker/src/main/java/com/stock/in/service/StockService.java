package com.stock.in.service;

import com.stock.in.dto.StockDTO;
import com.stock.in.dto.CurrencyExchangeDTO;
import com.stock.in.dto.DailyStockDataDTO;
import com.stock.in.exception.StockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class StockService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Queue<StockDTO> stockQueue = new ConcurrentLinkedQueue<>();

    @Value("${alphavantage.api.key}")
    private String apiKey;

    public StockDTO fetchStockPrice(String symbol) {
        try {
            log.info("Fetching stock price for {}", symbol);

            String url = UriComponentsBuilder.fromHttpUrl("https://www.alphavantage.co/query")
                    .queryParam("function", "GLOBAL_QUOTE")  // ✅ Using Global Quote API for accurate price
                    .queryParam("symbol", symbol)
                    .queryParam("apikey", apiKey)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("Full API response: {}", response);

            if (response == null || !response.containsKey("Global Quote")) {
                log.error("Invalid response from Alpha Vantage API: {}", response);
                throw new StockException("Failed to fetch stock price for " + symbol);
            }

            Map<String, String> quoteData = (Map<String, String>) response.get("Global Quote");
            BigDecimal price = new BigDecimal(quoteData.get("05. price"));

            StockDTO stockDTO = new StockDTO(symbol, price, LocalDateTime.now());
            stockQueue.add(stockDTO);

            if (stockQueue.size() > 10) {
                stockQueue.poll();
            }

            return stockDTO;
        } catch (Exception e) {
            log.error("Error fetching stock price: {}", e.getMessage());
            throw new StockException("Error fetching stock price");
        }
    }

    public Queue<StockDTO> getRecentStockPrices() {
        return stockQueue;
    }
    public CurrencyExchangeDTO fetchCurrencyExchangeRate(String fromCurrency , String toCurrency) {
    	
    	try {
    		log.info("Fetching currency exchange rate from {} to {}", fromCurrency, toCurrency);
    		String url = UriComponentsBuilder.fromHttpUrl("https://www.alphavantage.co/query")
    				.queryParam("function", "CURRENCY_EXCHANGE_RATE")
    				.queryParam("from_currency", fromCurrency)
                    .queryParam("to_currency", toCurrency)
                    .queryParam("apikey", apiKey)
                    .toUriString();
    		
    		Map<String, Object> response =restTemplate.getForObject(url, Map.class);
    		log.info("Full Currency API response: {}", response);
    		
    		if (response == null || !response.containsKey("Realtime Currency Exchange Rate")) {
                log.error("Invalid response from Alpha Vantage Currency API: {}", response);
                throw new StockException("Failed to fetch currency exchange rate from " + fromCurrency + " to " + toCurrency);
            }
    		Map<String, String> exchangeData = (Map<String, String>) response.get("Realtime Currency Exchange Rate");
    		
    		CurrencyExchangeDTO dto = new CurrencyExchangeDTO();
            dto.setFromCurrencyCode(exchangeData.get("1. From_Currency Code"));
            dto.setFromCurrencyName(exchangeData.get("2. From_Currency Name"));
            dto.setToCurrencyCode(exchangeData.get("3. To_Currency Code"));
            dto.setToCurrencyName(exchangeData.get("4. To_Currency Name"));
            dto.setExchangeRate(exchangeData.get("5. Exchange Rate"));
            dto.setLastRefreshed(exchangeData.get("6. Last Refreshed"));
            dto.setTimeZone(exchangeData.get("7. Time Zone"));
            dto.setBidPrice(exchangeData.get("8. Bid Price"));
            dto.setAskPrice(exchangeData.get("9. Ask Price"));

            return dto;
    	}catch (Exception e) {
            log.error("Error fetching currency exchange rate: {}", e.getMessage());
            throw new StockException("Error fetching currency exchange rate");
        }
    }
    public List<DailyStockDataDTO> fetchDailyTimeSeries(String symbol, String outputSize) {
        try {
            log.info("Fetching daily stock time series for {}", symbol);

            String url = UriComponentsBuilder.fromHttpUrl("https://www.alphavantage.co/query")
                    .queryParam("function", "TIME_SERIES_DAILY")
                    .queryParam("symbol", symbol)
                    .queryParam("outputsize", outputSize)  // "compact" or "full"
                    .queryParam("datatype", "json")
                    .queryParam("apikey", apiKey)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.debug("Time Series response: {}", response);

            if (response == null || !response.containsKey("Time Series (Daily)")) {
                throw new StockException("Invalid response for TIME_SERIES_DAILY");
            }

            Map<String, Map<String, String>> timeSeries = (Map<String, Map<String, String>>) response.get("Time Series (Daily)");

            List<DailyStockDataDTO> dailyData = new ArrayList<>();

            for (Map.Entry<String, Map<String, String>> entry : timeSeries.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey());
                Map<String, String> dailyValues = entry.getValue();

                DailyStockDataDTO dto = new DailyStockDataDTO();
                dto.setDate(date);
                dto.setOpen(new BigDecimal(dailyValues.get("1. open")));
                dto.setHigh(new BigDecimal(dailyValues.get("2. high")));
                dto.setLow(new BigDecimal(dailyValues.get("3. low")));
                dto.setClose(new BigDecimal(dailyValues.get("4. close")));
                dto.setVolume(Long.parseLong(dailyValues.get("5. volume")));

                dailyData.add(dto);
            }

            return dailyData;

        } catch (Exception e) {
            log.error("Error fetching daily stock data: {}", e.getMessage());
            throw new StockException("Failed to fetch daily stock data");
        }
    }

}
