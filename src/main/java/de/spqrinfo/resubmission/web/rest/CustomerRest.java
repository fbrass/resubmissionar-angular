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
import javax.transaction.Transactional;
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

    private static final int PAGE_SIZE_MAX = 4000; // Arbitrarily chosen

    @Inject
    private ResubmissionService resubmissionService;

    @GET
    @Path("{id}")
    public CustomerDetailDto getCustomer(@PathParam("id") final long id) {
        final Customer c = this.resubmissionService.getCustomer(id);
        return toDetail(c);
    }

    @DELETE
    @Path("{id}")
    public void deleteCustomer(@PathParam("id") final long id) {
        this.resubmissionService.deleteCustomer(id);
    }

    @GET
    @Path("{pageSize}/{page}")
    public CustomerPaginatedDto getAll(@PathParam("pageSize") final Integer pageSize,
                                       @PathParam("page") final Integer page) {
        return getPaginated(pageSize, page, null);
    }

    @GET
    @Path("{pageSize}/{page}/{searchText}")
    public CustomerPaginatedDto getAll(@PathParam("pageSize") final Integer pageSize,
                                       @PathParam("page") final Integer page,
                                       @PathParam("searchText") final String searchText) {
        return getPaginated(pageSize, page, searchText);
    }

    private CustomerPaginatedDto getPaginated(final Integer pageSize, final Integer page, final String searchText) {
        log.log(INFO, "getPaginatedAll pageSize {0}, page {1}, searchText {2}", new Object[]{ pageSize, page, searchText});

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

        final List<Customer> customers;
        if (searchText == null || searchText.isEmpty()) {
            customers = this.resubmissionService.getCustomers(pageSize, page);
        } else {
            customers = this.resubmissionService.getCustomers(pageSize, page, searchText);
        }

        final List<CustomerDto> tmp = customers.stream().map(CustomerRest::to).collect(Collectors.toList());
        return new CustomerPaginatedDto(this.resubmissionService.getCustomerCount(), tmp);
    }

    @POST
    @Transactional
    public String save(final CustomerDto customer) {
        if (customer.getId() == null) { // create
            final Customer c = new Customer();
            c.setCompanyName(customer.getCompanyName());
            c.setDescription(customer.getDescription());
            final Customer persistedCustomer = this.resubmissionService.createCustomer(c, customer.getLogoId());
            final JsonObjectBuilder builder = Json.createObjectBuilder().add("customerId", persistedCustomer.getCustomerId());
            return builder.build().toString();
        } else { // update
            final Customer currentCustomer = this.resubmissionService.getCustomer(customer.getId());
            if (currentCustomer.hasLogo()
                    && (!customer.getLogoId().equals(currentCustomer.getLogo().getUploadId())
                        || customer.getLogoId() == null)) {
                // Old logo to be removed
                this.resubmissionService.deleteCustomerLogo(currentCustomer);
            }

            currentCustomer.setCompanyName(customer.getCompanyName());
            currentCustomer.setDescription(customer.getDescription());
            this.resubmissionService.updateCustomer(currentCustomer, customer.getLogoId());
            final JsonObjectBuilder builder = Json.createObjectBuilder().add("customerId", currentCustomer.getCustomerId());
            return builder.build().toString();
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
            r.setLogoId(customer.getLogo().getUploadId());
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
