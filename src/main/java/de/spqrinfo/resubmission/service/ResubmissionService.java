package de.spqrinfo.resubmission.service;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Resubmission;
import de.spqrinfo.resubmission.persistence.UploadFile;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class ResubmissionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UploadFileService uploadFileService;

    public List<Customer> getCustomers() {
        return this.entityManager.createQuery("SELECT c FROM Customer c ORDER BY c.companyName", Customer.class).getResultList();
    }

    @Transactional
    public Customer createCustomer(final Customer customer, final Long logoId) {
        if (logoId != null) { // attempt to reference previsously uploaded logo image
            final UploadFile logo = this.uploadFileService.findTemporary(logoId);
            if (logo == null) {
                throw new RuntimeException("Invalid attempt to reference logo");
            }
            customer.setLogo(logo);
        }

        this.entityManager.persist(customer);

        if (customer.hasLogo()) {
            final UploadFile logo = customer.getLogo();
            this.uploadFileService.markPermanent(logo);
        }

        return customer;
    }

    @Transactional
    public Customer getCustomer(final Long customerId) {
        final Customer customer = this.entityManager.find(Customer.class, customerId);
        customer.getResubmissions().size();
        return customer;
    }

    @Transactional
    public void createResubmission(final Customer customer, final Resubmission resubmission) {
        resubmission.setResubmissionId(null); // force new
        resubmission.setActive(true);
        resubmission.setCustomer(customer);
        customer.getResubmissions().add(resubmission);
        this.entityManager.persist(resubmission);

        final Query query = this.entityManager.createQuery("UPDATE Resubmission r SET r.active = false WHERE r.customer = :cust " +
                "AND r <> :resub");
        query.setParameter("cust", customer);
        query.setParameter("resub", resubmission);
        query.executeUpdate();
    }

    public List<Resubmission> getDashboardResubmissions() {
        return this.entityManager.createQuery("SELECT r FROM Resubmission r WHERE r.active = true ORDER BY r.due DESC",
                Resubmission.class).getResultList();
    }

    @Transactional
    public void updateResubmission(final Resubmission resubmission) {
        this.entityManager.merge(resubmission);
    }

    // Difference to customer.getResubmissions() is that
    //  active == true comes first
    //  active == false is order by due date
//    public List<Resubmission> getResubmissions(final Customer customer) {
//        final String q1 = "SELECT r FROM Resubmission r WHERE r.active = true AND r.customer = :cust";
//        final String q2 = "SELECT r FROM Resubmission r WHERE r.active = false AND r.customer = :cust ORDER BY r.due DESC";
//
//        final List<Resubmission> resultList = this.entityManager.createQuery(q1, Resubmission.class)
//                .setParameter("cust", customer).getResultList();
//        final List<Resubmission> resultList2 = this.entityManager.createQuery(q2, Resubmission.class)
//                .setParameter("cust", customer).getResultList();
//
//        resultList.addAll(resultList2);
//        return resultList;
//    }

    public Resubmission getResubmission(final Long resubmissionId) {
        final TypedQuery<Resubmission> q = this.entityManager.createQuery(
                "SELECT r FROM Resubmission r WHERE r.resubmissionId = :id",
                Resubmission.class);
        q.setParameter("id", resubmissionId);
        return q.getSingleResult();
    }
}
