package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    private final Taxable taxable;
    public Map<Integer, Developer> developers;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getById(@PathVariable("id") int id) {
        Developer dev = developers.get(id);
        if (dev == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dev);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer add(@RequestBody Developer request) {
        Developer dev;
        double salary = request.getSalary();

        switch (request.getExperience()) {
            case JUNIOR -> {
                salary = salary - (salary * (taxable.getSimpleTaxRate() / 100));
                dev = new JuniorDeveloper(request.getId(), request.getName(), salary);
            }
            case MID -> {
                salary = salary - (salary * (taxable.getMiddleTaxRate() / 100));
                dev = new MidDeveloper(request.getId(), request.getName(), salary);
            }
            case SENIOR -> {
                salary = salary - (salary * (taxable.getUpperTaxRate() / 100));
                dev = new SeniorDeveloper(request.getId(), request.getName(), salary);
            }
            default -> throw new IllegalArgumentException("Geçersiz deneyim tipi");
        }

        developers.put(dev.getId(), dev);
        return dev;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable("id") int id, @RequestBody Developer updatedDev) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        double salary = updatedDev.getSalary();
        Developer dev;
        switch (updatedDev.getExperience()) {
            case JUNIOR -> {
                salary = salary - (salary * (taxable.getSimpleTaxRate() / 100));
                dev = new JuniorDeveloper(id, updatedDev.getName(), salary);
            }
            case MID -> {
                salary = salary - (salary * (taxable.getMiddleTaxRate() / 100));
                dev = new MidDeveloper(id, updatedDev.getName(), salary);
            }
            case SENIOR -> {
                salary = salary - (salary * (taxable.getUpperTaxRate() / 100));
                dev = new SeniorDeveloper(id, updatedDev.getName(), salary);
            }
            default -> throw new IllegalArgumentException("Geçersiz deneyim tipi");
        }

        developers.put(id, dev);
        return ResponseEntity.ok(dev);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        Developer removed = developers.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Developer with id " + id + " deleted.");
    }
}
