package de.spqrinfo.resubmission.persistence;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name="Customer.findAll", query="SELECT c FROM Customer c ORDER BY c.companyName"),
        @NamedQuery(name="Customer.findAllMatching",
                query="SELECT c FROM Customer c WHERE lower(c.companyName) LIKE :companyName ORDER BY c.companyName"),
        @NamedQuery(name="Customer.count", query="SELECT COUNT(c) FROM Customer c")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;

    @NotNull
    private String companyName;

    private String description;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("due DESC")
    private final List<Resubmission> resubmissions = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "uploadfileid")
    private UploadFile logo;

    public Long getCustomerId() {
        return this.customerId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Resubmission> getResubmissions() {
        return this.resubmissions;
    }

    public UploadFile getLogo() {
        return this.logo;
    }

    public void setLogo(final UploadFile logo) {
        this.logo = logo;
    }

    public boolean hasLogo() {
        return this.logo != null;
    }
}
