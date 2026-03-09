package com.clark.roper.Dispatch.Admin;

import com.clark.roper.Dispatch.entity.Interests;
import com.clark.roper.Dispatch.entity.Languages;
import com.clark.roper.Dispatch.entity.Tag;
import com.clark.roper.Dispatch.repository.InterestsRepository;
import com.clark.roper.Dispatch.repository.LanguagesRepository;
import com.clark.roper.Dispatch.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


/** Seeds the database with Languages, Interests, and Tags and reads from csv file **/




@Component
public class DatabasePrePopulator implements CommandLineRunner {

    private final LanguagesRepository languagesRepository;
    private final InterestsRepository interestsRepository;
    private final TagRepository tagRepository;

    public DatabasePrePopulator(LanguagesRepository languagesRepository,
            InterestsRepository interestsRepository,
            TagRepository tagRepository) {
        this.languagesRepository = languagesRepository;
        this.interestsRepository = interestsRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) {
        seedFromCsv("seed/languages.csv", this::seedLanguage);
        seedFromCsv("seed/interests.csv", this::seedInterest);
        seedFromCsv("seed/tags.csv", this::seedTag);
    }

    //read from csv
    private void seedFromCsv(String resourcePath, java.util.function.Consumer<String> seeder) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                List<String> lines = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .collect(Collectors.toList());

                for (String line : lines) {
                    seeder.accept(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load seed file " + resourcePath + ": " + e.getMessage());
        }
    }

    private void seedLanguage(String name) {
        if (languagesRepository.findByLanguage(name).isEmpty()) {
            Languages language = new Languages();
            language.setLanguage(name);
            languagesRepository.save(language);
        }
    }

    private void seedInterest(String name) {
        if (interestsRepository.findByInterest(name).isEmpty()) {
            Interests interest = new Interests();
            interest.setInterest(name);
            interestsRepository.save(interest);
        }
    }

    private void seedTag(String name) {
        String upper = name.toUpperCase();
        if (tagRepository.findByName(upper).isEmpty()) {
            Tag tag = new Tag();
            tag.setName(upper);
            tagRepository.save(tag);
        }
    }
}
