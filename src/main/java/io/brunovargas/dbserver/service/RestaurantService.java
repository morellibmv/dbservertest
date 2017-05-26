package io.brunovargas.dbserver.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.brunovargas.dbserver.dao.RestaurantDAO;
import io.brunovargas.dbserver.model.Restaurant;

@RestController
@RequestMapping("/rest/restaurant")
public class RestaurantService {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Iterable<Restaurant> getAll(@RequestParam(name = "id", required = false) Integer id) throws Exception {
		Iterable<Restaurant> result = restaurantDao.findAll();;
		return result;
	}
	
	 @Autowired
	 private RestaurantDAO restaurantDao;

}
