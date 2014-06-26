package de.spqrinfo.resubmission.web;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;

@Named("customerDetails")
@ViewScoped
public class CustomerDetailsBean implements Serializable {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    private Long customerId;
    private Customer customer;

    // Properties to create a new resubmission

    private Resubmission resubmission = new Resubmission();

    public Resubmission getResubmission() {
        return this.resubmission;
    }

    // State of the page

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public String init() {
        // Init bean upon view parameters...
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.customer = this.resubmissionFacade.getCustomer(this.customerId);
        }
        return null; // stay on page
    }

    // For displaying the current customer and its current resubmissions

    public Customer getCustomer() {
        return this.customer;
    }

    public boolean getRenderLogo() {
        return this.customer.hasLogo();
    }

    // TODO this should belong to the business layer
    public String getLogo() {
        return "/image?customer=" + Long.toString(this.customerId);
    }

    public Collection<Resubmission> getResubmissions() {
        return this.resubmissionFacade.getResubmissions(this.customer);
    }

    // Updating a resubmission (e.g. mark it done)

    public void toggleResubmissionActive(final Resubmission resubmission) {
        resubmission.setActive(false);
        this.resubmissionFacade.updateResubmission(resubmission);
    }

    // Properties for a new resubmission

    public void addResubmission() {
        this.resubmissionFacade.createResubmission(this.customer, this.resubmission);

        // TODO can we do this in a better way?
        // Fetch updated customer to reflect the proper sort order
        this.customer = this.resubmissionFacade.getCustomer(this.customerId);

        // Clear form field
        this.resubmission = new Resubmission();
    }
}
