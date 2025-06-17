package elieldm.provapoo.dao;

import elieldm.provapoo.model.Professor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import static elieldm.provapoo.utils.JPAUtil.getEntityManager;

public class ProfessorDAO extends GenericDAOImpl<Professor, Long> {

    public ProfessorDAO() {
        super();
    }


    public List<Professor> findAllWithDisciplinas() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Professor> query = em.createQuery(
                    "SELECT DISTINCT p FROM Professor p LEFT JOIN FETCH p.disciplinas",
                    Professor.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    public Professor findByIdWithDisciplinas(Long id) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Professor> query = em.createQuery(
                    "SELECT p FROM Professor p LEFT JOIN FETCH p.disciplinas WHERE p.id = :id",
                    Professor.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
