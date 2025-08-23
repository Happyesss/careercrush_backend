package com.stemlen.dto;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.springframework.data.annotation.Id;
import com.stemlen.entity.Hackathon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HackathonDTO {
	@Id
	private Long id;
	private String title;
	private String organizer;
	private LocalDate eventDate;
	private String duration;
	private String location;
	private String prize;
	private String about;
	private String description;
	private List<String> eligibilityCriteria;
	private String bannerImage;
	private String iconImage;
	private Long postedBy;
	private String applyUrl;

	public Hackathon toEntity() {
	    return new Hackathon(
	        this.id != null ? this.id : 0L, // Handle null ID
	        this.title, 
	        this.organizer,
	        this.eventDate, 
	        this.duration, 
	        this.location, 
	        this.prize, 
	        this.about, 
	        this.description, 
	        this.eligibilityCriteria, 
	        this.bannerImage != null ? Base64.getDecoder().decode(this.bannerImage) : null,
	        this.iconImage != null ? Base64.getDecoder().decode(this.iconImage) : null,
	        this.postedBy,
	        this.applyUrl
	    );
	}

}
