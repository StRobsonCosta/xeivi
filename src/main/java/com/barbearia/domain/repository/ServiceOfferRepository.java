package com.barbearia.domain.repository;

import com.barbearia.domain.model.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
}
