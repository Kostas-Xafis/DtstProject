package gr.hua.dit.springproject.DAO;

import gr.hua.dit.springproject.Entity.RealEstate;

import java.util.List;

public interface RealEstateDAO {

    List<RealEstate> getAll();

    RealEstate findById(Long id);

    Long save(RealEstate re);

    void delete(Long id);
}
