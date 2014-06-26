package de.spqrinfo.resubmission.web.rest;

public class CustomerDetailRest extends CustomerRest {

    private String imageUrl;
    private String description;

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
}
