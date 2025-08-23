package com.stemlen.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.HackathonDTO;
import com.stemlen.entity.Hackathon;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.HackathonRepository;
import com.stemlen.utility.Utilities;

@Service("hackathonService")
public class HackathonServiceImpl implements HackathonService {
	@Autowired
	private HackathonRepository hackathonRepository;

	@Override
	public HackathonDTO postHackathon(HackathonDTO hackathonDTO) throws PortalException {
	    if (Objects.isNull(hackathonDTO.getId()) || hackathonDTO.getId() == 0) {
	        hackathonDTO.setId(Utilities.getNextSequence("hackathon"));
	    }

	    Hackathon hackathon = hackathonRepository.save(hackathonDTO.toEntity());
	    return hackathon.toDTO();
	}

	@Override
	public List<HackathonDTO> getAllHackathons() {
	    return hackathonRepository.findAll().stream() 
	            .map(Hackathon::toDTO)
	            .toList();
	}

	@Override
	public HackathonDTO getHackathon(Long id) throws PortalException {
		return hackathonRepository.findById(id)
				.orElseThrow(()->new PortalException("HACKATHON_NOT_FOUND"))
				.toDTO();
	}

}
