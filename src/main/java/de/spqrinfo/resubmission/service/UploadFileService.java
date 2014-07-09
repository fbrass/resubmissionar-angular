package de.spqrinfo.resubmission.service;

import de.spqrinfo.resubmission.persistence.UploadFile;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

@Stateless
public class UploadFileService {

    private static final Logger log = Logger.getLogger(UploadFileService.class.getName());

    public static final long ONE_MINUTE_IN_MILLIS = 60000; // ms

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

    @Transactional
    private void cleanTemporaryUploads(final Date past) {
        final Query query = this.entityManager.createNamedQuery("UploadFile.deleteOlderThan");
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
        final TypedQuery<UploadFile> query = this.entityManager.createNamedQuery("UploadFile.findTemporary", UploadFile.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public UploadFile findTemporaryOrNull(final Long id) {
        try {
            return findTemporary(id);
        } catch (final NoResultException ignored) {
            return null;
        }
    }

    @Transactional
    public void markPermanent(final UploadFile uploadFile) {
        uploadFile.setTemporary(false);
        this.entityManager.persist(uploadFile);
    }
}
