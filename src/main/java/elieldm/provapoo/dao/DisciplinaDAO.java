package elieldm.provapoo.dao;

import elieldm.provapoo.model.Disciplina;
import jakarta.persistence.EntityManager;

import java.util.List;

import static elieldm.provapoo.utils.JPAUtil.getEntityManager;

public class DisciplinaDAO extends GenericDAOImpl<Disciplina, Long> {

    public DisciplinaDAO() {
        super();
    }

    public List<Disciplina> findAllWithProfessores() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT d FROM Disciplina d LEFT JOIN FETCH d.professores", Disciplina.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
