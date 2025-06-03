
package model;

/**
 *
 * @author lucaGomezB
 */
public class Persona {
    private int id;
    private String nombre;
    private int edad;
    private Domicilio domicilio;

    public Persona(int id, String nombre, int edad, Domicilio domicilio) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.domicilio = domicilio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    @Override
    public String toString() {
        return "Persona {\n\tid = " + id + "\n\tnombre = " + nombre + "\n\tedad = " + edad + "\n\tdomicilio = " + domicilio + "\n}\n";
    }
    
    
}
