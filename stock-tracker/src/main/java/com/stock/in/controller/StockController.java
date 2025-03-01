package com.stock.in.controller;

import com.stock.in.dto.StockDTO;
import com.stock.in.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
}
