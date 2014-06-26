package de.spqrinfo.resubmission.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Resubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long resubmissionId;

    @NotNull
    @ManyToOne
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date due;

    @NotNull
    @Column(length = 2048)
    private String note;

    private boolean active;

    public Long getResubmissionId() {
        return this.resubmissionId;
    }

    public void setResubmissionId(final Long resubmissionId) {
        this.resubmissionId = resubmissionId;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    public Date getDue() {
        return this.due;
    }

    public void setDue(final Date due) {
        this.due = due;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
}
