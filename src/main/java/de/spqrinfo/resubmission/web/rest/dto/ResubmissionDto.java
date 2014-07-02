package de.spqrinfo.resubmission.web.rest.dto;

import java.util.Date;

public class ResubmissionDto {

    private Long customerId;
    private Long id;
    private String note;
    private Date due;
    private Boolean active;

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Boolean isActive() {
        return this.active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }
}
