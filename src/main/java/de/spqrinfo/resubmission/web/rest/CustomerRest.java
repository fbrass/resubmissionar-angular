package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionService;
import de.spqrinfo.resubmission.web.rest.dto.CustomerDetailDto;
import de.spqrinfo.resubmission.web.rest.dto.CustomerDto;
import de.spqrinfo.resubmission.web.rest.dto.ResubmissionDto;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/customers/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class CustomerRest {

    @Inject
    private ResubmissionService resubmissionService;

    @GET
    @Path("{id}")
    public CustomerDetailDto getCustomer(@PathParam("id") final long id) {
        final Customer c = this.resubmissionService.getCustomer(id);
        return toDetail(c);
    }

    @GET
    public CustomerDto[] getAll() {
        final List<CustomerDto> customerRestList = new ArrayList<>();
        for (final Customer c : this.resubmissionService.getCustomers()) {
            final CustomerDto customerRest = to(c);
            customerRestList.add(customerRest);
        }
        return customerRestList.toArray(new CustomerDto[customerRestList.size()]);
    }

    @POST
    public String save(final CustomerDto customer) {
        if (customer.getId() == null) { // create
            final Customer c = new Customer();
            c.setCompanyName(customer.getCompanyName());
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
        if (customer.hasLogo()) {
            r.setImageUrl("file?kind=customerLogo&customer=" + customer.getCustomerId());
        }
        r.setDescription("Some description of the customer");
        final List<ResubmissionDto> resubmissionRestList = new ArrayList<>();
        for (final Resubmission resubmission : customer.getResubmissions()) {
            resubmissionRestList.add(to(resubmission));
        }
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
        return r;
    }
}
