package com.jyukutyo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SpringFlixFluxClientApplication {

    @Bean
    WebClient client() {
        return WebClient.create("http://localhost:8080/movies");
    }

    @Bean
    CommandLineRunner demo(WebClient client) {
        return args -> {
            client.get().uri("").exchange()
                    .flatMapMany(clientResponse ->
                            clientResponse.bodyToFlux(Movie.class))
                    .filter(movie -> movie.getName().contains("aaa"))
                    .subscribe(movie ->
                            client.get().uri("/{id}/events", movie.getId())
                            .exchange()
                            .flatMapMany(cr -> cr.bodyToFlux(MovieEvent.class))
                            .subscribe(System.out::println)
                    );
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringFlixFluxClientApplication.class, args);
    }
}

@AllArgsConstructor
@ToString
@NoArgsConstructor
@Data
class Movie {
    private String id;

    private String name, genre;
}

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
class MovieEvent {
    private Movie movie;
    private Date when;
    private String user;
}
