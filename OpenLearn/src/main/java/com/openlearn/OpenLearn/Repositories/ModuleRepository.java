package com.openlearn.OpenLearn.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.openlearn.OpenLearn.Model.Entities.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module,Long> {
}
