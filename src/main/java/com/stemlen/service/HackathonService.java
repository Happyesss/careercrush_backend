package com.stemlen.service;

import java.util.List;

import com.stemlen.dto.HackathonDTO;
import com.stemlen.exception.PortalException;

public interface HackathonService {

	public HackathonDTO postHackathon(HackathonDTO hackathonDTO) throws PortalException;

	public List<HackathonDTO> getAllHackathons();

	public HackathonDTO getHackathon(Long id) throws PortalException;

}
