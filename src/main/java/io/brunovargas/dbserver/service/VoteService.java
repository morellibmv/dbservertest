package io.brunovargas.dbserver.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.ws.http.HTTPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.brunovargas.dbserver.dao.RestaurantDAO;
import io.brunovargas.dbserver.dao.UserDAO;
import io.brunovargas.dbserver.dao.VoteDAO;
import io.brunovargas.dbserver.model.Restaurant;
import io.brunovargas.dbserver.model.User;
import io.brunovargas.dbserver.model.Vote;

@RestController
@RequestMapping("/rest/vote")
public class VoteService {

	@Value("${vote.allowRepeatedRestaurant:true}")
	private Boolean allowRepeatedRestaurant;

	@Value("${vote.endVoteAtNoon:true}")
	private Boolean endVoteAtNoon;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> vote(@RequestBody(required = false) Vote vote) throws Exception {
		ResponseEntity<?> result;

		if (isAllowedToVote()) {
			updateRestaurantData(vote);
			if(isRestaurantAllowed(vote)){
				updateUserAndVoteData(vote);
				result = new ResponseEntity<>(HttpStatus.OK);
				
			} else{
				result = new ResponseEntity<>("Este restaurante não pode ser escolhido.", HttpStatus.BAD_REQUEST);
			}

		} else {
			result = new ResponseEntity<>("Votações Encerradas", HttpStatus.METHOD_NOT_ALLOWED);
		}

		return result;

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<?> mostVoted() throws Exception {
		ResponseEntity<?> result;

		Iterable<Vote> todayVotes = voteDao
				.findByVotedate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		Map<String, Long> voteResult = StreamSupport.stream(todayVotes.spliterator(), false).map(Vote::getRestaurant)
				.collect(Collectors.groupingBy(Restaurant::getName, Collectors.counting()));
		result = new ResponseEntity<>(
				voteResult.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).findFirst().get()
						.getKey(),
				HttpStatus.OK);

		return result;

	}

	@RequestMapping(value = "/restaurant/config", method = RequestMethod.POST)
	public ResponseEntity<?> setRepeatRestaurant() throws Exception {
		allowRepeatedRestaurant = !allowRepeatedRestaurant;
		return new ResponseEntity<>(allowRepeatedRestaurant, HttpStatus.OK);

	}

	@RequestMapping(value = "/restaurant/config", method = RequestMethod.GET)
	public ResponseEntity<?> getRepeatRestaurant() throws Exception {
		return new ResponseEntity<>(allowRepeatedRestaurant, HttpStatus.OK);

	}

	@RequestMapping(value = "/config", method = RequestMethod.POST)
	public ResponseEntity<?> setEndVoteAtNoon() throws Exception {
		endVoteAtNoon = !endVoteAtNoon;
		return new ResponseEntity<>(endVoteAtNoon, HttpStatus.OK);

	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public ResponseEntity<?> getEndVoteAtNoon() throws Exception {
		return new ResponseEntity<>(endVoteAtNoon, HttpStatus.OK);

	}

	private Boolean isAllowedToVote() throws HTTPException {
		return !(LocalTime.now().isAfter(LocalTime.NOON) && endVoteAtNoon);
	}
	
	private void updateRestaurantData(Vote vote){
		Restaurant restaurant = vote.getRestaurant();
		Restaurant toPersistRestaurant;
		if (restaurant.getId() != null) {
			toPersistRestaurant = restaurantDao.findOne(vote.getRestaurant().getId());
		} else {
			toPersistRestaurant = restaurantDao.findByNameIgnoreCase(vote.getRestaurant().getName());
		}
		
		if (toPersistRestaurant != null) {
			vote.setRestaurant(toPersistRestaurant);
		}
	}
	
	private Boolean isRestaurantAllowed(Vote vote){
		if(allowRepeatedRestaurant){
			return true;
		}
		LocalDate now = LocalDate.now();
		TemporalField weekField = WeekFields.of(Locale.US).dayOfWeek();
		Iterable<Vote> thisWeekVoted = voteDao.findByDatesBetween(
				Date.from(now.with(weekField, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
				Date.from(now.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		Map<Date, List<Vote>> votesByDate = StreamSupport.stream(thisWeekVoted.spliterator(), false)
				.collect(Collectors.groupingBy(Vote::getVotedate));

		Map<Date, Map<String, Long>> voteCountsByDate = votesByDate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						voteDate -> voteDate.getValue().stream().map(Vote::getRestaurant)
								.collect(Collectors.groupingBy(Restaurant::getName, Collectors.counting()))));
		List<String> thisWeekWinners = voteCountsByDate.entrySet().stream()
				.map(dateVotes -> dateVotes.getValue().entrySet().stream()
						.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).findFirst()
						.map(Map.Entry::getKey).get().toUpperCase())
				.collect(Collectors.toList());

		return !thisWeekWinners.contains(vote.getRestaurant().getName().toUpperCase());
			
	}
	
	private void updateUserAndVoteData(Vote vote){
		User tempUser = userDao.findByEmailIgnoreCase(vote.getUser().getEmail());
		if (tempUser != null) {
			tempUser = userDao.save(tempUser);
			vote.setUser(tempUser);

			Vote tempVote = voteDao.findByUserAndVotedate(vote.getUser(),
					Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			System.out.print(tempVote);
			if (tempVote != null) {
				tempVote = voteDao.save(tempVote);
				tempVote.setRestaurant(vote.getRestaurant());
				vote = tempVote;
			}
		}
		voteDao.save(vote);
	}

	@Autowired
	private VoteDAO voteDao;

	@Autowired
	private RestaurantDAO restaurantDao;

	@Autowired
	private UserDAO userDao;

}
