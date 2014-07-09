package de.spqrinfo.resubmission.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name="UploadFile.deleteOlderThan",
                query="DELETE from UploadFile c WHERE c.temporary = true AND c.created <= :when"),
        @NamedQuery(name= "UploadFile.findTemporary",
                query="SELECT uf FROM UploadFile uf WHERE uf.temporary = true AND uf.uploadId = :id")
})
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uploadId;

    @NotNull
    private String filename;

    @NotNull
    private String contentType;

    @NotNull
    private long size;

    @NotNull
    @Column(length = 1024 * 1024 * 10)
    private byte[] data;

    @NotNull
    private Date created;

    private boolean temporary = true;

    public Long getUploadId() {
        return this.uploadId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUploadId(final Long uploadId) {
        this.uploadId = uploadId;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(final boolean temporary) {
        this.temporary = temporary;
    }
}
