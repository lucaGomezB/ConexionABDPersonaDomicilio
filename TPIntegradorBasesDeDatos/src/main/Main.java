// Main.java
package main; // You might want a dedicated 'main' package

import dao.DomicilioDAO;
import dao.PersonaDAO;
import java.sql.SQLException;
import model.Domicilio;
import model.Persona;
import service.PersonaService;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {
        DomicilioDAO domicilioDAO = new DomicilioDAO();
        PersonaDAO personaDAO = new PersonaDAO(domicilioDAO);
        PersonaService personaService = new PersonaService(personaDAO);

        System.out.println("--- Starting Persona Service Test ---");
        Persona persona1 = null;
        try {
            System.out.println("\n--- Creating a new Persona ---");
            Domicilio domicilio1 = new Domicilio(0, "Mendoza City", "Mendoza"); // ID 0 for new Domicilio
            Persona newPersona = new Persona(0, "Alice Smith", 30, domicilio1); // ID 0 for new Persona

            persona1 = personaService.create(newPersona);
            System.out.println("Created Persona: " + persona1);
            System.out.println("Associated Domicilio: " + persona1.getDomicilio());

        } catch (RuntimeException e) {
            System.err.println("Error creating persona: " + e.getMessage());
        }
        
        if (persona1 != null) {
            try {
                System.out.println("\n--- Finding Persona by ID: " + persona1.getId() + " ---");
                Persona foundPersona = personaService.findById(persona1.getId());
                if (foundPersona != null) {
                    System.out.println("Found Persona: " + foundPersona);
                    System.out.println("Found Domicilio: " + foundPersona.getDomicilio());
                } else {
                    System.out.println("Persona with ID " + persona1.getId() + " not found.");
                }
            } catch (RuntimeException e) {
                System.err.println("Error finding persona by ID: " + e.getMessage());
                e.printStackTrace();
            }

            // --- 3. Update an existing Persona ---
            try {
                System.out.println("\n--- Updating Persona ID: " + persona1.getId() + " ---");
                persona1.setNombre("Alicia Rodriguez");
                persona1.setEdad(31);
                // Modify domicilio too
                persona1.getDomicilio().setLocalidad("Godoy Cruz");
                persona1.getDomicilio().setProvincia("Mendoza"); // Still Mendoza

                Persona updatedPersona = personaService.update(persona1);
                System.out.println("Updated Persona: " + updatedPersona);
                System.out.println("Updated Domicilio: " + updatedPersona.getDomicilio());

            } catch (RuntimeException e) {
                System.err.println("Error updating persona: " + e.getMessage());
                e.printStackTrace();
            }

            // --- 4. Find All Personas ---
            try {
                System.out.println("\n--- Finding All Personas ---");
                List<Persona> allPersonas = personaService.findAll(); // Using BaseService.findAll()
                if (!allPersonas.isEmpty()) {
                    for (Persona p : allPersonas) {
                        System.out.println("  - " + p + " -> Domicilio: " + p.getDomicilio());
                    }
                } else {
                    System.out.println("No personas found in the database.");
                }
            } catch (RuntimeException e) {
                System.err.println("Error finding all personas: " + e.getMessage());
                e.printStackTrace();
            }

            // --- 5. Delete Persona ---
            try {
                System.out.println("\n--- Deleting Persona ID: " + persona1.getId() + " ---");
                personaService.delete(persona1.getId()); // Using BaseService.delete()
                System.out.println("Persona with ID " + persona1.getId() + " deleted successfully.");

                // Try to find the deleted persona to confirm
                Persona deletedCheck = personaService.findById(persona1.getId());
                if (deletedCheck == null) {
                    System.out.println("Confirmed: Persona ID " + persona1.getId() + " is no longer found.");
                } else {
                    System.out.println("Warning: Persona ID " + persona1.getId() + " still found after delete!");
                }

            } catch (RuntimeException e) {
                System.err.println("Error deleting persona: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("\nSkipping further tests as initial persona creation failed.");
        }

        System.out.println("\n--- Persona Service Test Finished ---");
    }
}