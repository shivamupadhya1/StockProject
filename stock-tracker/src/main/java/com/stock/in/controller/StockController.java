package com.stock.in.controller;

import com.stock.in.dto.CurrencyExchangeDTO;
import com.stock.in.dto.DailyStockDataDTO;
import com.stock.in.dto.StockDTO;
import com.stock.in.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Fetch stock price for a given symbol")
    @GetMapping("/fetch")
    public StockDTO fetchStock(@RequestParam String symbol) {
        return stockService.fetchStockPrice(symbol);
    }

    @Operation(summary = "Get last 10 fetched stock prices")
    @GetMapping("/history")
    public List<StockDTO> getStockHistory() {
        return List.copyOf(stockService.getRecentStockPrices());
    }
    @Operation(summary = "API from Currency Conversion")
    @GetMapping("/currency")
    public ResponseEntity<CurrencyExchangeDTO> getCurrencyRate(
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(stockService.fetchCurrencyExchangeRate(from, to));
    }
    
    @Operation(summary = "Fetch stock price for a given symbol for Daily")
    @GetMapping("/stock/daily")
    public ResponseEntity<List<DailyStockDataDTO>> getDailyStockData(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "compact") String outputsize) {

        return ResponseEntity.ok(stockService.fetchDailyTimeSeries(symbol, outputsize));
    }


}
