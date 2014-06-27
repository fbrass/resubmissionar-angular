package de.spqrinfo.resubmission.web.rest.dto;

import java.util.Date;

public class ResubmissionRestDto {

    private long customerId;
    private String note;
    private Date due;
    private boolean active;

    public long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public Date getDue() {
        return this.due;
    }

    public void setDue(final Date due) {
        this.due = due;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
}
