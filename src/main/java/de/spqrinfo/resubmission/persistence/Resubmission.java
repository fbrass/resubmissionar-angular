package de.spqrinfo.resubmission.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name="Resubmission.markInactiveCertain",
                query="UPDATE Resubmission r SET r.active = false WHERE r.customer = :cust AND r <> :resub"),
        @NamedQuery(name="Resubmission.findAllActive", query="SELECT r FROM Resubmission r WHERE r.active = true ORDER BY r.due DESC"),
        @NamedQuery(name="Resubmission.find", query="SELECT r FROM Resubmission r WHERE r.resubmissionId = :id")
})
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
