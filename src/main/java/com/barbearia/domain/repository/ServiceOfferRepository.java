package com.barbearia.domain.repository;

import com.barbearia.domain.model.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
    @Query(value = "SELECT * FROM service_offers WHERE name = ?1 LIMIT 1", nativeQuery = true)
    Optional<ServiceOffer> findFirstByName(String name);
    
    boolean existsByName(String name);
}
