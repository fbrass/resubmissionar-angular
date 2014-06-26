package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/customers/")
@Stateless
@LocalBean
public class CustomerRestBean {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerRest getCustomer(@PathParam("id") final long id) {
        final Customer c = this.resubmissionFacade.getCustomer(id);
        return toDetail(c);
    }

    private static CustomerDetailRest toDetail(final Customer c) {
        if (c == null) {
            return null;
        }
        final CustomerDetailRest r = new CustomerDetailRest();
        r.setId(c.getCustomerId());
        r.setCompanyName(c.getCompanyName());
        if (c.hasLogo()) {
            r.setImageUrl("image?customer=" + c.getCustomerId());
        }
        r.setDescription("Some description of the customer");
        return r;
    }

    private static CustomerRest to(final Customer c) {
        if (c == null) {
            return null;
        }
        final CustomerRest r = new CustomerRest();
        r.setId(c.getCustomerId());
        r.setCompanyName(c.getCompanyName());
        return r;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerRest[] getAll() {
        final List<CustomerRest> result = new ArrayList<>();
        for (final Customer c : this.resubmissionFacade.getCustomers())
            result.add(to(c));
        return result.toArray(new CustomerRest[result.size()]);
    }
}
