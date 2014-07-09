package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionService;
import de.spqrinfo.resubmission.web.rest.dto.ResubmissionDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/resubmissions/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class ResubmissionRest {

    @Inject
    private ResubmissionService resubmissionService;

    @POST
    public void save(final ResubmissionDto resubmission) {
        final Customer customer = this.resubmissionService.getCustomer(resubmission.getCustomerId());
        if (resubmission.getId() == null) {
            final Resubmission r = new Resubmission();
            r.setNote(resubmission.getNote());
            r.setDue(resubmission.getDue());
            this.resubmissionService.createResubmission(customer, r);
        } else {
            final Resubmission r = this.resubmissionService.getResubmission(resubmission.getId());
            r.setNote(resubmission.getNote());
            r.setDue(resubmission.getDue());
            r.setActive(resubmission.isActive());
            this.resubmissionService.updateResubmission(r);
        }
    }
}
