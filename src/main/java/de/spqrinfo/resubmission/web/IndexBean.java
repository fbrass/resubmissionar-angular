package de.spqrinfo.resubmission.web;

import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("index")
@RequestScoped
public class IndexBean implements Serializable {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    public List<de.spqrinfo.resubmission.persistence.Resubmission> getDashboardResubmissions() {
        return this.resubmissionFacade.getDashboardResubmissions();
    }
}
