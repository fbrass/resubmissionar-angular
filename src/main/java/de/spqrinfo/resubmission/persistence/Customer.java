package de.spqrinfo.resubmission.persistence;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {

    public static final int LOGO_MAX_SIZE = 32768;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;

    @NotNull
    private String companyName;

    private String logoContentType;

    @Column(length = LOGO_MAX_SIZE)
    private byte[] logo;

    @OneToMany(mappedBy = "customer")
    @OrderBy("due DESC")
    private final List<Resubmission> resubmissions = new ArrayList<>();

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public void setLogoContentType(final String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public void setLogo(final byte[] logo) {
        this.logo = logo;
    }

    public boolean hasLogo() { return this.logo != null; }

    public List<Resubmission> getResubmissions() {
        return this.resubmissions;
    }
}
