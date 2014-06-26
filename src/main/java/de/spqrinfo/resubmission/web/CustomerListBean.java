package de.spqrinfo.resubmission.web;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("customerList")
@ViewScoped
public class CustomerListBean implements Serializable {

    public static final int ITEMS_PER_PAGE = 25;
    private static final int ITEM_INDEX_INIT = 0;

    @Inject
    private ResubmissionFacade resubmissionFacade;

    public List<Customer> getCustomerList() {
        return this.resubmissionFacade.getCustomers();
    }

    int first = ITEM_INDEX_INIT;

    public int getItemsPerPage() { return ITEMS_PER_PAGE; }

    public int getFirst() {
        return this.first;
    }

    public String next() {
        final List<Customer> customers = this.resubmissionFacade.getCustomers();
        this.first += ITEMS_PER_PAGE;
        if (this.first > customers.size()) {
            this.first = customers.size() - ITEMS_PER_PAGE; // TODO broken: show last page
        }
        return null;
    }

    public String prev() {
        this.first -= ITEMS_PER_PAGE;
        if (this.first < 0) {
            this.first = ITEM_INDEX_INIT;
        }
        return null;
    }

    public boolean getPrevVisible() {
        return this.first > 0;
    }

    public boolean getNextVisible() {
        final List<Customer> customers = this.resubmissionFacade.getCustomers();
        return this.first + ITEMS_PER_PAGE < customers.size();
    }
}
