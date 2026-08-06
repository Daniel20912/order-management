package com.danieloliveira.order_management.repository.projection;

import java.math.BigDecimal;

public interface ResumoDiarioProjection {

    Long getQuantidade();

    BigDecimal getValorTotal();
}
