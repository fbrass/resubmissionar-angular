package de.spqrinfo.resubmission.web.rest.dto;

public class CustomerDetailDto extends CustomerDto {

    private String imageUrl;
    private String description;
    private ResubmissionDto[] resubmissions;

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ResubmissionDto[] getResubmissions() {
        return this.resubmissions;
    }

    public void setResubmissions(final ResubmissionDto[] resubmissions) {
        this.resubmissions = resubmissions;
    }
}
