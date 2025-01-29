package org.example.crmsystem.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "org.example.crmsystem")
public class GlobalConfiguration {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public PrintStream printStream() {
        return new PrintStream(System.out);
    }
}
