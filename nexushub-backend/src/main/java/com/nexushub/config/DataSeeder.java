package com.nexushub.config;

import com.nexushub.entity.Product;
import com.nexushub.entity.ProductStatus;
import com.nexushub.entity.User;
import com.nexushub.repository.ProductRepository;
import com.nexushub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        log.info("Seeding demo data...");

        User alice = userRepository.save(User.builder()
                .name("Alice Johnson")
                .email("alice@nexushub.dev")
                .password("password123")
                .authToken("demo-token-alice-001")
                .build());

        User bob = userRepository.save(User.builder()
                .name("Bob Martinez")
                .email("bob@nexushub.dev")
                .password("password123")
                .authToken("demo-token-bob-002")
                .build());

        // ACTIVE products
        productRepository.save(Product.builder()
                .name("Wireless Noise-Cancelling Headphones")
                .description("Premium over-ear headphones with 40h battery and ANC technology.")
                .price(new BigDecimal("149.99"))
                .stock(30)
                .category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400")
                .seller(alice)
                .status(ProductStatus.ACTIVE)
                .build());

        productRepository.save(Product.builder()
                .name("Mechanical Keyboard TKL")
                .description("Tenkeyless mechanical keyboard with Cherry MX Brown switches and RGB lighting.")
                .price(new BigDecimal("89.99"))
                .stock(50)
                .category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400")
                .seller(alice)
                .status(ProductStatus.ACTIVE)
                .build());

        productRepository.save(Product.builder()
                .name("Minimalist Leather Wallet")
                .description("Slim bifold wallet crafted from genuine full-grain leather. Holds up to 8 cards.")
                .price(new BigDecimal("34.50"))
                .stock(120)
                .category("Accessories")
                .imageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=400")
                .seller(bob)
                .status(ProductStatus.ACTIVE)
                .build());

        productRepository.save(Product.builder()
                .name("Stainless Steel Water Bottle 1L")
                .description("Vacuum-insulated double-wall bottle. Keeps cold 24h, hot 12h. BPA-free.")
                .price(new BigDecimal("28.00"))
                .stock(200)
                .category("Sports & Outdoors")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400")
                .seller(bob)
                .status(ProductStatus.ACTIVE)
                .build());

        // Low-stock product — demonstrates threshold logic (stock <= 5)
        productRepository.save(Product.builder()
                .name("Desk Lamp LED Adjustable")
                .description("Eye-care LED lamp with touch dimmer, 5 color temperatures and USB charging port.")
                .price(new BigDecimal("45.00"))
                .stock(3)
                .category("Home & Office")
                .imageUrl("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400")
                .seller(alice)
                .status(ProductStatus.ACTIVE)
                .build());

        // OUT_OF_STOCK product — demonstrates auto-status
        productRepository.save(Product.builder()
                .name("Portable Bluetooth Speaker")
                .description("360° sound, IPX7 waterproof, 20h battery. Perfect for outdoors.")
                .price(new BigDecimal("79.00"))
                .stock(0)
                .category("Electronics")
                .imageUrl("https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400")
                .seller(bob)
                .status(ProductStatus.OUT_OF_STOCK)
                .build());

        log.info("Demo data seeded: 2 users, 6 products (4 ACTIVE, 1 low-stock, 1 OUT_OF_STOCK)");
    }
}
