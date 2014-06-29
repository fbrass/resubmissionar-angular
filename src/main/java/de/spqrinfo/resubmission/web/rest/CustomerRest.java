package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionFacade;
import de.spqrinfo.resubmission.web.rest.dto.CustomerDetailRestDto;
import de.spqrinfo.resubmission.web.rest.dto.CustomerRestDto;
import de.spqrinfo.resubmission.web.rest.dto.ResubmissionRestDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/customers/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerRest {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @GET
    @Path("{id}")
    public CustomerDetailRestDto getCustomer(@PathParam("id") final long id) {
        final Customer c = this.resubmissionFacade.getCustomer(id);
        return toDetail(c);
    }

    @GET
    public CustomerRestDto[] getAll() {
        final List<CustomerRestDto> customerRestList = new ArrayList<>();
        for (final Customer c : this.resubmissionFacade.getCustomers()) {
            final CustomerRestDto customerRest = to(c);
            customerRestList.add(customerRest);
        }
        return customerRestList.toArray(new CustomerRestDto[customerRestList.size()]);
    }

    @POST
    public void save(final CustomerRestDto customer) {
        if (customer.getId() == null) {
            // create
            final Customer c = new Customer();
            c.setCompanyName(customer.getCompanyName());
            this.resubmissionFacade.createCustomer(c);
        } else {
            // update
            throw new RuntimeException("Not yet");
        }
    }

    private static CustomerDetailRestDto toDetail(final Customer customer) {
        if (customer == null) {
            return null;
        }
        final CustomerDetailRestDto r = new CustomerDetailRestDto();
        r.setId(customer.getCustomerId());
        r.setCompanyName(customer.getCompanyName());
        if (customer.hasLogo()) {
            r.setImageUrl("image?customer=" + customer.getCustomerId());
        }
        r.setDescription("Some description of the customer");
        final List<ResubmissionRestDto> resubmissionRestList = new ArrayList<>();
        for (final Resubmission resubmission : customer.getResubmissions()) {
            resubmissionRestList.add(to(resubmission));
        }
        r.setResubmissions(resubmissionRestList.toArray(new ResubmissionRestDto[resubmissionRestList.size()]));
        return r;
    }

    private static ResubmissionRestDto to(final Resubmission resub) {
        if (resub == null) {
            return null;
        }
        final ResubmissionRestDto r = new ResubmissionRestDto();
        r.setCustomerId(resub.getCustomer().getCustomerId());
        r.setId(resub.getResubmissionId());
        r.setNote(resub.getNote());
        r.setDue(resub.getDue());
        r.setActive(resub.isActive());
        return r;
    }

    private static CustomerRestDto to(final Customer c) {
        if (c == null) {
            return null;
        }
        final CustomerRestDto r = new CustomerRestDto();
        r.setId(c.getCustomerId());
        r.setCompanyName(c.getCompanyName());
        return r;
    }
}
