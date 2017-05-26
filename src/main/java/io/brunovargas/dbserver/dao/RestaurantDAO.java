package io.brunovargas.dbserver.dao;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.brunovargas.dbserver.model.Restaurant;

@Transactional
public interface RestaurantDAO extends CrudRepository<Restaurant, Long> {

  public Restaurant findByNameIgnoreCase(String name);
  
}