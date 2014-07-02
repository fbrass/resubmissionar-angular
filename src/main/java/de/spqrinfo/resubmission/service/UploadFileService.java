package de.spqrinfo.resubmission.service;

import de.spqrinfo.resubmission.persistence.UploadFile;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

@Stateless
public class UploadFileService {

    private static final Logger log = Logger.getLogger(UploadFileService.class.getName());

    private static final long ONE_MINUTE_IN_MILLIS = 60000; // ms

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UploadFile createTemporaryUpload(final UploadFile uploadFile) {
        log.log(INFO, "creating temporary upload file {0}", uploadFile.getFilename());
        uploadFile.setCreated(new Date());
        uploadFile.setTemporary(true);
        this.entityManager.persist(uploadFile);
        return uploadFile; // guaranteed to contain the generated id outside of the transaction
    }

//    public static javax.json.JsonObject getTemporaryImageInfo(final UploadFile uploadFile) {
//        final JsonObjectBuilder builder = Json.createObjectBuilder();
//        builder.add("logoId", Long.toString(uploadFile.getUploadId()));
//        FileServlet.appendCustomerLogoImageUrl(builder, uploadFile);
//        return builder.build();
//    }

    @Transactional
    private void cleanTemporaryUploads(final Date past) {
        final Query query = this.entityManager.createQuery("DELETE from UploadFile c WHERE c.temporary = true AND c.created <= :when");
        query.setParameter("when", past);
        query.executeUpdate();
    }

    @Schedule(minute = "*/5", hour = "*")
    public void cleanUploads() {
        final Date minutesAgo = new Date(new Date().getTime() - 5 * ONE_MINUTE_IN_MILLIS);
        log.log(INFO, "cleaning uploads older than {0}", minutesAgo);
        cleanTemporaryUploads(minutesAgo);
    }

    @Transactional
    public UploadFile findTemporary(final Long id) {
        final String cq = "SELECT uf FROM UploadFile uf WHERE uf.temporary = true AND uf.uploadId = :id";
        final TypedQuery<UploadFile> query = this.entityManager.createQuery(cq, UploadFile.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Transactional
    public void markPermanent(final UploadFile uploadFile) {
        uploadFile.setTemporary(false);
        this.entityManager.persist(uploadFile);
    }
}
