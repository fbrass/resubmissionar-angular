package de.spqrinfo.resubmission.web.rest.dto;

import java.util.List;

public class CustomerPaginatedDto {

    private final long total;
    private final List<CustomerDto> customers;

    public CustomerPaginatedDto(final long total, final List<CustomerDto> customers) {
        this.total = total;
        this.customers = customers;
    }

    public long getTotal() {
        return this.total;
    }

    public List<CustomerDto> getCustomers() {
        return this.customers;
    }
}
