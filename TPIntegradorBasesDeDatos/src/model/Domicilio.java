
package model;

/**
 *
 * @author lucaGomezB
 */
public class Domicilio {
    private int id;
    private String localidad;
    private String provincia;

    public Domicilio(int id, String localidad, String provincia) {
        this.id = id;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @Override
    public String toString() {
        return "Domicilio {\n\tid = " + id + "\n\tlocalidad = " + localidad + "\n\tprovincia = " + provincia + "\n}\n";
    }
    
    
}
