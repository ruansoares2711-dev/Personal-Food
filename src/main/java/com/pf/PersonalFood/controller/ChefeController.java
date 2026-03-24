package com.pf.PersonalFood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pf.PersonalFood.model.Chefe;
import com.pf.PersonalFood.repository.ChefeRepository;

@RestController
@RequestMapping("/api/chefes")
@CrossOrigin(origins = "*")
public class ChefeController {

    @Autowired
    private ChefeRepository chefeRepo;

    @GetMapping
    public ResponseEntity<List<Chefe>> listarChefes() {
        // Essa única linha faz um "SELECT * FROM chefes INNER JOIN usuarios..."
        List<Chefe> listaDeChefes = chefeRepo.findAll();
        return ResponseEntity.ok(listaDeChefes);
    }
}