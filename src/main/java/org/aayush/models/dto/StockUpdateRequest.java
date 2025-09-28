package org.aayush.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateRequest {
    @NotNull(message = "Stock amount is required")
    @Min(value = 1, message = "Stock amount must be at least 1")
    private Integer amount;
}
