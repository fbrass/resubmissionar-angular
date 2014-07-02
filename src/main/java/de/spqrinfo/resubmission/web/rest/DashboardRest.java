package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionService;
import de.spqrinfo.resubmission.web.rest.dto.DashboardtDto;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/dashboard/")
@Produces(APPLICATION_JSON)
public class DashboardRest {

    @Inject
    private ResubmissionService resubmissionService;

    @GET
    public DashboardtDto[] getAll() {
        final List<Resubmission> dashboardResubmissions = this.resubmissionService.getDashboardResubmissions();

        final List<DashboardtDto> result = new ArrayList<>();
        for (final Resubmission resub : dashboardResubmissions) {
            result.add(to(resub));
        }
        return result.toArray(new DashboardtDto[result.size()]);
    }

    private static DashboardtDto to(final Resubmission r) {
        if (r == null) {
            return null;
        }
        final DashboardtDto d = new DashboardtDto();
        d.setCustomerId(r.getCustomer().getCustomerId());
        d.setCustomerName(r.getCustomer().getCompanyName());
        d.setResubmissionDue(r.getDue());
        d.setResubmissionNote(r.getNote());
        return d;
    }
}
