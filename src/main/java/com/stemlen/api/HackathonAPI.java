package com.stemlen.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.HackathonDTO;
import com.stemlen.exception.PortalException;
import com.stemlen.service.HackathonService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/hackathons")
public class HackathonAPI {
 
	@Autowired
	private HackathonService hackathonService;
	
	@PostMapping("/post")
	public ResponseEntity<HackathonDTO> postHackathon(@RequestBody @Valid HackathonDTO hackathonDTO) throws PortalException {
		return new ResponseEntity<>(hackathonService.postHackathon(hackathonDTO), HttpStatus.CREATED);
	}

	@GetMapping("/getAll")
	public ResponseEntity<List<HackathonDTO>> getAllHackathons() throws PortalException {
		return new ResponseEntity<>(hackathonService.getAllHackathons(), HttpStatus.OK);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<HackathonDTO> getHackathon(@PathVariable Long id) throws PortalException {
		return new ResponseEntity<>(hackathonService.getHackathon(id), HttpStatus.OK);
	}

//	@GetMapping("/organizedBy/{id}")
//	public ResponseEntity<List<HackathonDTO>> getHackathonsOrganizedBy(@PathVariable Long id) throws PortalException {
//		return new ResponseEntity<>(hackathonService.getHackathonsOrganizedBy(id), HttpStatus.OK);
//	}
}
