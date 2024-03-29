package tourGuide;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

	@GetMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	@GetMapping("/getLocation")
	public Location getLocation(@RequestParam String userName) throws InterruptedException, ExecutionException {
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return visitedLocation.location;
	}

	// TODO: Change this method to no longer return a List of Attractions.
	// Instead: Get the closest five tourist attractions to the user - no matter how
	// far away they are.
	// Return a new JSON object that contains:
	// Name of Tourist attraction,
	// Tourist attractions lat/long,
	// The user's location lat/long,
	// The distance in miles between the user's location and each of the
	// attractions.
	// The reward points for visiting each Attraction.
	// Note: Attraction reward points can be gathered from RewardsCentral
	@RequestMapping("/getNearbyAttractions")
	public List<Attraction> getNearbyAttractions(@RequestParam String userName)
			throws InterruptedException, ExecutionException {
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return tourGuideService.getNearByAttractions(visitedLocation);
	}

	@RequestMapping("/getRewards")
	public List<UserReward> getRewards(@RequestParam String userName) {
		return tourGuideService.getUserRewards(getUser(userName));
	}

	@GetMapping("/getAllCurrentLocations")
	public CopyOnWriteArrayList<VisitedLocation> getAllCurrentLocations(@RequestParam String userName) {

		CopyOnWriteArrayList<VisitedLocation> vlocations = getUser(userName).getVisitedLocations();
		return vlocations;
	}

	@GetMapping("/getTripDeals")
	public List<Provider> getTripDeals(@RequestParam String userName) {
		List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
		return providers;
	}

	private User getUser(String userName) {
		return tourGuideService.getUser(userName);
	}

}