package com.informatization_controle_declarations_biens.declaration_biens_control.iservice.securite;

import java.util.List;
import java.util.Optional;

public interface IGenericService<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}

