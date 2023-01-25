package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.TaxDeclaration;

import java.util.List;

public interface TaxDeclarationDAO {

    List<?> getAll();

    TaxDeclaration findById(Long id);

    TaxDeclaration findByEstateId(Long id);

    Long save(TaxDeclaration taxDeclaration);

    void delete(TaxDeclaration td);

    void reset(TaxDeclaration td);
}
