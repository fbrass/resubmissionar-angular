package de.spqrinfo.resubmission.web.rest.dto;

public class CustomerDetailRestDto extends CustomerRestDto {

    private String imageUrl;
    private String description;
    private ResubmissionRestDto[] resubmissions;

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

    public ResubmissionRestDto[] getResubmissions() {
        return this.resubmissions;
    }

    public void setResubmissions(final ResubmissionRestDto[] resubmissions) {
        this.resubmissions = resubmissions;
    }
}
