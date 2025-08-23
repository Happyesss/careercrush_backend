package com.stemlen.entity;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.HackathonDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "hackathons")
public class Hackathon {
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
	private List<String>eligibilityCriteria;
	private byte[] bannerImage;
	private byte[] iconImage;
	private Long postedBy;
	private String applyUrl;
	
	
    public HackathonDTO toDTO() {
		return new HackathonDTO(
				this.id,
				this.title, 
				this.organizer,
				this.eventDate, 
				this.duration, 
				this.location, 
				this.prize, 
				this.about, 
				this.description, 
				this.eligibilityCriteria, 
				this.bannerImage!=null?Base64.getEncoder().encodeToString(this.bannerImage):null,
				this.iconImage!=null?Base64.getEncoder().encodeToString(this.iconImage):null,
				this.postedBy,
				this.applyUrl
				);
    	
    }
}
