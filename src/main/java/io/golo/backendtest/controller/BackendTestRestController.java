package io.golo.backendtest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.golo.backendtest.model.TemplateDTO;

/**
 * Rest controller exposing all API endpoints
 */
@RestController
public class BackendTestRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendTestRestController.class);

    /**
     * Default constructor
     */
    @Autowired
    public BackendTestRestController() {
        // Put init here
    }

    /**
     * Returns API resource
     *
     * @param parameter
     *
     * @return The response containing the data to retrieve.
     */
    @GetMapping(value = "/uri to setup here")
    public ResponseEntity<TemplateDTO> getBooking(@RequestParam(name = "parameter", required = false) String parameter) {
        LOGGER.trace("Get resource");


        return new ResponseEntity<>(new TemplateDTO(), HttpStatus.OK);

    }

}
