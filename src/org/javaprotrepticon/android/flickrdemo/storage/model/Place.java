package org.javaprotrepticon.android.flickrdemo.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Place {
	
	@DatabaseField(id = true)
	private String id;
	
	@DatabaseField
	private String parentId;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private String woeid;
	
	@DatabaseField
	private Integer photoCount;
	
	@DatabaseField
	private Integer placeType;
	
	@DatabaseField
	private String url;
	
	@DatabaseField
	private Double latitude;
	
	@DatabaseField
	private Double longitude;

	public Place() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWoeid() {
		return woeid;
	}

	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}

	public Integer getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(Integer photoCount) {
		this.photoCount = photoCount;
	}

	public Integer getPlaceType() {
		return placeType;
	}

	public void setPlaceType(Integer placeType) {
		this.placeType = placeType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}
