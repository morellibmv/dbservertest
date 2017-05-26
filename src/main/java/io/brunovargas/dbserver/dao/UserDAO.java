package io.brunovargas.dbserver.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import io.brunovargas.dbserver.model.User;

@Transactional
public interface UserDAO extends CrudRepository<User, Long> {

  public User findByEmailIgnoreCase(String email);

}