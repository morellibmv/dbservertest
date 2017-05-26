package io.brunovargas.dbserver.dao;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.brunovargas.dbserver.model.User;
import io.brunovargas.dbserver.model.Vote;

@Transactional
public interface VoteDAO extends CrudRepository<Vote, Long> {

  public Vote findByUserAndVotedate(User user, Date votedate);
  
  public Iterable<Vote> findByVotedate(Date votedate);
  
  @Query("select v from Vote v " +
	         "where v.votedate between :startDate and :endDate")
  public Iterable<Vote> findByDatesBetween(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

}