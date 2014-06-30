package de.spqrinfo.resubmission.web.rest.dto;

public class CustomerRestDto {

    private Long id;
    private String companyName;
    private Long logoId;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public Long getLogoId() {
        return this.logoId;
    }

    public void setLogoId(final Long logoId) {
        this.logoId = logoId;
    }
}
