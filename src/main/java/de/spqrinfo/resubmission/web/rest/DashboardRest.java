package de.spqrinfo.resubmission.web.rest;

import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionFacade;
import de.spqrinfo.resubmission.web.rest.dto.DashboardRestDto;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/dashboard/")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardRest {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @GET
    public DashboardRestDto[] getAll() {
        final List<Resubmission> dashboardResubmissions = this.resubmissionFacade.getDashboardResubmissions();

        final List<DashboardRestDto> result = new ArrayList<>();
        for (final Resubmission resub : dashboardResubmissions) {
            result.add(to(resub));
        }
        return result.toArray(new DashboardRestDto[result.size()]);
    }

    private static DashboardRestDto to(final Resubmission r) {
        if (r == null) {
            return null;
        }
        final DashboardRestDto d = new DashboardRestDto();
        d.setCustomerId(r.getCustomer().getCustomerId());
        d.setCustomerName(r.getCustomer().getCompanyName());
        d.setResubmissionDue(r.getDue());
        d.setResubmissionNote(r.getNote());
        return d;
    }
}
