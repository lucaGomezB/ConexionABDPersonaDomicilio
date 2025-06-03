import config.DatabaseConnection;
import dao.DomicilioDAO;
import dao.PersonaDAO;
import model.Domicilio;
import model.Persona;
import service.DomicilioService;
import service.PersonaService;

import java.util.List;
import java.util.Optional; 

public class Main {
    public static void main(String[] args) {
        // Your DatabaseConnection will handle connecting to the configured MySQL database.
        System.out.println("--- Starting Persona Service Test ---");

        // 1. Instantiate DAOs
        // DomicilioDAO is needed first as PersonaDAO depends on it.
        DomicilioDAO domicilioDAO = new DomicilioDAO();
        PersonaDAO personaDAO = new PersonaDAO(domicilioDAO); 

        // 2. Instantiate Services
        // Services orchestrate business logic and transactions, using DAOs.
        DomicilioService domicilioService = new DomicilioService(domicilioDAO);
        PersonaService personaService = new PersonaService(personaDAO);

        System.out.println("\n--- Testing PersonaService CRUD Operations ---");

        // --- CREATE Operation ---
        System.out.println("\n*** Creating a new Persona ***");
        // Create a Domicilio object. Its creation/linking will be handled by PersonaService/DAO.
        // Ensure the associated Domicilio is valid before creating the Persona.
        Domicilio domicilio1 = new Domicilio(45,"Mendoza Capital", "Mendoza"); 
        Persona persona1 = new Persona(46,"Juan Perez", 30, domicilio1);

        try {
            // Attempt to create the persona. PersonaService handles Domicilio creation/linking.
            persona1 = personaService.create(persona1);
            System.out.println("Persona creada con éxito: " + persona1);
        } catch (RuntimeException e) {
            // Catch specific validation errors or general runtime exceptions from service layer
            System.err.println("Error al crear persona: " + e.getMessage());
        }

        // --- READ by ID Operation ---
        System.out.println("\n*** Reading Persona by ID ***");
            try {
                Persona foundPersona = personaService.findById(persona1.getId());
                System.out.println("Persona encontrada por ID: " + foundPersona);
            } catch (RuntimeException e) {
                System.err.println("Error al encontrar persona por ID: " + e.getMessage());
            }

        // --- UPDATE Operation ---
        System.out.println("\n*** Updating Persona ***");
            persona1.setNombre("Juan Carlos Perez"); // Update name
            persona1.setEdad(31); // Update age
            // Update associated Domicilio details (this will trigger DomicilioDAO.update via PersonaDAO.update)
            // Ensure the Domicilio object has its ID set if it's an existing one being updated.
            if(persona1.getDomicilio() != null) { // Add a check for null Domicilio
                persona1.getDomicilio().setLocalidad("Godoy Cruz");
                persona1.getDomicilio().setProvincia("Mendoza");
            }

            try {
                Persona updatedPersona = personaService.update(persona1);
                System.out.println("Persona actualizada con éxito: " + updatedPersona);
            } catch (RuntimeException e) {
                System.err.println("Error al actualizar persona: " + e.getMessage());
            }

        // --- FIND ALL Operation ---
        System.out.println("\n*** Listing all Personas ***");
        try {
            List<Persona> allPersonas = personaService.findAll();
            if (allPersonas.isEmpty()) {
                System.out.println("No hay personas en la base de datos.");
            } else {
                System.out.println("Personas en la base de datos:");
                allPersonas.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error al listar personas: " + e.getMessage());
        }

        // --- Test invalid inputs (error handling) ---
        System.out.println("\n*** Testing invalid inputs for Persona creation/update ***");
        try {
            System.out.println("Attempting to create persona with null name (expected error):");
            personaService.create(new Persona(77,null, 25, new Domicilio(77,"Loc", "Prov")));
        } catch (IllegalArgumentException e) {
            System.err.println("Caught expected error: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to create persona with invalid age (expected error):");
            personaService.create(new Persona(55,"Test", 0, new Domicilio(55,"Loc", "Prov")));
        } catch (IllegalArgumentException e) {
            System.err.println("Caught expected error: " + e.getMessage());
        }
        
        try {
            System.out.println("Attempting to update persona with non-existent ID (expected error):");
            // Create a Persona object with an ID that definitely does not exist
            Persona invalidPersona = new Persona(9999, "Invalid Person", 30, new Domicilio(1, "Fake Loc", "Fake Prov"));
            personaService.update(invalidPersona);
        } catch (RuntimeException e) {
            System.err.println("Caught expected error: " + e.getMessage());
        }

        // --- DELETE Operation ---
        System.out.println("\n*** Deleting Persona ***");
            try {
                personaService.delete(persona1.getId());
                System.out.println("Persona eliminada con ID: " + persona1.getId());
            } catch (RuntimeException e) {
                System.err.println("Error al eliminar persona: " + e.getMessage());
            }
        
        System.out.println("\n*** Listing all Personas after deletion (should show remaining or be empty) ***");
        try {
            List<Persona> allPersonas = personaService.findAll();
            if (allPersonas.isEmpty()) {
                System.out.println("No hay personas en la base de datos después de eliminar.");
            } else {
                System.out.println("Remaining personas:");
                allPersonas.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error al listar personas: " + e.getMessage());
        }

        System.out.println("\n--- Testing Complete ---");
    }
}