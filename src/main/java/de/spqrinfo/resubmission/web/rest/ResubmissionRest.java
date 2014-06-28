package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionFacade;
import de.spqrinfo.resubmission.web.rest.dto.ResubmissionRestDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/resubmissions/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResubmissionRest {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @POST
    public void save(final ResubmissionRestDto resubmission) {
        final Customer customer = this.resubmissionFacade.getCustomer(resubmission.getCustomerId());
        if (resubmission.getId() == null) {
            final Resubmission r = new Resubmission();
            r.setNote(resubmission.getNote());
            r.setDue(resubmission.getDue());
            this.resubmissionFacade.createResubmission(customer, r);
        } else {
            // TODO get resubmission by id
            final Resubmission r = this.resubmissionFacade.getResubmission(resubmission.getId());
            r.setNote(resubmission.getNote());
            r.setDue(resubmission.getDue());
            r.setActive(resubmission.isActive());
            this.resubmissionFacade.updateResubmission(r);
        }
    }
}
