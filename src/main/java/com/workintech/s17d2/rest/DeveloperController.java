package com.workintech.s17d2.rest;


import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final Taxable taxable;

    // In-memory storage
    public Map<Integer, Developer> developers;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        this.developers = new ConcurrentHashMap<>();

    }

    // [GET] /workintech/developers
    @GetMapping
    public ResponseEntity<List<Developer>> findAll() {
        return ResponseEntity.ok(new ArrayList<>(developers.values()));
    }

    // [GET] /workintech/developers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Developer> findById(@PathVariable Integer id) {
        Developer dev = developers.get(id);
        return (dev == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dev);
    }

    // [POST] /workintech/developers
    // Body: { "id":1, "name":"Ada", "salary":120000, "experience":"JUNIOR" }
    @PostMapping
    public ResponseEntity<Developer> create(@RequestBody Developer body) {
        if (body.getId() == null || body.getExperience() == null || body.getSalary() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (developers.containsKey(body.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Developer created = buildWithTax(body);
        developers.put(created.getId(), created);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // [PUT] /workintech/developers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable Integer id, @RequestBody Developer body) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        body.setId(id);
        Developer updated = buildWithTax(body);
        developers.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] /workintech/developers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Developer removed = developers.remove(id);
        return (removed == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }


    // --- Helpers ---
    private Developer buildWithTax(Developer input) {
        double gross = input.getSalary();
        double net;
        Developer result;
        switch (input.getExperience()) {
            case JUNIOR -> {
                net = gross - (gross * (taxable.getSimpleTaxRate()/100));
                result = new JuniorDeveloper(input.getId(), input.getName(), net);
            }
            case MID -> {
                net = gross - (gross * (taxable.getMiddleTaxRate()/100));
                result = new MidDeveloper(input.getId(), input.getName(), net);
            }
            case SENIOR -> {
                net = gross - (gross * (taxable.getUpperTaxRate()/100));
                result = new SeniorDeveloper(input.getId(), input.getName(), net);
            }
            default -> throw new IllegalStateException("Unexpected experience: " + input.getExperience());
        }
        return result;
    }

}
