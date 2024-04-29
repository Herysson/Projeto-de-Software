package com.example.CRUD.repository;

import com.example.CRUD.model.Pessoa;
import org.springframework.data.repository.CrudRepository;

public interface PessoaRepository extends CrudRepository<Pessoa,Integer> {

}
