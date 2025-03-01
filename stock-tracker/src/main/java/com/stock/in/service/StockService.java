package com.stock.in.service;

import com.stock.in.dto.StockDTO;
import com.stock.in.exception.StockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
}
