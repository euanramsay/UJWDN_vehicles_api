package com.udacity.vehicles.api;


import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")


class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    CollectionModel<EntityModel<Car>> list() {
        List<EntityModel<Car>> resources = carService.list().stream().map(assembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<Car> get(@PathVariable Long id) {
        Car car = carService.findById(id);
        return assembler.toModel(car);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        Car savedCar = carService.save(car);
        EntityModel<Car> resource = assembler.toModel(savedCar);
        return ResponseEntity.created(new URI(resource.getLink("price").toString())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        car.setId(id);
        Car savedCar = carService.save(car);
        EntityModel<Car> resource = assembler.toModel(savedCar);
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
