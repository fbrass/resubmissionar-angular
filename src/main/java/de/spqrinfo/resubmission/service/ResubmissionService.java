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

    @Transactional
    public List<Customer> getCustomers(final Integer pageSize, final Integer page) {
        final TypedQuery<Customer> query = this.entityManager.createNamedQuery("Customer.findAll", Customer.class);

        if (pageSize != null && page != null) {
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Transactional
    public List<Customer> getCustomers(final Integer pageSize, final Integer page, final String searchText) {
        final TypedQuery<Customer> query = this.entityManager.createNamedQuery("Customer.findAllMatching", Customer.class);

        query.setParameter("companyName", '%' + searchText.toLowerCase() + '%');

        if (pageSize != null && page != null) {
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Transactional
    public long getCustomerCount() {
        return (long) this.entityManager.createNamedQuery("Customer.count").getSingleResult();
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
    public Customer updateCustomer(final Customer customer, final Long logoId) {
        if (logoId != null) { // attempt to reference previsously uploaded logo image
            final UploadFile logo = this.uploadFileService.findTemporaryOrNull(logoId);
            if (logo != null) {
                customer.setLogo(logo);
            }
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
    public void deleteCustomer(final long id) {
        final Customer customer = getCustomer(id);
        this.entityManager.remove(customer);
    }

    @Transactional
    public void deleteCustomerLogo(final Customer customer) {
        final UploadFile logo = customer.getLogo();
        customer.setLogo(null);
        this.entityManager.remove(logo);
        this.entityManager.merge(customer);
    }

    @Transactional
    public void createResubmission(final Customer customer, final Resubmission resubmission) {
        resubmission.setResubmissionId(null); // force new
        resubmission.setActive(true);
        resubmission.setCustomer(customer);
        customer.getResubmissions().add(resubmission);
        this.entityManager.persist(resubmission);

        final Query query = this.entityManager.createNamedQuery("Resubmission.markInactiveCertain");
        query.setParameter("cust", customer);
        query.setParameter("resub", resubmission);
        query.executeUpdate();
    }

    public List<Resubmission> getDashboardResubmissions() {
        return this.entityManager.createNamedQuery("Resubmission.findAllActive", Resubmission.class).getResultList();
    }

    @Transactional
    public void updateResubmission(final Resubmission resubmission) {
        this.entityManager.merge(resubmission);
    }

    public Resubmission getResubmission(final Long resubmissionId) {
        final TypedQuery<Resubmission> q = this.entityManager.createNamedQuery("Resubmission.find", Resubmission.class);
        q.setParameter("id", resubmissionId);
        return q.getSingleResult();
    }
}
