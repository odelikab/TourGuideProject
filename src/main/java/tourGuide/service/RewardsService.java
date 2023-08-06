package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public CompletableFuture<List<Attraction>> calculateRewards(User user)
			throws InterruptedException, ExecutionException {
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<VisitedLocation>();
		userLocations = user.getVisitedLocations();
		List<Attraction> attractions = new ArrayList<Attraction>();
		CompletableFuture<List<Attraction>> test = CompletableFuture.supplyAsync(() -> gpsUtil.getAttractions());

//		for (VisitedLocation visitedLocation : userLocations) {
//			for (Attraction attraction : attractions) {
//				if (user.getUserRewards().stream()
//						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
//					if (nearAttraction(visitedLocation, attraction)) {
//						user.addUserReward(
//								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user).get()));
//					}
//				}
//			}
//		}
//		return test.get();
		return test;
	}

//	public void calculateRewards(User user) {
//        List<VisitedLocation> userLocations = user.getVisitedLocations();
//        List<Attraction> allAttractions = gpsUtil.getAttractions();
//        List<UserReward> userRewards = user.getUserRewards();
//
//        // Get the attractions that the user has not visited yet
//        List<Attraction> attractionsToVisit = new CopyOnWriteArrayList<>();
//        for (Attraction attraction : allAttractions) {
//            if (userRewards.stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
//                attractionsToVisit.add(attraction);
//            }
//        }
//
//        List<CompletableFuture<UserReward>> futures = new ArrayList<>();
//
//        // Calculate the rewards for each location and attraction in a separate thread
//        for (VisitedLocation visitedLocation : userLocations) {
//            for  (Attraction attraction : attractionsToVisit) {
//                CompletableFuture<UserReward> future = CompletableFuture.supplyAsync(() -> {
//                    if (nearAttraction(visitedLocation, attraction)) {
//
//                        return new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user));
//                    }
//                    return null;
//                }, executorService);
//                futures.add(future);
//            }
//        }
//
//        // Collect the rewards calculated by each future
//        List<UserReward> newRewards = futures.stream()
//                .map(CompletableFuture::join)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        userRewards.addAll(newRewards);
//
//
//     //Sort the rewards by their reward points in descending order
//    userRewards.sort((r1, r2) -> Integer.compare(r2.getRewardPoints(), r1.getRewardPoints()));
//}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private CompletableFuture<Integer> getRewardPoints(Attraction attraction, User user) {

		CompletableFuture<Integer> rp = CompletableFuture
				.supplyAsync(() -> rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()));
		return rp;
	}

	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

	public int getAttractionProximityRange() {
		return attractionProximityRange;
	}

	public void setAttractionProximityRange(int attractionProximityRange) {
		this.attractionProximityRange = attractionProximityRange;
	}

}
