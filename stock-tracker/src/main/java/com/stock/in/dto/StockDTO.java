package com.stock.in.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StockDTO {
    private String symbol;
    private BigDecimal price;
    private LocalDateTime timestamp;
}
