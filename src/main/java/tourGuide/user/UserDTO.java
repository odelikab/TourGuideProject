package tourGuide.user;

import java.util.UUID;

import gpsUtil.location.Location;

public class UserDTO {
	private Location location;
	private UUID userId;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
}
