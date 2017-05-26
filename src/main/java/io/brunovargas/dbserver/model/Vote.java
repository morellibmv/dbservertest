package io.brunovargas.dbserver.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
    @JoinColumn(name="user", referencedColumnName="id")
	private User user;

	private Date votedate;

	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
    @JoinColumn(name="restaurant", referencedColumnName="id")
	private Restaurant restaurant;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getVotedate() {
		return votedate;
	}

	public void setVotedate(Date votedate) {
		this.votedate = votedate;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	@PrePersist
	private void defineVoteDate() {
		this.votedate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
