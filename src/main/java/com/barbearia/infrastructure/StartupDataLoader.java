package com.barbearia.infrastructure;

import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.MaterialCost;
import com.barbearia.domain.model.Product;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.MaterialCostRepository;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class StartupDataLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupDataLoader.class);

    private final CustomerRepository customerRepository;
    private final ServiceOfferRepository serviceOfferRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;
    private final MaterialCostRepository materialCostRepository;

    public StartupDataLoader(CustomerRepository customerRepository,
                             ServiceOfferRepository serviceOfferRepository,
                             ProductRepository productRepository,
                             AppointmentRepository appointmentRepository,
                             MaterialCostRepository materialCostRepository) {
        this.customerRepository = customerRepository;
        this.serviceOfferRepository = serviceOfferRepository;
        this.productRepository = productRepository;
        this.appointmentRepository = appointmentRepository;
        this.materialCostRepository = materialCostRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Idempotent startup data loader: only insert if not present
        Customer alice = customerRepository.findFirstByEmail("alice@example.com")
                .orElseGet(() -> customerRepository.save(new Customer("Alice Silva", "alice@example.com", "+55 11 99999-0001")));

        Customer bruno = customerRepository.findFirstByEmail("bruno@example.com")
                .orElseGet(() -> customerRepository.save(new Customer("Bruno Costa", "bruno@example.com", "+55 11 99999-0002")));

        ServiceOffer corte = serviceOfferRepository.findFirstByName("Corte Clássico")
                .orElseGet(() -> serviceOfferRepository.save(new ServiceOffer("Corte Clássico", "Corte masculino completo", 45.0)));

        ServiceOffer barba = serviceOfferRepository.findFirstByName("Barba Premium")
                .orElseGet(() -> serviceOfferRepository.save(new ServiceOffer("Barba Premium", "Acabamento e tratamento da barba", 35.0)));

        productRepository.findFirstByName("Pomada Modeladora")
                .orElseGet(() -> productRepository.save(new Product("Pomada Modeladora", "Fixação média", 28.0)));

        productRepository.findFirstByName("Óleo de Barba")
                .orElseGet(() -> productRepository.save(new Product("Óleo de Barba", "Hidratação e lubrificação", 35.0)));

        // Only add sample appointments if none exist to avoid duplicates on restart
        if (appointmentRepository.count() == 0) {
            appointmentRepository.save(new Appointment(alice, corte, LocalDateTime.now().plusDays(1).withHour(10).withMinute(30), 25.0));
            appointmentRepository.save(new Appointment(bruno, barba, LocalDateTime.now().plusDays(1).withHour(12).withMinute(15), 25.0));
        }

        // Material costs: add only if none exist with same description and date
        if (materialCostRepository.count() == 0) {
            materialCostRepository.save(new MaterialCost("Lâminas descartáveis", 60.0, LocalDate.now().minusDays(2)));
            materialCostRepository.save(new MaterialCost("Água e produtos", 120.0, LocalDate.now().minusDays(1)));
        }

        logger.info("Dados iniciais da barbearia carregados (idempotente).");
    }
}
