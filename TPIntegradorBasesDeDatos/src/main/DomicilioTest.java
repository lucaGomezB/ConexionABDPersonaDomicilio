// Main.java (Modified to include Domicilio tests)
import config.DatabaseConnection;
import dao.DomicilioDAO;
import dao.PersonaDAO; // Keep if testing Persona, otherwise remove
import model.Domicilio;
import model.Persona; // Keep if testing Persona, otherwise remove
import service.DomicilioService;
import service.PersonaService; // Keep if testing Persona, otherwise remove

import java.util.List;
import java.util.Optional; 

public class DomicilioTest {
    public static void main(String[] args) {
        System.out.println("--- Starting Application Tests ---");

        // --- Setup for Persona (keep if you still want to test Persona) ---
        // DomicilioDAO domicilioDAO = new DomicilioDAO();
        // PersonaDAO personaDAO = new PersonaDAO(domicilioDAO); 
        // PersonaService personaService = new PersonaService(personaDAO);
        // System.out.println("\n--- Testing PersonaService CRUD Operations ---");
        // // ... (Your existing Persona tests here) ...
        // System.out.println("\n--- PersonaService Tests Complete ---");


        // --- New Test Section for DomicilioService ---
        System.out.println("\n--- Testing DomicilioService CRUD Operations ---");

        DomicilioDAO domicilioDAOForTest = new DomicilioDAO();
        DomicilioService domicilioService = new DomicilioService(domicilioDAOForTest);

        // --- CREATE Operation ---
        System.out.println("\n*** Creating a new Domicilio ***");
        Domicilio domicilio1 = new Domicilio(19,"Godoy Cruz", "Mendoza");
        try {
            domicilio1 = domicilioService.create(domicilio1);
            System.out.println("Domicilio creado con éxito: " + domicilio1);
        } catch (RuntimeException e) {
            System.err.println("Error al crear domicilio: " + e.getMessage());
        }

        // --- READ by ID Operation (using findById which returns T or throws) ---
        System.out.println("\n*** Reading Domicilio by ID (using findById) ***");
            try {
                Domicilio foundDomicilio = domicilioService.findById(domicilio1.getId());
                System.out.println("Domicilio encontrado por ID: " + foundDomicilio);
            } catch (RuntimeException e) {
                System.err.println("Error al encontrar domicilio por ID: " + e.getMessage());
            }

        // --- READ by ID Operation (using read which returns Optional<T>) ---
        System.out.println("\n*** Reading Domicilio by ID (using read - Optional) ***");
            try {
                Optional<Domicilio> optionalDomicilio = domicilioService.read(domicilio1.getId());
                if (optionalDomicilio.isPresent()) {
                    System.out.println("Domicilio encontrado por ID (Optional): " + optionalDomicilio.get());
                } else {
                    System.out.println("Domicilio no encontrado por ID (Optional): " + domicilio1.getId());
                }
            } catch (RuntimeException e) {
                System.err.println("Error al encontrar domicilio por ID (Optional): " + e.getMessage());
            }
        

        // --- UPDATE Operation ---
        System.out.println("\n*** Updating Domicilio ***");
            domicilio1.setLocalidad("Lujan de Cuyo");
            domicilio1.setProvincia("Mendoza"); // Still Mendoza, but ensure all fields are set

            try {
                Domicilio updatedDomicilio = domicilioService.update(domicilio1);
                System.out.println("Domicilio actualizado con éxito: " + updatedDomicilio);
            } catch (RuntimeException e) {
                System.err.println("Error al actualizar domicilio: " + e.getMessage());
            }
        

        // --- FIND ALL Operation ---
        System.out.println("\n*** Listing all Domicilios ***");
        try {
            List<Domicilio> allDomicilios = domicilioService.findAll();
            if (allDomicilios.isEmpty()) {
                System.out.println("No hay domicilios en la base de datos.");
            } else {
                System.out.println("Domicilios en la base de datos:");
                allDomicilios.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error al listar domicilios: " + e.getMessage());
        }

        // --- Test invalid inputs (error handling) ---
        System.out.println("\n*** Testing invalid inputs for Domicilio creation/update ***");
        try {
            System.out.println("Attempting to create domicilio with empty localidad (expected error):");
            domicilioService.create(new Domicilio(88,"", "Mendoza"));
        } catch (IllegalArgumentException e) {
            System.err.println("Caught expected error: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to update non-existent domicilio (expected error):");
            domicilioService.update(new Domicilio(9999, "Fake Loc", "Fake Prov"));
        } catch (RuntimeException e) {
            System.err.println("Caught expected error: " + e.getMessage());
        }

        // --- DELETE Operation ---
        System.out.println("\n*** Deleting Domicilio ***");
            try {
                domicilioService.delete(domicilio1.getId());
                System.out.println("Domicilio eliminado con ID: " + domicilio1.getId());
            } catch (RuntimeException e) {
                System.err.println("Error al eliminar domicilio: " + e.getMessage());
            }
        
        
        System.out.println("\n*** Listing all Domicilios after deletion (should be empty if only one was created) ***");
        try {
            List<Domicilio> allDomicilios = domicilioService.findAll();
            if (allDomicilios.isEmpty()) {
                System.out.println("No hay domicilios en la base de datos después de eliminar.");
            } else {
                System.out.println("Remaining domicilios:");
                allDomicilios.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error al listar domicilios: " + e.getMessage());
        }

        System.out.println("\n--- DomicilioService Tests Complete ---");
        System.out.println("\n--- All Application Tests Finished ---");
    }
}