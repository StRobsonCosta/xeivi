package com.barbearia.domain.repository;

import com.barbearia.domain.model.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
	Optional<ServiceOffer> findByName(String name);
	boolean existsByName(String name);
}
