package de.spqrinfo.resubmission.web.rest.dto;

import java.util.Date;

public class DashboardtDto {

    private long customerId;
    private String customerName;
    private String resubmissionNote;
    private Date resubmissionDue;

    public long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public String getResubmissionNote() {
        return this.resubmissionNote;
    }

    public void setResubmissionNote(final String resubmissionNote) {
        this.resubmissionNote = resubmissionNote;
    }

    public Date getResubmissionDue() {
        return this.resubmissionDue;
    }

    public void setResubmissionDue(final Date resubmissionDue) {
        this.resubmissionDue = resubmissionDue;
    }
}
