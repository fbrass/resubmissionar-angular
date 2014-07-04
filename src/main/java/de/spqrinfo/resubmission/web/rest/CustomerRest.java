package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionService;
import de.spqrinfo.resubmission.web.rest.dto.CustomerDetailDto;
import de.spqrinfo.resubmission.web.rest.dto.CustomerDto;
import de.spqrinfo.resubmission.web.rest.dto.CustomerPaginatedDto;
import de.spqrinfo.resubmission.web.rest.dto.ResubmissionDto;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/customers/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class CustomerRest {

    private static final Logger log = Logger.getLogger(CustomerRest.class.getName());

    private static final int PAGE_SIZE_MAX = 2000;

    @Inject
    private ResubmissionService resubmissionService;

    @GET
    @Path("{id}")
    public CustomerDetailDto getCustomer(@PathParam("id") final long id) {
        final Customer c = this.resubmissionService.getCustomer(id);
        return toDetail(c);
    }

    @GET
    @Path("{pageSize}/{page}")
    public CustomerPaginatedDto getAll(@PathParam("pageSize") final Integer pageSize,
                                       @PathParam("page") final Integer page) {
        log.log(INFO, "getAll pageSize {0}, page {1}", new Object[]{ pageSize, page });

        if (pageSize == null && page != null
                || pageSize != null && page == null) {
            throw new IllegalArgumentException("pageSize goes with page parameter");
        }

        if (pageSize != null && (pageSize < 1 || pageSize > PAGE_SIZE_MAX)) {
            throw new IllegalArgumentException("pageSize is invalid");
        }

        if (page != null && page < 1) {
            throw new IllegalArgumentException("page is invalid");
        }

        final long customerCount = this.resubmissionService.getCustomerCount();

        final List<CustomerDto> customers;
        customers = this.resubmissionService.getCustomers(pageSize, page).stream().map(CustomerRest::to).collect(Collectors.toList());

        return new CustomerPaginatedDto(customerCount, customers);
    }

    @GET
    @Path("{pageSize}/{page}/{searchText}")
    public CustomerPaginatedDto getAll(@PathParam("pageSize") final Integer pageSize,
                                       @PathParam("page") final Integer page,
                                       @PathParam("searchText") final String searchText) {
        log.log(INFO, "getAll pageSize {0}, page {1}, searchText {2}", new Object[]{ pageSize, page, searchText});

        if (pageSize == null && page != null
                || pageSize != null && page == null) {
            throw new IllegalArgumentException("pageSize goes with page parameter");
        }

        if (pageSize != null && (pageSize < 1 || pageSize > PAGE_SIZE_MAX)) {
            throw new IllegalArgumentException("pageSize is invalid");
        }

        if (page != null && page < 1) {
            throw new IllegalArgumentException("page is invalid");
        }

        final long customerCount = this.resubmissionService.getCustomerCount();

        final List<CustomerDto> customers;
        customers = this.resubmissionService.getCustomers(pageSize, page, searchText).stream().map(CustomerRest::to).collect(Collectors.toList());

        return new CustomerPaginatedDto(customerCount, customers);
    }

    @POST
    public String save(final CustomerDto customer) {
        if (customer.getId() == null) { // create
            final Customer c = new Customer();
            c.setCompanyName(customer.getCompanyName());
            c.setDescription(customer.getDescription());
            final Customer persistedCustomer = this.resubmissionService.createCustomer(c, customer.getLogoId());
            final JsonObjectBuilder builder = Json.createObjectBuilder().add("customerId", persistedCustomer.getCustomerId());
            return builder.build().toString();
        } else { // update
            throw new RuntimeException("Not yet");
        }
    }

    private static CustomerDetailDto toDetail(final Customer customer) {
        if (customer == null) {
            return null;
        }
        final CustomerDetailDto r = new CustomerDetailDto();
        r.setId(customer.getCustomerId());
        r.setCompanyName(customer.getCompanyName());
        r.setDescription(customer.getDescription());
        if (customer.hasLogo()) {
            r.setImageUrl("file?kind=customerLogo&customer=" + customer.getCustomerId());
        }
        final List<ResubmissionDto> resubmissionRestList;
        resubmissionRestList = customer.getResubmissions().stream().map(CustomerRest::to).collect(Collectors.toList());
        r.setResubmissions(resubmissionRestList.toArray(new ResubmissionDto[resubmissionRestList.size()]));
        return r;
    }

    private static ResubmissionDto to(final Resubmission resub) {
        if (resub == null) {
            return null;
        }
        final ResubmissionDto r = new ResubmissionDto();
        r.setCustomerId(resub.getCustomer().getCustomerId());
        r.setId(resub.getResubmissionId());
        r.setNote(resub.getNote());
        r.setDue(resub.getDue());
        r.setActive(resub.isActive());
        return r;
    }

    private static CustomerDto to(final Customer c) {
        if (c == null) {
            return null;
        }
        final CustomerDto r = new CustomerDto();
        r.setId(c.getCustomerId());
        r.setCompanyName(c.getCompanyName());
        r.setDescription(c.getDescription());
        return r;
    }
}
