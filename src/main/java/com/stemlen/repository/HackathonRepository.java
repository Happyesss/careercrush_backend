package com.stemlen.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.stemlen.entity.Hackathon;

public interface HackathonRepository extends MongoRepository<Hackathon, Long> {
	public List<Hackathon> findByPostedBy(Long postedBy);
}
