package de.spqrinfo.resubmission.service;

import de.spqrinfo.resubmission.persistence.Upload;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Date;

@Stateless
public class UploadFacade {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Upload createUpload(final Upload upload) {
        upload.setCreated(new Date());
        this.entityManager.persist(upload);
        return upload; // guaranteed to contain the generated id outside of the transaction
    }

    @Transactional
    void cleanUploadsAgo(final Date past) {
        final Query query = this.entityManager.createQuery("DELETE from Upload c WHERE c.created <= :when");
        query.setParameter("when", past);
        query.executeUpdate();
    }
}
